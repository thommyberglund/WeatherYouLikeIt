package com.weatheryoulikeit.application;

import java.time.LocalDate;

public class Flight implements FlightModel{

    private String origin;
    private String destination;
    private String company;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private String refUrl;

    public Flight(String origin, String destination, String company, double price, LocalDate startDate, LocalDate endDate, String refUrl) {
        this.destination = destination;
        this.origin = origin;
        this.company = company;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.refUrl = refUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;

        if (Double.compare(flight.price, price) != 0) return false;
        if (!origin.equals(flight.origin)) return false;
        if (!destination.equals(flight.destination)) return false;
        if (!company.equals(flight.company)) return false;
        if (!startDate.equals(flight.startDate)) return false;
        if (!endDate.equals(flight.endDate)) return false;
        return refUrl.equals(flight.refUrl);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = origin.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + company.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + refUrl.hashCode();
        return result;
    }

    public String getDestination() {

        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }
}
