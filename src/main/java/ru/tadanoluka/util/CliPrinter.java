package ru.tadanoluka.util;

import lombok.RequiredArgsConstructor;
import ru.tadanoluka.intersection.Intersection;

@RequiredArgsConstructor
public class CliPrinter implements Runnable {

    private final Intersection intersection;

    @Override
    public void run() {
        intersection.getTrafficLights().forEach(System.out::println);
        System.out.println("____________________________________________");
    }
}
