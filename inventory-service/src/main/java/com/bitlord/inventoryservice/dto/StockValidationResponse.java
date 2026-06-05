package com.bitlord.inventoryservice.dto;

import java.util.List;


/**
 * Response DTO for stock validation.
 *
 * This class is used to return the result of a stock availability check
 * for one or more products (SKUs), typically before placing an order.
 */
public class StockValidationResponse {

    // Indicates whether all requested items have sufficient stock available
    private boolean allAvailable;

    // Detailed validation results for each SKU
    private List<Result> results;

    // Default constructor
    public StockValidationResponse() {}


    // Constructor to initialize all fields
    public StockValidationResponse(boolean allAvailable, List<Result> results) {
        this.allAvailable = allAvailable;
        this.results = results;
    }

    public boolean isAllAvailable() {
        return allAvailable;
    }

    public void setAllAvailable(boolean allAvailable) {
        this.allAvailable = allAvailable;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }



    /**
     * Inner DTO representing validation result for a single SKU
     */
    public static class Result {

        // Stock Keeping Unit identifier
        private String sku;

        // Quantity requested by the user/order
        private int requestedQuantity;

        // Quantity currently available in inventory
        private int availableQuantity;

        // Indicates whether requested quantity can be fulfilled
        private boolean sufficient;


        // Default constructor
        public Result() {}

        // Constructor to initialize all fields
        public Result(String sku, int requestedQuantity, int availableQuantity, boolean sufficient) {
            this.sku = sku;
            this.requestedQuantity = requestedQuantity;
            this.availableQuantity = availableQuantity;
            this.sufficient = sufficient;
        }

        public String getSku() {
            return sku;
        }

        public int getRequestedQuantity() {
            return requestedQuantity;
        }

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public boolean isSufficient() {
            return sufficient;
        }
    }
}