package com.weatheryoulikeit.application;

 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLConnection;
 import java.time.LocalDate;

@RestController
public class WebController {


    @GetMapping("/getFlightResults")
    public String getFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) {

        FlightSearchData fsd = new FlightSearchData(origin, LocalDate.parse(startDate), LocalDate.parse(endDate),tempMin,tempMax);

        FlightData flightData = new FlightData();
        flightData.addFlight(new Flight("ARN","LAX","SAS",2300,"http://"));

        //return flightData.toJson();

        return getExternalFlights(fsd);
    }

    public String getExternalFlights(FlightSearchData fsd) {
        String urlReturnData = "";
        String searchInput = "https://api.sandbox.amadeus.com/v1.2/flights/extensive-search" +
                "?apikey=3t5NtG65HILsuQeEJqqC95xsA2WpArbF&origin=LAX&destination=LON&departure_date=" +
                ""+ fsd.getStartDate() +"--"+ fsd.getEndDate() +"";
        try {
            URL url = new URL(searchInput);

            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String inputBuffer;
            while((inputBuffer = reader.readLine()) != null) {
                urlReturnData +=inputBuffer;
            }
        }
        catch (MalformedURLException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return urlReturnData;
    }
}
