package com.weatheryoulikeit.application;

public class FlightSearchData {

    private String origin;
    private String startDate;
    private String endDate;
    private int tempMin;
    private int tempMax;

    public FlightSearchData(String origin, String startDate, String endDate, int tempMin, int tempMax) {
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

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }
}
