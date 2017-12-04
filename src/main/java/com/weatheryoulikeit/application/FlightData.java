package com.weatheryoulikeit.application;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FlightData {
    private List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightData that = (FlightData) o;

        return flights.equals(that.flights);
    }

    public String toJson() {
        Gson gson = new Gson();
        String flightDataJson = gson.toJson(this.flights);
        return flightDataJson;
    }
}
