package com.weatheryoulikeit.application;


import java.time.LocalDate;

public class Flight implements FlightModel {

    private String origin;
    private String destination;
    private String airline;
    private double price;
    private String refUrl;
    LocalDate departure_date;
    LocalDate return_date;

    public Flight(String origin, String destination, String company, double price, String refUrl) {
        this.destination = destination;
        this.origin = origin;
        this.airline = company;
        this.price = price;
        this.refUrl = refUrl;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public LocalDate getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(LocalDate departure_date) {
        this.departure_date = departure_date;
    }

    public LocalDate getReturn_date() {
        return return_date;
    }

    public void setReturn_date(LocalDate return_date) {
        this.return_date = return_date;
    }
}