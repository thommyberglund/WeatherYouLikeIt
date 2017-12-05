package com.weatheryoulikeit.application;

import java.time.LocalDate;

public class FlightSearchData {

    private String origin;
    private LocalDate startDate;
    private LocalDate endDate;
    private int tempMin;
    private int tempMax;

    public FlightSearchData(String origin, LocalDate startDate, LocalDate endDate, int tempMin, int tempMax) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return "LON"; // Placeholder until weatherdb returns possible destinations
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }
}
