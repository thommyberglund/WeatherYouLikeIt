package com.weatheryoulikeit.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebControllerTests {

    private WebController sut;
    private FlightSearchData fsd;

    @Autowired
    FlightDataRepository fdr;

    @Before
    public void setup() {
        sut = new WebController();
        fsd = new FlightSearchData("LAX",LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-04"),20,40);
    }

    @Test
    public void getFlightResults() {
       assertEquals("{  \"origin\" : \"LAX\",  \"currency\" : \"USD\",  \"destination\" : \"LON\",    \"departure_date\" : \"2018-01-03\",    \"price\" : \"388.01\",    \"airline\" : \"WW\"  } ", sut.getFlightResults("LAX", "2018-01-01", "2018-01-04", 20, 30));
    }

    @Test
    public void getTemperatureFromDatabase() {
        try {
            double temp = fdr.getTemperature("ALB", 1);
            assertEquals(2.02, temp, 0.01);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCountriesByTemperatureFromDatabase() {
        List<String> correctCountries = new ArrayList<>();
        correctCountries.add("AUS");
        correctCountries.add("PRY");
        List<String> countries = fdr.getCountriesByTemperatureRange(1, 27, 30);
        assertEquals(correctCountries, countries);
    }

    @Test
    public void getCityByISOfromDatabase() {
        assertEquals("Frankfurt/Main Int'l Airport", fdr.convertISOtoName("FRA"));
    }

}


