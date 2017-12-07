package com.weatheryoulikeit.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Autowired
    FlightDataRepository repo = new FlightDataRepository();

    @GetMapping(path="/getFlightResults", produces = "application/json")
    public String getFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) {

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate, tempMin, tempMax);

        return repo.getExternalFlights(fsd);
    }

    @PostMapping(path="/search", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    String searchFlights(@RequestBody FlightSearchData fsd) {
        return repo.getExternalFlights(fsd);
    }
  
    @PostMapping(path="/getFlightResults", produces = "application/json")
    public String postFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) {

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate,tempMin,tempMax);

        return repo.getExternalFlights(fsd);
    }

}
