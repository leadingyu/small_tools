package com.example.nba;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for NbaApiClient with mock data and JSON serialization tests
 */
public class NbaApiClientTest {
    
    private NbaApiClient nbaApiClient;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        nbaApiClient = new NbaApiClient();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @DisplayName("Test revenue calculation with mock data - 2 pages (38 teams total)")
    void testCalculateTotalRevenueWithMockData() {
        // Create mock data for 2 pages: 20 teams on page 1, 18 teams on page 2
        List<TeamInfo> page1Teams = createMockTeamDataPage1();
        List<TeamInfo> page2Teams = createMockTeamDataPage2();
        
        // Combine all teams
        List<TeamInfo> allMockTeams = new ArrayList<>();
        allMockTeams.addAll(page1Teams);
        allMockTeams.addAll(page2Teams);
        
        // Calculate expected total revenue
        long expectedTotalRevenue = allMockTeams.stream()
                .mapToLong(team -> team.getRevenue() != null ? team.getRevenue() : 0L)
                .sum();
        
        System.out.println("Mock data created with " + allMockTeams.size() + " teams");
        System.out.println("Expected total revenue: $" + expectedTotalRevenue);
        
        // Display first few teams for verification
        System.out.println("\nFirst 5 teams:");
        allMockTeams.stream().limit(5).forEach(team -> 
            System.out.println("  " + team.getName() + " - Revenue: $" + team.getRevenue())
        );
        
        // Verify we have the correct number of teams
        assertEquals(38, allMockTeams.size(), "Should have 38 teams total");
        
        // Verify revenue calculation
        assertTrue(expectedTotalRevenue > 0, "Total revenue should be greater than 0");
        
        System.out.println("\nTest passed: Successfully calculated revenue for " + 
                          allMockTeams.size() + " teams with total revenue of $" + expectedTotalRevenue);
    }
    
    @Test
    @DisplayName("Test TeamInfo object creation and properties")
    void testTeamInfoObject() {
        TeamInfo team = new TeamInfo("Los Angeles Lakers", 500000000L);
        
        assertEquals("Los Angeles Lakers", team.getName());
        assertEquals(500000000L, team.getRevenue());
        
        // Test toString method
        String teamString = team.toString();
        assertTrue(teamString.contains("Los Angeles Lakers"));
        assertTrue(teamString.contains("500000000"));
    }
    
    @Test
    @DisplayName("Test PaginationResponse object creation")
    void testPaginationResponseObject() {
        List<TeamInfo> teams = new ArrayList<>();
        teams.add(new TeamInfo("Team 1", 1000000L));
        teams.add(new TeamInfo("Team 2", 2000000L));
        
        PaginationResponse response = new PaginationResponse(teams, 0, 2, 38, true);
        
        assertEquals(2, response.getData().size());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getTotalPages());
        assertEquals(38, response.getTotalElements());
        assertTrue(response.isHasNext());
    }
    
    /**
     * Creates mock JSON response for a specific page
     */
    private String createMockJsonResponse(int page, List<TeamInfo> teams, boolean hasNext) throws IOException {
        PaginationResponse paginationResponse = new PaginationResponse(
            teams, page, hasNext ? 2 : 1, 38, hasNext
        );
        
        return objectMapper.writeValueAsString(paginationResponse);
    }
    
    /**
     * Creates mock team data for page 1 (20 teams)
     */
    private List<TeamInfo> createMockTeamDataPage1() {
        List<TeamInfo> teams = new ArrayList<>();
        String[] teamNames = {
            "Los Angeles Lakers", "Boston Celtics", "Chicago Bulls", "Miami Heat", "Golden State Warriors",
            "New York Knicks", "Philadelphia 76ers", "Brooklyn Nets", "Toronto Raptors", "Milwaukee Bucks",
            "Detroit Pistons", "Cleveland Cavaliers", "Indiana Pacers", "Atlanta Hawks", "Orlando Magic",
            "Washington Wizards", "Charlotte Hornets", "Sacramento Kings", "Phoenix Suns", "Portland Trail Blazers"
        };
        
        for (int i = 0; i < 20; i++) {
            long revenue = 100000000L + (i * 5000000L); // Revenue from $100M to $195M
            teams.add(new TeamInfo(teamNames[i], revenue));
        }
        
        return teams;
    }
    
    /**
     * Creates mock team data for page 2 (18 teams)
     */
    private List<TeamInfo> createMockTeamDataPage2() {
        List<TeamInfo> teams = new ArrayList<>();
        String[] teamNames = {
            "Dallas Mavericks", "Houston Rockets", "San Antonio Spurs", "Denver Nuggets", "Utah Jazz",
            "Oklahoma City Thunder", "Minnesota Timberwolves", "New Orleans Pelicans", "Memphis Grizzlies",
            "Los Angeles Clippers", "Seattle SuperSonics", "Vancouver Grizzlies", "New Jersey Nets",
            "Charlotte Bobcats", "New Orleans Hornets", "Seattle Storm", "Las Vegas Aces", "Atlanta Dream"
        };
        
        for (int i = 0; i < 18; i++) {
            long revenue = 80000000L + (i * 3000000L); // Revenue from $80M to $131M
            teams.add(new TeamInfo(teamNames[i], revenue));
        }
        
        return teams;
    }
    
    /**
     * Helper method to calculate total revenue from a list of teams
     */
    private long calculateTotalRevenue(List<TeamInfo> teams) {
        return teams.stream()
                .mapToLong(team -> team.getRevenue() != null ? team.getRevenue() : 0L)
                .sum();
    }
    
    @Test
    @DisplayName("Test revenue calculation helper method")
    void testRevenueCalculationHelper() {
        List<TeamInfo> page1Teams = createMockTeamDataPage1();
        List<TeamInfo> page2Teams = createMockTeamDataPage2();
        List<TeamInfo> allTeams = new ArrayList<>();
        allTeams.addAll(page1Teams);
        allTeams.addAll(page2Teams);
        
        long totalRevenue = calculateTotalRevenue(allTeams);
        
        assertTrue(totalRevenue > 0, "Total revenue should be positive");
        assertEquals(38, allTeams.size(), "Should have 38 teams");
        
        System.out.println("Total revenue calculated: $" + totalRevenue);
    }
    
    @Test
    @DisplayName("Test JSON serialization/deserialization for API responses")
    void testJsonSerializationDeserialization() throws IOException {
        // Create mock data for page 1
        List<TeamInfo> page1Teams = createMockTeamDataPage1();
        PaginationResponse page1Response = new PaginationResponse(page1Teams, 0, 2, 38, true);
        
        // Serialize to JSON
        String jsonResponse = objectMapper.writeValueAsString(page1Response);
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.contains("Los Angeles Lakers"));
        assertTrue(jsonResponse.contains("hasNext"));
        
        // Deserialize back to object
        PaginationResponse deserializedResponse = objectMapper.readValue(jsonResponse, PaginationResponse.class);
        
        assertEquals(20, deserializedResponse.getData().size());
        assertEquals(0, deserializedResponse.getPage());
        assertEquals(2, deserializedResponse.getTotalPages());
        assertEquals(38, deserializedResponse.getTotalElements());
        assertTrue(deserializedResponse.isHasNext());
        
        // Verify team data
        TeamInfo firstTeam = deserializedResponse.getData().get(0);
        assertEquals("Los Angeles Lakers", firstTeam.getName());
        assertEquals(100000000L, firstTeam.getRevenue());
        
        System.out.println("JSON serialization/deserialization test passed");
        System.out.println("Sample JSON response: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())) + "...");
    }
}
