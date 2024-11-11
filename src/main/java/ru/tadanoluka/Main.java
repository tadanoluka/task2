package ru.tadanoluka;

import ru.tadanoluka.intersection.Intersection;
import ru.tadanoluka.util.QueueIncreaser;
import ru.tadanoluka.util.CliPrinter;
import ru.tadanoluka.util.TrafficSimulator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    public static void main(String[] args) {
        Intersection intersection = new Intersection();

        CliPrinter cliPrinter = new CliPrinter(intersection);
        QueueIncreaser queueIncreaser = new QueueIncreaser(intersection);
        TrafficSimulator trafficSimulator = new TrafficSimulator(intersection);

        intersection.start();

        executor.scheduleAtFixedRate(cliPrinter, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(queueIncreaser, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(trafficSimulator, 0, 1, TimeUnit.SECONDS);
    }
}