package org.paumard.flightmonitoring.business.service;

import org.paumard.flightmonitoring.business.model.Flight;
import org.paumard.flightmonitoring.business.model.FlightID;

public interface DBService {
    Flight fetchFlight(FlightID flightID);
}