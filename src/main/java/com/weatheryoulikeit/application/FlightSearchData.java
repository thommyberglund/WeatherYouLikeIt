package com.weatheryoulikeit.application;

public class FlightSearchData {

    private String origin;
    private String startDate;
    private String endDate;
    private int tempMin;
    private int tempMax;
    private double priceMax;

    public FlightSearchData() {}

    public FlightSearchData(String origin, String startDate, String endDate,
                            String tempMin, String tempMax, String priceMax) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = Integer.parseInt(tempMin);
        this.tempMax = Integer.parseInt(tempMax);
        this.priceMax = Double.parseDouble(priceMax);
    }

    public FlightSearchData(String origin, String startDate, String endDate,
                            String tempMin, String tempMax) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = Integer.parseInt(tempMin);
        this.tempMax = Integer.parseInt(tempMax);
        this.priceMax = Double.MAX_VALUE;
    }

    public FlightSearchData(String origin, String startDate, String endDate,
                            int tempMin, int tempMax) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.priceMax = Double.MAX_VALUE;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public String getOrigin() {
        return origin;
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

    public double getPriceMax() {
        return priceMax;
    }
}
