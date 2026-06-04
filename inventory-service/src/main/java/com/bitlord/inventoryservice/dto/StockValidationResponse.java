package com.bitlord.inventoryservice.dto;

import java.util.List;

/**
 * Response DTO for stock validation.
 */
public class StockValidationResponse {
    private boolean allAvailable;
    private List<Result> results;

    public StockValidationResponse() {}

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

    public static class Result {
        private String sku;
        private int requestedQuantity;
        private int availableQuantity;
        private boolean sufficient;

        public Result() {}

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
