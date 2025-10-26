package com.example.nba;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP client for NBA team API
 */
public class NbaApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(NbaApiClient.class);
    private static final String BASE_URL = "http://localhost:3000/hello";
    private static final int PAGE_SIZE = 20;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public NbaApiClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Fetches NBA teams for a specific page
     * @param page the page number (0-based)
     * @return PaginationResponse containing teams and pagination info
     * @throws IOException if HTTP request fails
     */
    public PaginationResponse getTeams(int page) throws IOException {
        String url = BASE_URL + "?page=" + page + "&size=" + PAGE_SIZE;
        logger.info("Fetching teams from URL: {}", url);
        
        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        if (statusCode != 200) {
            throw new IOException("HTTP request failed with status code: " + statusCode);
        }
        
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        EntityUtils.consume(entity);
        
        logger.debug("Response body: {}", responseBody);
        
        return objectMapper.readValue(responseBody, PaginationResponse.class);
    }
    
    /**
     * Fetches all NBA teams across all pages
     * @return List of all TeamInfo objects
     * @throws IOException if HTTP request fails
     */
    public List<TeamInfo> getAllTeams() throws IOException {
        List<TeamInfo> allTeams = new ArrayList<>();
        int currentPage = 0;
        boolean hasNext = true;
        
        while (hasNext) {
            PaginationResponse response = getTeams(currentPage);
            allTeams.addAll(response.getData());
            hasNext = response.isHasNext();
            currentPage++;
            
            logger.info("Fetched page {} with {} teams. Total teams so far: {}", 
                       currentPage, response.getData().size(), allTeams.size());
        }
        
        return allTeams;
    }
    
    /**
     * Calculates total revenue from all teams
     * @return total revenue as Long
     * @throws IOException if HTTP request fails
     */
    public Long calculateTotalRevenue() throws IOException {
        List<TeamInfo> allTeams = getAllTeams();
        return allTeams.stream()
                .mapToLong(team -> team.getRevenue() != null ? team.getRevenue() : 0L)
                .sum();
    }
    
    /**
     * Closes the HTTP client resources
     */
    public void close() {
        try {
            if (httpClient instanceof org.apache.http.impl.client.CloseableHttpClient) {
                ((org.apache.http.impl.client.CloseableHttpClient) httpClient).close();
            }
        } catch (IOException e) {
            logger.error("Error closing HTTP client", e);
        }
    }
}
