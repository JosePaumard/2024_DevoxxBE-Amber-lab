package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.business.model.Flight;
import org.paumard.flightmonitoring.business.model.SimpleFlight;
import org.paumard.flightmonitoring.business.service.FlightGUIService;

public class FlightGUI implements FlightGUIService {

    public void displayFlight(Flight flight) {
        switch (flight) {
            case SimpleFlight simpleFlight -> System.out.println(
                    "Flight from " + simpleFlight.from().name() + " to " + simpleFlight.to().name() +
                    ": price is now " + SimpleFlight.price(simpleFlight));
        }
    }
}
