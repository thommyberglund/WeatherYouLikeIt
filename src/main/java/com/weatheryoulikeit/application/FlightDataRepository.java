package com.weatheryoulikeit.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class FlightDataRepository {

    @Autowired
    private DataSource dataSource;

    @Value("${apikey}")
    private String apikey;

    private Random rand = new Random();

    public double getTemperature(String country, int month) {

        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT TEMP FROM historical_temp_data WHERE COUNTRY = ? AND MONTH = ?");) {
                pstmt.setString(1, country);
                pstmt.setInt(2, month);
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<String> getCountriesByTemperatureRange(int month, int tempMin, int tempMax) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNTRY FROM historical_temp_data WHERE MONTH = ? AND TEMP BETWEEN ? AND ?");) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, tempMin);
                pstmt.setInt(3, tempMax);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<String> countries = new ArrayList<>();
                    while(rs.next()) {
                        countries.add(rs.getString(1));
                    }
                    return countries;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getCitiesInCountry(String country) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT CODE FROM [Academy_Projekt2].[dbo].[iata_codes] WHERE COUNTRY = ?")) {
                pstmt.setString(1, country);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<String> cities = new ArrayList<>();
                    while(rs.next()) {
                         cities.add(rs.getString("CODE"));
                    }
                    return cities;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertISOtoCountryName(String isoCode) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT NAME FROM [Academy_Projekt2].[dbo].[country] WHERE ISO3 = ?")) {
                pstmt.setString(1, isoCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    String returnData = "";
                    while(rs.next()) {
                        returnData = rs.getString("name");
                    }
                    return returnData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String convertISOtoCityName(String isoCode) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT City FROM [Academy_Projekt2].[dbo].[iata_codes] WHERE Code = ?")) {
                pstmt.setString(1, isoCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    String returnData = "";
                    rs.next();
                    returnData = rs.getString(1);
                    return returnData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";

    }

    public String getExternalFlights(FlightSearchData fsd) {

        String urlReturnData = "[";
        int month = Integer.parseInt(fsd.getStartDate().substring(5,7));
        List<String> filteredCountries = getCountriesByTemperatureRange(month,fsd.getTempMin(),fsd.getTempMax());
        List<String> selectedCountries = nRandomItems(filteredCountries, 5);

        for(String countryISO : selectedCountries) {

            String country = convertISOtoCountryName(countryISO);
            List<String> cityISO = getCitiesInCountry(country);
            if (cityISO.isEmpty())
                continue;
            List<String> selectedCities = nRandomItems(cityISO, 1);

            for (String city : selectedCities) {
                String jsonBuilder = "";
                String searchInput = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?" +
                        "apikey=" + apikey + "&origin=" + fsd.getOrigin() + "&destination=" + city + "&departure_date=" +
                        "" + fsd.getStartDate() + "&number_of_results=10";
                try {

                    URL url = new URL(searchInput);

                    try(InputStream in = url.openStream()) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String inputBuffer;
                        while ((inputBuffer = reader.readLine()) != null) {
                            jsonBuilder += inputBuffer;
                        }

                    }
                } catch (MalformedURLException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                    continue;
                }
                JsonObject jsonObject = parseAmadeusResult(jsonBuilder);
                jsonObject.addProperty("origin", fsd.getOrigin());
                jsonObject.addProperty("destination", convertISOtoCityName(city));
                jsonObject.addProperty("temperature", getTemperature(countryISO, month));
                urlReturnData += jsonObject.toString() + ",";
            }
        }
        urlReturnData = removeLastChar(urlReturnData);
        urlReturnData += "]";
        return urlReturnData;
    }

    JsonObject parseAmadeusResult(String result) {
        JsonElement jelement = new JsonParser().parse(result);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("results");
        JsonObject firstResult = jarray.get(0).getAsJsonObject();
        String currency = jobject.getAsJsonPrimitive("currency").toString();

        JsonObject fareResult = firstResult.get("fare").getAsJsonObject();
        String fare = fareResult.get("total_price").toString();

        JsonArray flightsResult = firstResult
                .getAsJsonArray("itineraries")
                .get(0).getAsJsonObject()
                .getAsJsonObject("outbound")
                .getAsJsonArray("flights");
        JsonObject firstFlight = flightsResult.get(0).getAsJsonObject();
        String departsAt = firstFlight.getAsJsonPrimitive("departs_at").toString();
        String airline = firstFlight.getAsJsonPrimitive("marketing_airline").toString();

        JsonObject jsonResult = new JsonObject();
        jsonResult.addProperty("price", fare.replaceAll("\"",""));
        jsonResult.addProperty("currency", currency.replaceAll("\"",""));
        jsonResult.addProperty("airline", airline.replaceAll("\"",""));
        jsonResult.addProperty("departsAt", departsAt.replaceAll("\"",""));

        return jsonResult;
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

    private List<String> nRandomItems(List<String> list, int nItems) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < nItems; i++) {
            randomList.add(list.get(rand.nextInt(list.size())));
        }
        return randomList;
    }

}
