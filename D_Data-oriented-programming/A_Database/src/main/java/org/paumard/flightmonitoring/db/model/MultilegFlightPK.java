package org.paumard.flightmonitoring.db.model;

public final class MultilegFlightPK implements FlightPK {
    private String flightId;

    public MultilegFlightPK(String id) {
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
