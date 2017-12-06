package com.weatheryoulikeit.application;

 import com.google.gson.Gson;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.List;

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

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate,tempMin,tempMax);

        return getExternalFlights(fsd);
    }

    @PostMapping(path="/getFlightResults", produces = "application/json")
    public String postFlightResults(@RequestParam(value="origin", required=false) String origin,
                                   @RequestParam(value="startDate", required=false) String startDate,
                                   @RequestParam(value="endDate", required=false) String endDate,
                                   @RequestParam(value="tempMin", required=false, defaultValue="0")int tempMin,
                                   @RequestParam(value="tempMax", required=false, defaultValue="0")int tempMax) {

        FlightSearchData fsd = new FlightSearchData(origin, startDate, endDate,tempMin,tempMax);

        return getExternalFlights(fsd);
    }
    public String getExternalFlights(FlightSearchData fsd) {
        String urlReturnData = "[";
        int month = Integer.parseInt(fsd.getStartDate().substring(5,7));
        List<String> filteredCountries = repo.getCountriesByTemperatureRange(month,fsd.getTempMin(),fsd.getTempMax());
        for(String country : filteredCountries) {
            country = repo.convertISOtoCountryName(country);
            List<String> cityISO = repo.convertCountrytoCity(country);
            for (String city : cityISO) {
                String jsonBuilder = "";
                String searchInput = "https://api.sandbox.amadeus.com/v1.2/flights/extensive-search" +
                        "?apikey=3t5NtG65HILsuQeEJqqC95xsA2WpArbF&origin=" + fsd.getOrigin() + "&destination=" + city + "&departure_date=" +
                        "" + fsd.getStartDate() + "--" + fsd.getEndDate() + "&aggregation_mode=DESTINATION&one-way=true";
                try {

                    URL url = new URL(searchInput);

                    InputStream in = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String inputBuffer;
                    while ((inputBuffer = reader.readLine()) != null) {
                        jsonBuilder += inputBuffer;
                    }
                } catch (MalformedURLException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
                urlReturnData += jsonBuilder + ",";
            }
        }
        //urlReturnData = removeLastChar(urlReturnData);
        urlReturnData += "]";
        return trimJson(urlReturnData);
    }

    private String trimJson(String urlReturnData) {
        urlReturnData = urlReturnData.replace("[","");
        urlReturnData = urlReturnData.replace("]","");
        urlReturnData = urlReturnData.replace(",  \"results\" :  {  ",",");
        //return removeLastChar(urlReturnData);
        return urlReturnData;
    }
    private String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

}
