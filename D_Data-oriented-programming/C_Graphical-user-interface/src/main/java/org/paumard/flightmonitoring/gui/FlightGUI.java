package org.paumard.flightmonitoring.gui;

import org.paumard.flightmonitoring.business.model.Flight;
import org.paumard.flightmonitoring.business.model.MultilegFlight;
import org.paumard.flightmonitoring.business.model.SimpleFlight;
import org.paumard.flightmonitoring.business.service.FlightGUIService;

public class FlightGUI implements FlightGUIService {

    public void displayFlight(Flight flight) {
        switch (flight) {
            case SimpleFlight simpleFlight -> System.out.println(
                    "Flight from " + simpleFlight.from().name() + " to " + simpleFlight.to().name() +
                    ": price is now " + SimpleFlight.price(simpleFlight).price());
            case MultilegFlight multilegFlight -> System.out.println(
                    "Flight from " + multilegFlight.from().name() + " to " + multilegFlight.to().name() +
                    " via " + multilegFlight.via().name() +
                    ": price is now " + MultilegFlight.price(multilegFlight).price());
        }
    }
}
