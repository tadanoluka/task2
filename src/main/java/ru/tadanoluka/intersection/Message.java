package ru.tadanoluka.intersection;


import ru.tadanoluka.intersection.lights.TrafficLightState;

import java.time.Duration;
import java.time.Instant;

public record Message(
        TrafficGroup trafficGroup,
        TrafficLightState state,
        Instant startsAt,
        Instant endsAt,
        double averageQueue,
        Duration greenDuration
) {
}
