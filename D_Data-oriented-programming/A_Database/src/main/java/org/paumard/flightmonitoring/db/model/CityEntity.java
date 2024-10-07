package org.paumard.flightmonitoring.db.model;

public class CityEntity {
    private final String name;

    public CityEntity(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "City[name=" + name + "]";
    }
}
