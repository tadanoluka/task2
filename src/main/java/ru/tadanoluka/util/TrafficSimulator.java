package ru.tadanoluka.util;

import ru.tadanoluka.intersection.Intersection;
import ru.tadanoluka.intersection.lights.TrafficLight;
import ru.tadanoluka.intersection.lights.TrafficLightState;

import java.util.List;


public class TrafficSimulator implements Runnable {
    private final List<TrafficLight> trafficLights;


    public TrafficSimulator(Intersection intersection) {
        this.trafficLights = intersection.getTrafficLights();
    }

    @Override
    public void run() {
        trafficLights.forEach(this::decreaseQueue);
    }

    private void decreaseQueue(TrafficLight trafficLight) {
        if (TrafficLightState.GREEN.equals(trafficLight.getCurrentState())) {
            trafficLight.reduceQueue(1);
        }
    }
}
