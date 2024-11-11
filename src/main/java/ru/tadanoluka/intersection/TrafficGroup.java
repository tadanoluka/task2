package ru.tadanoluka.intersection;

public enum TrafficGroup {
    H_CARS, V_CARS, PEDESTRIANS;

    private static final int MAX = TrafficGroup.values().length - 1;

    public boolean isAfter(TrafficGroup group) {
        return this.ordinal() - group.ordinal() == 1 || this.ordinal() - group.ordinal() == -MAX;
    }
}
