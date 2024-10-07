package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.db.model.FlightEntity;

public class FlightGUI {

    public static FlightGUI getInstance() {
        return new FlightGUI();
    }

    public void displayFlight(FlightEntity flightEntity) {
        System.out.println(
                "Flight from " + flightEntity.from().name() + " to " + flightEntity.to().name() +
                ": price is now " + flightEntity.price().price());
    }
}
