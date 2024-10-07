package org.paumard.flightmonitoring.business.model;

public sealed interface FlightID
        permits SimpleFlightID, MultilegFlightID {
}
