package com.weatheryoulikeit.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Autowired
    FlightDataRepository repo = new FlightDataRepository();

    @PostMapping(path="/search", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    String searchFlights(@RequestBody FlightSearchData fsd){
        return repo.getExternalFlights(fsd);
    }
}
