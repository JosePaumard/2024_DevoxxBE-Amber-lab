package org.paumard.flightmonitoring.db.model;

public class PlaneEntity {
    private String type;

    public PlaneEntity(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    @Override
    public String toString() {
        return "Plane[type=" + type + "]";
    }
}
