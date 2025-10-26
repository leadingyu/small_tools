package com.example.nba;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents NBA team information
 */
public class TeamInfo {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("revenue")
    private Long revenue;
    
    // Default constructor
    public TeamInfo() {}
    
    // Constructor with parameters
    public TeamInfo(String name, Long revenue) {
        this.name = name;
        this.revenue = revenue;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getRevenue() {
        return revenue;
    }
    
    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    }
    
    @Override
    public String toString() {
        return "TeamInfo{" +
                "name='" + name + '\'' +
                ", revenue=" + revenue +
                '}';
    }
}
