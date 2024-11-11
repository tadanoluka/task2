package ru.tadanoluka.util;

import lombok.Setter;
import ru.tadanoluka.intersection.Intersection;
import ru.tadanoluka.intersection.TrafficGroup;
import ru.tadanoluka.intersection.lights.TrafficLight;

import java.util.List;
import java.util.Random;

public class QueueIncreaser implements Runnable {
    private static final int MIN_QUEUE_ADD_DEFAULT = 0;
    private static final int MAX_QUEUE_ADD_DEFAULT = 2;

    private static final int MIN_QUEUE_ADD_AUGMENTED = 1;
    private static final int MAX_QUEUE_ADD_AUGMENTED = 4;

    private final List<TrafficLight> trafficLights;
    private final Random random = new Random();

    @Setter
    private TrafficGroup groupWithHighTraffic;

    public QueueIncreaser(Intersection intersection) {
        this.trafficLights = intersection.getTrafficLights();
    }

    @Override
    public void run() {
        increaseQueue();
    }

    private void increaseQueue() {
        trafficLights.forEach(this::randomQueueIncrease);
    }

    private void randomQueueIncrease(TrafficLight trafficLight) {
        if (!trafficLight.getTrafficGroup().equals(groupWithHighTraffic)) {
            trafficLight.increaseQueue(random.nextInt(MIN_QUEUE_ADD_DEFAULT, MAX_QUEUE_ADD_DEFAULT + 1));
        } else {
            trafficLight.increaseQueue(random.nextInt(MIN_QUEUE_ADD_AUGMENTED, MAX_QUEUE_ADD_AUGMENTED + 1));
        }
    }

}
