package com.weatheryoulikeit.application;

import java.time.LocalDate;

public interface FlightModel {

     String origin = "null";
     String destination = "null";
     String airline = "null";
     double price = 0;
     LocalDate departure_date = LocalDate.parse("1970-01-01");
     LocalDate return_date = LocalDate.parse("1970-01-01");
     String refUrl = "http://replaceme";
}
