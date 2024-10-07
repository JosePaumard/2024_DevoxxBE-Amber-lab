package org.paumard.flightmonitoring.db.model;

public final class SimpleFlightPK implements FlightPK {
    private String flightId;

    public SimpleFlightPK(String id) {
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
