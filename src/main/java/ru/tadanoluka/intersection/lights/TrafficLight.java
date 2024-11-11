package ru.tadanoluka.intersection.lights;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tadanoluka.intersection.Intersection;
import ru.tadanoluka.intersection.Message;
import ru.tadanoluka.intersection.TrafficGroup;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class TrafficLight implements Runnable {
    private static final Duration YELLOW_DURATION = Duration.ofSeconds(2);
    private static final Duration MIN_GREEN_DURATION = Duration.ofSeconds(5);
    private static final Duration MAX_GREEN_DURATION = Duration.ofSeconds(30);

    @EqualsAndHashCode.Include
    private final UUID id = UUID.randomUUID();
    private final Intersection intersection;
    private final boolean isLeader;
    private final TrafficGroup trafficGroup;
    private final Map<TrafficGroup, Double> averageQueueMap = new HashMap<>();
    private final Queue<Message> messages = new LinkedList<>();

    private TrafficLightState currentState = TrafficLightState.RED;
    private Instant currentStateExpiresAt = Instant.now();
    private TrafficLightState nextState;
    private Instant nextStateStartsAt;
    private Instant nextStateEndsAt;
    private Duration greenDuration = MIN_GREEN_DURATION;
    private int queueSize = 0;


    public abstract TrafficLightType getType();

    public void reduceQueue(int value) {
        queueSize -= value;
        if (queueSize < 0) {
            queueSize = 0;
        }
    }

    public void increaseQueue(int value) {
        queueSize += value;
    }

    public void receiveMessage(Message message) {
        messages.offer(message);
    }

    @Override
    public String toString() {
        return "ID: '%s' | Group: %11s | Queue size: %4d | State: %6s | Green Duration: %3d"
                .formatted(id, trafficGroup, queueSize, currentState, greenDuration.toSeconds());
    }

    @Override
    public void run() {
        handleMessages();
        handleTime();
    }

    private void handleTime() {
        if (currentStateExpiresAt.isBefore(Instant.now())) {
            if (nextState != null) {
                if (nextStateStartsAt.isBefore(Instant.now())) {
                    currentState = nextState;
                    nextState = null;
                    currentStateExpiresAt = nextStateEndsAt;
                    calculateAverageQueue();
                    sendStateToOtherGroup();
                }
            } else if (currentState == TrafficLightState.GREEN || currentState == TrafficLightState.YELLOW) {
                sendStateToGroup(getNextState());
            }
        }
    }

    private void handleMessages() {
        while (!messages.isEmpty()) {
            Message currentMessage = messages.poll();
            if (currentMessage == null) {
                return;
            }

            if (trafficGroup.equals(currentMessage.trafficGroup())) {
                handleMessageFromLeader(currentMessage);
            } else if (isLeader) {
                handleMessageFromOtherGroups(currentMessage);
            }
        }
    }

    private void handleMessageFromLeader(Message message) {
        nextState = message.state();
        nextStateStartsAt = message.startsAt();
        nextStateEndsAt = message.endsAt();
        greenDuration = message.greenDuration();
    }

    private void handleMessageFromOtherGroups(Message message) {
        if (message.averageQueue() >= 0) {
            averageQueueMap.put(message.trafficGroup(), message.averageQueue());
        }
        if (TrafficLightState.RED.equals(message.state()) && trafficGroup.isAfter(message.trafficGroup())) {
            changeToGreen();
        }
    }

    public void changeToGreen() {
        sendStateToGroup(TrafficLightState.GREEN);
    }

    private void sendStateToGroup(TrafficLightState state) {
        if (isLeader) {
            calculateGreenDuration();
            Instant timeFrom = Instant.now().isAfter(currentStateExpiresAt) ? Instant.now() : currentStateExpiresAt;
            Instant startTime = timeFrom.plus(Duration.ofSeconds(1));
            Duration duration = switch (state) {
                case GREEN -> greenDuration;
                case YELLOW -> YELLOW_DURATION;
                default -> Duration.ZERO;
            };
            Instant entTime = startTime.plus(duration);
            double averageQueue = averageQueueMap.getOrDefault(trafficGroup, -1.0);
            Message message = new Message(trafficGroup, state, startTime, entTime, averageQueue, greenDuration);

            intersection.getTrafficLights().stream()
                    .filter(trafficLight ->
                            trafficLight.trafficGroup.equals(trafficGroup))
                    .forEach(trafficLight -> trafficLight.receiveMessage(message));
        }
    }

    private void sendStateToOtherGroup() {
        if (isLeader) {
            double averageQueue = averageQueueMap.getOrDefault(trafficGroup, -1.0);
            Message message = new Message(trafficGroup, currentState, Instant.now().minus(Duration.ofSeconds(1)),
                    currentStateExpiresAt, averageQueue, greenDuration);
            intersection.getTrafficLights().stream()
                    .filter(trafficLight -> !trafficLight.trafficGroup.equals(trafficGroup))
                    .forEach(trafficLight -> trafficLight.receiveMessage(message));
        }
    }

    private TrafficLightState getNextState() {
        return switch (currentState) {
            case RED -> TrafficLightState.GREEN;
            case GREEN -> getType().equals(TrafficLightType.CAR) ? TrafficLightState.YELLOW : TrafficLightState.RED;
            case YELLOW -> TrafficLightState.RED;
        };
    }

    private void calculateAverageQueue() {
        if (isLeader && currentState == TrafficLightState.GREEN) {
            OptionalDouble averageQueueOptional = intersection.getTrafficLights().stream()
                    .filter(trafficLight -> trafficLight.trafficGroup == trafficGroup)
                    .mapToInt(TrafficLight::getQueueSize)
                    .average();
            if (averageQueueOptional.isPresent()) {
                averageQueueMap.put(trafficGroup, averageQueueOptional.getAsDouble());
            }
        }
    }

    private void calculateGreenDuration() {
        if (averageQueueMap.values().size() == 3) {
            double avgQueueForThisGroup = averageQueueMap.get(trafficGroup);
            double totalQueueSize = averageQueueMap.values().stream().mapToDouble(Double::doubleValue).sum();

            Duration newDuration = Duration.ofSeconds((long) (avgQueueForThisGroup / totalQueueSize *
                    (MAX_GREEN_DURATION.toSeconds() - MIN_GREEN_DURATION.toSeconds()))).plus(MIN_GREEN_DURATION);

            if (getType() == TrafficLightType.CAR) {
                newDuration = newDuration.plus(YELLOW_DURATION);
            }

            greenDuration = newDuration;
        }
    }
}
