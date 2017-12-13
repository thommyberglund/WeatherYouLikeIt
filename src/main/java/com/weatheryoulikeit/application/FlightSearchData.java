package com.weatheryoulikeit.application;

public class FlightSearchData implements SearchData{

    private String origin;
    private String startDate;
    private String endDate;
    private int tempMin;
    private int tempMax;
    private double priceMax;
    private int nAdults;
    private int nChildren;
    private int nInfants;

    public FlightSearchData() {}

    public FlightSearchData(String origin, String startDate, String endDate,
                            String tempMin, String tempMax, String priceMax,
                            String nAdults, String nChildren, String nInfants) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = Integer.parseInt(tempMin);
        this.tempMax = Integer.parseInt(tempMax);
        this.priceMax = Double.parseDouble(priceMax);
        this.nAdults = Integer.parseInt(nAdults);
        this.nChildren = Integer.parseInt(nChildren);
        this.nInfants = Integer.parseInt(nInfants);
    }

    public FlightSearchData(String origin, String startDate, String endDate,
                            int tempMin, int tempMax, double priceMax,
                            int nAdults, int nChildren, int nInfants) {
        this.origin = origin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.priceMax = priceMax;
        this.nAdults = nAdults;
        this.nChildren = nChildren;
        this.nInfants = nInfants;
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

    public void setnAdults(int nAdults) {
        this.nAdults = nAdults;
    }

    public void setnChildren(int nChildren) {
        this.nChildren = nChildren;
    }

    public void setnInfants(int nInfants) {
        this.nInfants = nInfants;
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

    public int getnAdults() {
        return nAdults;
    }

    public int getnChildren() {
        return nChildren;
    }

    public int getnInfants() {
        return nInfants;
    }
}
