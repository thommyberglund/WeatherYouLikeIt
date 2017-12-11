package com.weatheryoulikeit.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Autowired
    FlightDataRepository repo = new FlightDataRepository();

    String jsonArray = "";

    @Component
    public class ApplicationStartup
            implements ApplicationListener<ApplicationReadyEvent> {

        /**
         * This event is executed as late as conceivably possible to indicate that
         * the application is ready to service requests.
         */
        @Override
        public void onApplicationEvent(final ApplicationReadyEvent event) {

            jsonArray = repo.populateJsonArray();

            return;
        }

    }

    @GetMapping(path="/getjson", produces = "application/json")
    public String getJson() {
        return jsonArray;
    }

    @GetMapping(path="/getFlightResults", produces = "application/json")
    public String getFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) throws InterruptedException {

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate, tempMin, tempMax);

        return repo.getExternalFlights(fsd);
    }

    @PostMapping(path="/search", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    String searchFlights(@RequestBody FlightSearchData fsd) throws InterruptedException {
        return repo.getExternalFlights(fsd);
    }
  
    @PostMapping(path="/getFlightResults", produces = "application/json")
    public String postFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) throws InterruptedException {

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate,tempMin,tempMax);

        return repo.getExternalFlights(fsd);
    }



}
