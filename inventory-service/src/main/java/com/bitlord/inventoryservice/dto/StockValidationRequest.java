package com.bitlord.inventoryservice.dto;

import java.util.List;

/**
 * Request DTO for validating stock.
 */
public class StockValidationRequest {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String sku;
        private int requestedQuantity;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public int getRequestedQuantity() {
            return requestedQuantity;
        }

        public void setRequestedQuantity(int requestedQuantity) {
            this.requestedQuantity = requestedQuantity;
        }
    }
}
