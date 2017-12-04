package com.weatheryoulikeit.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class WebControllerTests {

    private WebController sut;

    @Before
    public void setup() {
        sut = new WebController();
    }

    @Test
    public void getFlightResults() {
        FlightData flightData = new FlightData();
        assertEquals("{\"flights\":[]}", sut.getFlightResults("ARN", "2018-01-01", "2018-01-05", 20, 30));
    }

}
