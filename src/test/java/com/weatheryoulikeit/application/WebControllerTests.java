package com.weatheryoulikeit.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class WebControllerTests {

    private WebController sut;
    private FlightSearchData fsd;

    @Before
    public void setup() {
        sut = new WebController();
        fsd = new FlightSearchData("LAX",LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-04"),20,40);
    }

    @Test
    public void getFlightResults() {
       assertEquals("", sut.getFlightResults("LAX", "2018-01-01", "2018-01-04", 20, 30));
    }
/*    @Test
    public void getFlightData() {
        assertEquals("", sut.getExternalFlights(fsd));
    }*/
}
