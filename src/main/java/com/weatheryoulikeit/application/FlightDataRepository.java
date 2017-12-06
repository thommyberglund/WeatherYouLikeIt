package com.weatheryoulikeit.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightDataRepository {

    @Autowired
    private DataSource dataSource;

    public double getTemperature(String country, int month) throws SQLException {

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

    public List<String> convertCountrytoCity(String country) {
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

}
