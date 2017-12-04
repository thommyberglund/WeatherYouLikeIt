package com.weatheryoulikeit.application;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class WebController {

    public FlightData getFlightResults(String origin, String startDate, String endDate, int tempMin, int tempMax) {
        FlightData flightData = new FlightData();
//        flightData.addFlight(new Flight(origin, "CDG", "SAS", 25000.0,
//                LocalDate.parse(startDate), LocalDate.parse(endDate), "www.expedia.com"));
        return flightData;
    }
}
