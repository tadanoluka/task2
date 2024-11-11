package ru.tadanoluka.intersection;

import lombok.Getter;
import ru.tadanoluka.intersection.lights.CarTrafficLight;
import ru.tadanoluka.intersection.lights.PedestrianTrafficLight;
import ru.tadanoluka.intersection.lights.TrafficLight;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Intersection {
    @Getter
    private final List<TrafficLight> trafficLights = new ArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(12);

    {
        trafficLights.add(new CarTrafficLight(this, true, TrafficGroup.H_CARS));
        trafficLights.add(new CarTrafficLight(this, false, TrafficGroup.H_CARS));
        trafficLights.add(new CarTrafficLight(this, true, TrafficGroup.V_CARS));
        trafficLights.add(new CarTrafficLight(this, false, TrafficGroup.V_CARS));

        trafficLights.add(new PedestrianTrafficLight(this, true, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
        trafficLights.add(new PedestrianTrafficLight(this, false, TrafficGroup.PEDESTRIANS));
    }

    public void start() {
        trafficLights.forEach(trafficLight ->
                executor.scheduleAtFixedRate(trafficLight, 0, 20_000, TimeUnit.NANOSECONDS)
        );
        TrafficLight firstLeader = trafficLights.stream().filter(TrafficLight::isLeader).findFirst().orElseThrow();
        firstLeader.changeToGreen();
    }
}
