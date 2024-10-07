package org.paumard.flightmonitoring.db.model;

public class FlightPK {
    private String flightId;

    public FlightPK(String id) {
        this.flightId = id;
    }

    public String flightId() {
        return this.flightId;
    }

    @Override
    public String toString() {
        return "FlightID[flightId=" + flightId + "]";
    }
}
