package com.example.nba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Main application class for NBA HTTP client
 */
public class NbaApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(NbaApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting NBA HTTP Client Application");
        
        NbaApiClient apiClient = new NbaApiClient();
        
        try {
            // Example 1: Fetch teams from a specific page
            logger.info("Fetching teams from page 0...");
            PaginationResponse response = apiClient.getTeams(0);
            logger.info("Page 0 response: {} teams, hasNext: {}", 
                       response.getData().size(), response.isHasNext());
            
            // Example 2: Fetch all teams across all pages
            logger.info("Fetching all teams...");
            List<TeamInfo> allTeams = apiClient.getAllTeams();
            logger.info("Total teams fetched: {}", allTeams.size());
            
            // Example 3: Calculate total revenue
            logger.info("Calculating total revenue...");
            Long totalRevenue = apiClient.calculateTotalRevenue();
            logger.info("Total revenue from all teams: ${}", totalRevenue);
            
            // Display sample teams
            logger.info("Sample teams:");
            allTeams.stream().limit(5).forEach(team -> 
                logger.info("  {} - Revenue: ${}", team.getName(), team.getRevenue())
            );
            
        } catch (IOException e) {
            logger.error("Error occurred while fetching NBA team data", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } finally {
            apiClient.close();
            logger.info("Application completed");
        }
    }
}
