package org.paumard.flightmonitoring;

import org.paumard.flightmonitoring.business.FlightMonitoring;
import org.paumard.flightmonitoring.business.model.MultilegFlightID;
import org.paumard.flightmonitoring.business.model.SimpleFlightID;
import org.paumard.flightmonitoring.business.service.DBService;
import org.paumard.flightmonitoring.business.service.FlightGUIService;
import org.paumard.flightmonitoring.business.service.PriceMonitoringService;
import org.paumard.flightmonitoring.db.FlightDBService;
import org.paumard.flightmonitoring.gui.FlightGUI;
import org.paumard.flightmonitoring.pricemonitoring.FlightPriceMonitoringService;

public class Main {

    public static void main(String[] args) {

        DBService dbService =
                new FlightDBService();
        FlightGUIService guiService =
                new FlightGUI();
        PriceMonitoringService monitoringService =
                new FlightPriceMonitoringService();
        var flightMonitoring =
                new FlightMonitoring(
                        dbService,
                        guiService,
                        monitoringService);

        var f1 = new SimpleFlightID("PaAt");
        var f2 = new SimpleFlightID("AmNY");
        var f3 = new MultilegFlightID("LoPaMi");
        var f4 = new MultilegFlightID("FrLoWa");

        flightMonitoring.followFlight(f1);
        flightMonitoring.followFlight(f2);
        flightMonitoring.followFlight(f3);
        flightMonitoring.followFlight(f4);

        flightMonitoring.monitorFlight(f1);
        flightMonitoring.monitorFlight(f2);
        flightMonitoring.monitorFlight(f3);
        flightMonitoring.monitorFlight(f4);

        monitoringService.updatePrices();
        flightMonitoring.launchDisplay();

        while (true) {

        }
    }
}