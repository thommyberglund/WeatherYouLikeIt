package com.weatheryoulikeit.application;

import java.time.LocalDate;

public interface FlightModel {

     String origin = "null";
     String destination = "null";
     String company = "null";
     double price = 0;
     LocalDate startDate = LocalDate.parse("1970-01-01");
     LocalDate endDate = LocalDate.parse("1970-01-01");
     String refUrl = "http://replaceme";
}
