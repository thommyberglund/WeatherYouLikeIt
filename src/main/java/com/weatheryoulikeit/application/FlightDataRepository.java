package com.weatheryoulikeit.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Component
public class FlightDataRepository {

    @Autowired
    private DataSource dataSource;

    @Value("${apikey}")
    private String apikey;

    @Value("${weatherkey}")
    private String weatherkey;

    private Random rand = new Random();

    private double[] getLatLong(String isoCode) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT lat,long FROM GlobalAirportDatabase WHERE ISO3 = ?");) {
                pstmt.setString(1, isoCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    double[] returnData = {rs.getDouble(1), rs.getDouble(2)};
                    return returnData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getPrecipitation(String isoCode, int month) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT PRECIP FROM historical_climate_data WHERE COUNTRY = ? AND MONTH = ?");) {
                pstmt.setString(1, isoCode);
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

    private double getTemperature(String country, int month) {

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

    private List<String> getCountriesByTemperatureRange(int month, int tempMin, int tempMax) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNTRY FROM historical_temp_data WHERE MONTH = ? AND TEMP BETWEEN ? AND ?");) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, tempMin);
                pstmt.setInt(3, tempMax);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<String> countries = new ArrayList<>();
                    while (rs.next()) {
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

    private List<String> getCitiesInCountry(String country) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT CODE FROM [Academy_Projekt2].[dbo].[iata_codes] WHERE COUNTRY = ?")) {
                pstmt.setString(1, country);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<String> cities = new ArrayList<>();
                    while (rs.next()) {
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

    private String convertCitytoISO(String city) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT CODE FROM [Academy_Projekt2].[dbo].[iata_codes] WHERE City LIKE ?")) {
                pstmt.setString(1, city + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    String returnCity = rs.getString(1);
                    return returnCity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertISOtoCountryName(String isoCode) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT NAME FROM [Academy_Projekt2].[dbo].[country] WHERE ISO3 = ?")) {
                pstmt.setString(1, isoCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    String returnData = "";
                    while (rs.next()) {
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

    private String convertISOtoCityName(String isoCode) {
        try (Connection conn = dataSource.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT City FROM [Academy_Projekt2].[dbo].[iata_codes] WHERE Code = ?")) {
                pstmt.setString(1, isoCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";

    }

    private void cropCityName(FlightSearchData fsd) {
        String cityNameToCrop = fsd.getOrigin();
        int positionOfSlash = cityNameToCrop.lastIndexOf('/');
        cityNameToCrop = cityNameToCrop.substring(0, positionOfSlash);
        cityNameToCrop = convertCitytoISO(cityNameToCrop);
        fsd.setOrigin(cityNameToCrop);
    }

    public String getExternalFlights(FlightSearchData fsd) {

        JsonArray returnData = new JsonArray();
        int month = Integer.parseInt(fsd.getStartDate().substring(5, 7));
        List<String> filteredCountries = getCountriesByTemperatureRange(month, fsd.getTempMin(), fsd.getTempMax());
        List<String> selectedCountries = nUniqueRandomItems(filteredCountries, 10);

        for (String countryISO : selectedCountries) {

            String country = convertISOtoCountryName(countryISO);
            List<String> cityISO = getCitiesInCountry(country);
            if (cityISO.isEmpty()) {
                System.out.println("Found no cities in " + country);
                continue;
            }
            List<String> selectedCities = nUniqueRandomItems(cityISO, 1);

            if (fsd.getOrigin().length() > 3) {
                cropCityName(fsd);
            }

            for (String city : selectedCities) {
                String jsonBuilder = "";
                String weatherBuilder = "";
                String searchInput = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?" +
                        "apikey=" + apikey + "&origin=" + fsd.getOrigin() + "&destination=" + city +
                        "&departure_date=" + fsd.getStartDate() + "&return_date=" + fsd.getEndDate() +
                        "&number_of_results=5" + "&adults=" + fsd.getNoadults() + "&children=" + fsd.getnChildren() +
                        "&infants=" + fsd.getnInfants() + "&currency=EUR";
                double[] latLong = getLatLong(city);
                String weatherInput = "https://api.darksky.net/forecast/" + weatherkey + "/" + latLong[0]
                        + "," + latLong[1] + "?units=si&exclude=hourly,minutely,daily,flags,alerts";

                try {

                    URL url2 = new URL(weatherInput);

                    try (InputStream in = url2.openStream()) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String inputBuffer;
                        while ((inputBuffer = reader.readLine()) != null) {
                            weatherBuilder += inputBuffer;
                        }
                    }
                } catch (MalformedURLException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }

                try {

                    URL url = new URL(searchInput);

                    try (InputStream in = url.openStream()) {
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
                JsonObject weatherObject = parseWeather(weatherBuilder);
                jsonObject.addProperty("origin", fsd.getOrigin());
                jsonObject.addProperty("destination", convertISOtoCityName(city));
                jsonObject.addProperty("country", country);
                jsonObject.addProperty("temperature", getTemperature(countryISO, month));
                jsonObject.addProperty("precipitation", getPrecipitation(countryISO, month) / 30);
                jsonObject.addProperty("temperatureToday", weatherObject.get("temperature").toString());


                String price = jsonToStringNoQuotes(jsonObject.get("pricePerPerson"));
                if (Double.parseDouble(price) > fsd.getPriceMax()) {
                    //Skip this result
                    System.out.println("Price " + price + " > maxprice");
                    continue;
                }

                returnData.add(jsonObject);
            }
        }

        return sortFlightArray(returnData);
    }

    private String sortFlightArray(JsonArray jsonArray) {
        List<JsonObject> jsonValues = new ArrayList<>();
        JsonArray sortedJsonArray = new JsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            jsonValues.add(jsonArray.get(i).getAsJsonObject());
        }

        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            private static final String KEY_NAME = "price";

            @Override
            public int compare(JsonObject o1, JsonObject o2) {
                double valA = 0.0;
                double valB = 0.0;

                try {
                    valA = o1.get(KEY_NAME).getAsDouble();
                    valB = o2.get(KEY_NAME).getAsDouble();
                } catch (RuntimeException e) {
                    //do something
                }

                return Double.compare(valA, valB);
            }
        });

        for (int i = 0; i < jsonArray.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }

        return sortedJsonArray.toString();
    }

    private JsonObject parseWeather(String result) {
        JsonElement jelement = new JsonParser().parse(result);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject currently = jobject.getAsJsonObject("currently");
        JsonElement temp = currently.getAsJsonPrimitive("temperature");

        JsonObject jsonResult = new JsonObject();
        jsonResult.add("temperature", temp);

        return jsonResult;
    }

    private JsonObject parseAmadeusResult(String result) {
        JsonElement jelement = new JsonParser().parse(result);
        JsonObject jobject = jelement.getAsJsonObject();

        JsonElement currency = jobject.getAsJsonPrimitive("currency");

        JsonArray jarray = jobject.getAsJsonArray("results");
        JsonObject cheapestResult = jarray.get(0).getAsJsonObject();

        JsonObject fareResult = cheapestResult.get("fare").getAsJsonObject();
        JsonElement fareTotal = fareResult.getAsJsonPrimitive("total_price");
        JsonElement farePerPerson = fareResult.getAsJsonObject("price_per_adult")
                .getAsJsonPrimitive("total_fare");

        JsonObject outboundFlight = getFlightData(cheapestResult, "outbound");
        JsonObject inboundFlight = getFlightData(cheapestResult, "inbound");

        JsonObject jsonResult = new JsonObject();
        jsonResult.add("priceTotal", fareTotal);
        jsonResult.add("pricePerPerson", farePerPerson);
        jsonResult.add("currency", currency);
        jsonResult.add("outboundDepartureDate", outboundFlight.getAsJsonPrimitive("date"));
        jsonResult.add("outboundDepartureTime", outboundFlight.getAsJsonPrimitive("time"));
        jsonResult.add("outboundStops", outboundFlight.getAsJsonPrimitive("stops"));
        jsonResult.add("inboundDepartureDate", inboundFlight.getAsJsonPrimitive("date"));
        jsonResult.add("inboundDepartureTime", inboundFlight.getAsJsonPrimitive("time"));
        jsonResult.add("inboundStops", inboundFlight.getAsJsonPrimitive("stops"));

        return jsonResult;
    }

    private JsonObject getFlightData(JsonObject itinerary, String bound) {

        JsonArray flights = itinerary
                .getAsJsonArray("itineraries")
                .get(0).getAsJsonObject()
                .getAsJsonObject(bound)
                .getAsJsonArray("flights");

        JsonObject result = new JsonObject();

        int nStops = flights.size();
        result.add("stops", new JsonPrimitive(nStops));

        JsonPrimitive dateTime = flights.get(0).getAsJsonObject().getAsJsonPrimitive("departs_at");
        String dateTimeStr = jsonToStringNoQuotes(dateTime);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDateTime);
        String time = DateTimeFormatter.ofPattern("HH:mm").format(localDateTime);
        result.add("date", new JsonPrimitive(date));
        result.add("time", new JsonPrimitive(time));

        return result;
    }

    private String jsonToStringNoQuotes(JsonElement json) {
        return json.toString().replaceAll("\"", "");
    }

    public List<String> nUniqueRandomItems(List<String> list, int nItems) {
        List<String> listCopy = new ArrayList<>();
        for (String item : list) {
            listCopy.add(item);
        }

        List<String> randomList = new ArrayList<>();

        while (randomList.size() < nItems && listCopy.size() > 0) {
            randomList.add(listCopy.remove(rand.nextInt(listCopy.size())));
        }
        return randomList;
    }

    private String amadeusResult = "{  \"currency\" : \"USD\",  \"results\" : [ {    \"itineraries\" : [ {      \"outbound\" : {        \"flights\" : [ {          \"departs_at\" : \"2018-03-19T09:40\",          \"arrives_at\" : \"2018-03-19T19:00\",          \"origin\" : {            \"airport\" : \"FRA\",            \"terminal\" : \"2\"          },          \"destination\" : {            \"airport\" : \"DXB\",            \"terminal\" : \"3\"          },          \"marketing_airline\" : \"EK\",          \"operating_airline\" : \"EK\",          \"flight_number\" : \"44\",          \"aircraft\" : \"388\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"B\",            \"seats_remaining\" : 9          }        }, {          \"departs_at\" : \"2018-03-19T23:25\",          \"arrives_at\" : \"2018-03-20T05:35\",          \"origin\" : {            \"airport\" : \"DXB\",            \"terminal\" : \"3\"          },          \"destination\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"A\"          },          \"marketing_airline\" : \"EK\",          \"operating_airline\" : \"EK\",          \"flight_number\" : \"767\",          \"aircraft\" : \"77W\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"B\",            \"seats_remaining\" : 9          }        }, {          \"departs_at\" : \"2018-03-20T06:40\",          \"arrives_at\" : \"2018-03-20T07:35\",          \"origin\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"B\"          },          \"destination\" : {            \"airport\" : \"MSU\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"4Z\",          \"flight_number\" : \"8050\",          \"aircraft\" : \"ER3\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Q\",            \"seats_remaining\" : 8          }        } ]      }    } ],    \"fare\" : {      \"total_price\" : \"826.47\",      \"price_per_adult\" : {        \"total_fare\" : \"826.47\",        \"tax\" : \"372.47\"      },      \"restrictions\" : {        \"refundable\" : false,        \"change_penalties\" : true      }    }  }, {    \"itineraries\" : [ {      \"outbound\" : {        \"flights\" : [ {          \"departs_at\" : \"2018-03-19T16:05\",          \"arrives_at\" : \"2018-03-20T00:05\",          \"origin\" : {            \"airport\" : \"FRA\",            \"terminal\" : \"1\"          },          \"destination\" : {            \"airport\" : \"DOH\"          },          \"marketing_airline\" : \"QR\",          \"operating_airline\" : \"QR\",          \"flight_number\" : \"68\",          \"aircraft\" : \"77W\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"V\",            \"seats_remaining\" : 9          }        }, {          \"departs_at\" : \"2018-03-20T02:20\",          \"arrives_at\" : \"2018-03-20T10:05\",          \"origin\" : {            \"airport\" : \"DOH\"          },          \"destination\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"A\"          },          \"marketing_airline\" : \"QR\",          \"operating_airline\" : \"QR\",          \"flight_number\" : \"1363\",          \"aircraft\" : \"77W\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"V\",            \"seats_remaining\" : 9          }        }, {          \"departs_at\" : \"2018-03-20T15:00\",          \"arrives_at\" : \"2018-03-20T16:00\",          \"origin\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"B\"          },          \"destination\" : {            \"airport\" : \"MSU\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"4Z\",          \"flight_number\" : \"8062\",          \"aircraft\" : \"ER3\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Q\",            \"seats_remaining\" : 8          }        } ]      }    } ],    \"fare\" : {      \"total_price\" : \"839.37\",      \"price_per_adult\" : {        \"total_fare\" : \"839.37\",        \"tax\" : \"287.37\"      },      \"restrictions\" : {        \"refundable\" : true,        \"change_penalties\" : true      }    }  }, {    \"itineraries\" : [ {      \"outbound\" : {        \"flights\" : [ {          \"departs_at\" : \"2018-03-19T20:45\",          \"arrives_at\" : \"2018-03-20T08:30\",          \"origin\" : {            \"airport\" : \"FRA\",            \"terminal\" : \"1\"          },          \"destination\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"A\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"SA\",          \"flight_number\" : \"261\",          \"aircraft\" : \"346\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Q\",            \"seats_remaining\" : 9          }        }, {          \"departs_at\" : \"2018-03-20T09:40\",          \"arrives_at\" : \"2018-03-20T10:35\",          \"origin\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"B\"          },          \"destination\" : {            \"airport\" : \"MSU\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"4Z\",          \"flight_number\" : \"8052\",          \"aircraft\" : \"ER3\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"L\",            \"seats_remaining\" : 5          }        } ]      }    } ],    \"fare\" : {      \"total_price\" : \"978.84\",      \"price_per_adult\" : {        \"total_fare\" : \"978.84\",        \"tax\" : \"330.84\"      },      \"restrictions\" : {        \"refundable\" : false,        \"change_penalties\" : true      }    }  }, {    \"itineraries\" : [ {      \"outbound\" : {        \"flights\" : [ {          \"departs_at\" : \"2018-03-19T21:35\",          \"arrives_at\" : \"2018-03-20T06:25\",          \"origin\" : {            \"airport\" : \"FRA\",            \"terminal\" : \"1\"          },          \"destination\" : {            \"airport\" : \"ADD\",            \"terminal\" : \"2\"          },          \"marketing_airline\" : \"ET\",          \"operating_airline\" : \"ET\",          \"flight_number\" : \"707\",          \"aircraft\" : \"350\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"H\",            \"seats_remaining\" : 7          }        }, {          \"departs_at\" : \"2018-03-20T08:40\",          \"arrives_at\" : \"2018-03-20T13:05\",          \"origin\" : {            \"airport\" : \"ADD\",            \"terminal\" : \"2\"          },          \"destination\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"A\"          },          \"marketing_airline\" : \"ET\",          \"operating_airline\" : \"ET\",          \"flight_number\" : \"809\",          \"aircraft\" : \"350\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"M\",            \"seats_remaining\" : 7          }        }, {          \"departs_at\" : \"2018-03-20T15:00\",          \"arrives_at\" : \"2018-03-20T16:00\",          \"origin\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"B\"          },          \"destination\" : {            \"airport\" : \"MSU\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"4Z\",          \"flight_number\" : \"8062\",          \"aircraft\" : \"ER3\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Q\",            \"seats_remaining\" : 8          }        } ]      }    } ],    \"fare\" : {      \"total_price\" : \"1131.82\",      \"price_per_adult\" : {        \"total_fare\" : \"1131.82\",        \"tax\" : \"373.82\"      },      \"restrictions\" : {        \"refundable\" : true,        \"change_penalties\" : true      }    }  }, {    \"itineraries\" : [ {      \"outbound\" : {        \"flights\" : [ {          \"departs_at\" : \"2018-03-19T20:45\",          \"arrives_at\" : \"2018-03-20T08:30\",          \"origin\" : {            \"airport\" : \"FRA\",            \"terminal\" : \"1\"          },          \"destination\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"A\"          },          \"marketing_airline\" : \"LH\",          \"operating_airline\" : \"SA\",          \"flight_number\" : \"9544\",          \"aircraft\" : \"346\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Y\",            \"seats_remaining\" : 4          }        }, {          \"departs_at\" : \"2018-03-20T09:40\",          \"arrives_at\" : \"2018-03-20T10:35\",          \"origin\" : {            \"airport\" : \"JNB\",            \"terminal\" : \"B\"          },          \"destination\" : {            \"airport\" : \"MSU\"          },          \"marketing_airline\" : \"SA\",          \"operating_airline\" : \"4Z\",          \"flight_number\" : \"8052\",          \"aircraft\" : \"ER3\",          \"booking_info\" : {            \"travel_class\" : \"ECONOMY\",            \"booking_code\" : \"Y\",            \"seats_remaining\" : 9          }        } ]      }    } ],    \"fare\" : {      \"total_price\" : \"2682.61\",      \"price_per_adult\" : {        \"total_fare\" : \"2682.61\",        \"tax\" : \"401.61\"      },      \"restrictions\" : {        \"refundable\" : true,        \"change_penalties\" : true      }    }  } ]}";
}
