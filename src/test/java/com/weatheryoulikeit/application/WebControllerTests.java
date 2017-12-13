package com.weatheryoulikeit.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

    @Value("${apikey}")
    private String apikey;

    @Before
    public void setup() {
        sut = new WebController();
        fsd = new FlightSearchData("LAX", "2018-01-01", "2018-01-04", 20, 40, 1500, 2, 0, 0);

    }
/*
    @Test
    public void getWeather() throws InterruptedException {
        assertEquals("",fdr.parseWeather("{\"latitude\":25.907,\"longitude\":-97.426,\"timezone\":\"America/Chicago\",\"currently\":{\"time\":1513169526,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"nearestStormDistance\":1588,\"nearestStormBearing\":358,\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":7.36,\"apparentTemperature\":6.27,\"dewPoint\":4.77,\"humidity\":0.84,\"pressure\":1022.34,\"windSpeed\":1.83,\"windGust\":1.79,\"windBearing\":288,\"cloudCover\":0.01,\"uvIndex\":0,\"visibility\":16.09,\"ozone\":259.42},\"offset\":-6}"));
    }*/

/*    @Test
    public void getTemperatureFromDatabase() {
        double temp = fdr.getTemperature("ALB", 1);
        assertEquals(2.02, temp, 0.01);
    }*/
/*
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
        assertEquals("Frankfurt/Main Int'l Airport", fdr.getCitiesInCountry("FRA"));
    }

    @Test
    public void testTempRange() {
        int month = Integer.parseInt(fsd.getStartDate().substring(6, 7));
        List<String> filteredCountries = fdr.getCountriesByTemperatureRange(month, fsd.getTempMin(), fsd.getTempMax());
        assertEquals("", filteredCountries);
    }

    @Test
    public void testISOtoCountryName() {
        assertEquals("Belgium", fdr.convertISOtoCountryName("BEL"));
    }

    @Test
    public void testISOtoCityName() {
        assertEquals("Belgium", fdr.convertISOtoCityName("BZE"));
    }

    @Test
    public void convertCounrytoCity() {
        assertEquals("", fdr.getCitiesInCountry("Poland"));
    }*/

    @Test
    public void testISOConvert() {
        String result = fdr.getExternalFlights(fsd);
        System.out.println(result);
        assertEquals("", result);
    }

    @Test
    public void testAmadeusAPI() {
        String jsonBuilder = "";
        String searchInput = "https://api.sandbox.amadeus.com/v1.2/flights/extensive-search" +
                "?apikey=" + apikey + "&origin=" + fsd.getOrigin() + "&destination=LON&departure_date=" +
                "" + fsd.getStartDate() + "--" + fsd.getEndDate() + "&aggregation_mode=DESTINATION&one-way=true";
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
        }
        String urlReturnData = jsonBuilder + ",";
    }

/*    @Test
    public void testAmadeusParser() {
        JsonObject jsonObject = fdr.parseAmadeusResult(amadeusResult);
        System.out.println(jsonObject.toString());
    }*/

/*    @Test
    public void testPrecip() {
        assertEquals("",fdr.getPrecipitation("AFG",1)/30);
    }*/

/*    @Test
    public void testCitytoISO() {
        assertEquals("STO",fdr.convertCitytoISO("Stockholm"));
    }*/

/*    @Test
    public void testLatLong() {
        double returnData[] = new double[2];
        returnData = fdr.getLatLong("LWR");
        assertEquals("", returnData[0] );
    }*/

/*    @Test
    public void testWeatherJson() {
        assertEquals("",fdr.parseWeather("{\"latitude\":25.907,\"longitude\":-97.426,\"timezone\":\"America/Chicago\",\"currently\":{\"time\":1513169526,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"nearestStormDistance\":1588,\"nearestStormBearing\":358,\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":7.36,\"apparentTemperature\":6.27,\"dewPoint\":4.77,\"humidity\":0.84,\"pressure\":1022.34,\"windSpeed\":1.83,\"windGust\":1.79,\"windBearing\":288,\"cloudCover\":0.01,\"uvIndex\":0,\"visibility\":16.09,\"ozone\":259.42},\"offset\":-6}"));
    }*/

    @Test
    public void parseOneAmadeusFlight() {
        JsonElement jelement = new JsonParser().parse(amadeusResult);
        JsonObject jobject = jelement.getAsJsonObject();

        JsonArray jarray = jobject.getAsJsonArray("results");
        JsonObject cheapestResult = jarray.get(0).getAsJsonObject();

        JsonObject flight = fdr.getFlightData(cheapestResult, "outbound");

        assertEquals("2018-01-22", flight.get("date").toString().replaceAll("\"", ""));
        assertEquals("11:40", flight.get("time").toString().replaceAll("\"", ""));
    }

    @Test
    public void sortJsonFlightArrayByPrice() {
        JsonArray jsonArray = new JsonArray();
        for (int i = 5; i > 0; i--) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("price", i*100);
            jsonObject.addProperty("foo", i);
            jsonArray.add(jsonObject);
        }
        String sorted = fdr.sortFlightArray(jsonArray);
        String correctResult = "[{\"price\":100,\"foo\":1},{\"price\":200,\"foo\":2},{\"price\":300,\"foo\":3},{\"price\":400,\"foo\":4},{\"price\":500,\"foo\":5}]";
        assertEquals(correctResult, sorted);
    }

    private String amadeusResult = "\n" +
            "{\n" +
            "  \"currency\" : \"USD\",\n" +
            "  \"results\" : [ {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T11:40\",\n" +
            "          \"arrives_at\" : \"2018-01-22T13:30\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"777\",\n" +
            "          \"aircraft\" : \"321\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"O\",\n" +
            "            \"seats_remaining\" : 7\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T15:10\",\n" +
            "          \"arrives_at\" : \"2018-01-22T17:25\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"2A\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"316\",\n" +
            "          \"aircraft\" : \"321\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"O\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T21:05\",\n" +
            "          \"arrives_at\" : \"2018-01-22T22:40\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ORY\",\n" +
            "            \"terminal\" : \"W\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"777\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"N\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"210.31\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"210.31\",\n" +
            "        \"tax\" : \"61.31\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T11:40\",\n" +
            "          \"arrives_at\" : \"2018-01-22T13:30\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"777\",\n" +
            "          \"aircraft\" : \"321\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"O\",\n" +
            "            \"seats_remaining\" : 7\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T16:10\",\n" +
            "          \"arrives_at\" : \"2018-01-22T19:05\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"3\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1A\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"370\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Q\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T21:15\",\n" +
            "          \"arrives_at\" : \"2018-01-22T22:05\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"157\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"N\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"222.56\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"222.56\",\n" +
            "        \"tax\" : \"59.56\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T07:20\",\n" +
            "          \"arrives_at\" : \"2018-01-22T09:15\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"771\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Q\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T11:05\",\n" +
            "          \"arrives_at\" : \"2018-01-22T14:00\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"3\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1A\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"368\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"O\",\n" +
            "            \"seats_remaining\" : 5\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T17:50\",\n" +
            "          \"arrives_at\" : \"2018-01-22T18:40\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"155\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"N\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"237.56\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"237.56\",\n" +
            "        \"tax\" : \"59.56\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T18:20\",\n" +
            "          \"arrives_at\" : \"2018-01-22T21:00\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"2F\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"AF\",\n" +
            "          \"flight_number\" : \"1063\",\n" +
            "          \"aircraft\" : \"321\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-23T09:25\",\n" +
            "          \"arrives_at\" : \"2018-01-23T11:00\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ORY\",\n" +
            "            \"terminal\" : \"W\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"4453\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 5\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"246.70\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"246.70\",\n" +
            "        \"tax\" : \"46.70\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T06:00\",\n" +
            "          \"arrives_at\" : \"2018-01-22T08:55\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"2F\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"AF\",\n" +
            "          \"flight_number\" : \"1463\",\n" +
            "          \"aircraft\" : \"319\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T13:35\",\n" +
            "          \"arrives_at\" : \"2018-01-22T15:20\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"2F\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"4748\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"246.71\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"246.71\",\n" +
            "        \"tax\" : \"46.71\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T06:00\",\n" +
            "          \"arrives_at\" : \"2018-01-22T08:55\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"2F\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"AF\",\n" +
            "          \"flight_number\" : \"1463\",\n" +
            "          \"aircraft\" : \"319\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T15:40\",\n" +
            "          \"arrives_at\" : \"2018-01-22T16:55\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ORY\",\n" +
            "            \"terminal\" : \"W\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"AF\",\n" +
            "          \"flight_number\" : \"6014\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T17:50\",\n" +
            "          \"arrives_at\" : \"2018-01-22T18:40\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AF\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"4694\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"R\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"256.91\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"256.91\",\n" +
            "        \"tax\" : \"56.91\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : false,\n" +
            "        \"change_penalties\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T09:30\",\n" +
            "          \"arrives_at\" : \"2018-01-22T11:40\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AMS\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"KL\",\n" +
            "          \"operating_airline\" : \"KL\",\n" +
            "          \"flight_number\" : \"1106\",\n" +
            "          \"aircraft\" : \"E90\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"B\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T12:35\",\n" +
            "          \"arrives_at\" : \"2018-01-22T14:25\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"AMS\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"KL\",\n" +
            "          \"operating_airline\" : \"AF\",\n" +
            "          \"flight_number\" : \"2039\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"B\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T17:50\",\n" +
            "          \"arrives_at\" : \"2018-01-22T18:40\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"155\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Y\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"1286.95\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"1286.95\",\n" +
            "        \"tax\" : \"52.95\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : true,\n" +
            "        \"change_penalties\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T07:15\",\n" +
            "          \"arrives_at\" : \"2018-01-22T09:50\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"CDG\",\n" +
            "            \"terminal\" : \"1\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"SK\",\n" +
            "          \"operating_airline\" : \"SK\",\n" +
            "          \"flight_number\" : \"573\",\n" +
            "          \"aircraft\" : \"73H\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"E\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T16:00\",\n" +
            "          \"arrives_at\" : \"2018-01-22T17:35\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ORY\",\n" +
            "            \"terminal\" : \"W\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"773\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Y\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"1316.36\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"1316.36\",\n" +
            "        \"tax\" : \"82.36\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : true,\n" +
            "        \"change_penalties\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T10:00\",\n" +
            "          \"arrives_at\" : \"2018-01-22T12:25\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"ZRH\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"LX\",\n" +
            "          \"operating_airline\" : \"LX\",\n" +
            "          \"flight_number\" : \"1249\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"B\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T17:05\",\n" +
            "          \"arrives_at\" : \"2018-01-22T18:15\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ZRH\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"NCE\",\n" +
            "            \"terminal\" : \"1\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"LX\",\n" +
            "          \"operating_airline\" : \"LX\",\n" +
            "          \"flight_number\" : \"560\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"B\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T21:00\",\n" +
            "          \"arrives_at\" : \"2018-01-22T21:45\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"NCE\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"105\",\n" +
            "          \"aircraft\" : \"AT7\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Y\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"1386.10\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"1386.10\",\n" +
            "        \"tax\" : \"152.10\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : true,\n" +
            "        \"change_penalties\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"itineraries\" : [ {\n" +
            "      \"outbound\" : {\n" +
            "        \"flights\" : [ {\n" +
            "          \"departs_at\" : \"2018-01-22T11:40\",\n" +
            "          \"arrives_at\" : \"2018-01-22T13:30\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"ARN\",\n" +
            "            \"terminal\" : \"2\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"5\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"AY\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"5927\",\n" +
            "          \"aircraft\" : \"321\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Y\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T16:10\",\n" +
            "          \"arrives_at\" : \"2018-01-22T19:05\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"LHR\",\n" +
            "            \"terminal\" : \"3\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1A\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"BA\",\n" +
            "          \"operating_airline\" : \"BA\",\n" +
            "          \"flight_number\" : \"370\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"B\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        }, {\n" +
            "          \"departs_at\" : \"2018-01-22T21:15\",\n" +
            "          \"arrives_at\" : \"2018-01-22T22:05\",\n" +
            "          \"origin\" : {\n" +
            "            \"airport\" : \"MRS\",\n" +
            "            \"terminal\" : \"1B\"\n" +
            "          },\n" +
            "          \"destination\" : {\n" +
            "            \"airport\" : \"AJA\"\n" +
            "          },\n" +
            "          \"marketing_airline\" : \"XK\",\n" +
            "          \"operating_airline\" : \"XK\",\n" +
            "          \"flight_number\" : \"157\",\n" +
            "          \"aircraft\" : \"320\",\n" +
            "          \"booking_info\" : {\n" +
            "            \"travel_class\" : \"ECONOMY\",\n" +
            "            \"booking_code\" : \"Y\",\n" +
            "            \"seats_remaining\" : 9\n" +
            "          }\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"fare\" : {\n" +
            "      \"total_price\" : \"1535.06\",\n" +
            "      \"price_per_adult\" : {\n" +
            "        \"total_fare\" : \"1535.06\",\n" +
            "        \"tax\" : \"178.06\"\n" +
            "      },\n" +
            "      \"restrictions\" : {\n" +
            "        \"refundable\" : true,\n" +
            "        \"change_penalties\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

}


