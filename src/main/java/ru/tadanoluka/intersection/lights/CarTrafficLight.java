package ru.tadanoluka.intersection.lights;

import ru.tadanoluka.intersection.Intersection;
import ru.tadanoluka.intersection.TrafficGroup;

public class CarTrafficLight extends TrafficLight {
    private static final TrafficLightType TRAFFIC_LIGHT_TYPE = TrafficLightType.CAR;

    public CarTrafficLight(Intersection intersection, boolean isLeader, TrafficGroup trafficGroup) {
        super(intersection, isLeader, trafficGroup);
    }

    @Override
    public TrafficLightType getType() {
        return TRAFFIC_LIGHT_TYPE;
    }
}
