package com.example.nba;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a paginated response from the NBA API
 */
public class PaginationResponse {
    
    @JsonProperty("data")
    private List<TeamInfo> data;
    
    @JsonProperty("page")
    private int page;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("totalElements")
    private int totalElements;
    
    @JsonProperty("hasNext")
    private boolean hasNext;
    
    // Default constructor
    public PaginationResponse() {}
    
    // Constructor with parameters
    public PaginationResponse(List<TeamInfo> data, int page, int totalPages, 
                            int totalElements, boolean hasNext) {
        this.data = data;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
    
    // Getters and setters
    public List<TeamInfo> getData() {
        return data;
    }
    
    public void setData(List<TeamInfo> data) {
        this.data = data;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    @Override
    public String toString() {
        return "PaginationResponse{" +
                "data=" + data +
                ", page=" + page +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                ", hasNext=" + hasNext +
                '}';
    }
}
