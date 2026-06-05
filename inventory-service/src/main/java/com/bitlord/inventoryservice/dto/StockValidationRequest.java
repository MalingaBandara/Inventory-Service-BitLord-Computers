package com.bitlord.inventoryservice.dto;

import java.util.List;

/**
 * Request DTO for validating stock.
 *
 * This class is used to send a list of items to the inventory service
 * in order to check whether sufficient stock is available for each item.
 */
public class StockValidationRequest {

    // List of items that need stock validation
    private List<Item> items;

    /**
     * Returns the list of items to be validated
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Sets the list of items to be validated
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Inner DTO representing a single item in the stock validation request
     */
    public static class Item {

        // Stock Keeping Unit identifier for the product
        private String sku;

        // Quantity requested by the user/order
        private int requestedQuantity;

        /**
         * Returns the SKU of the item
         */
        public String getSku() {
            return sku;
        }


        public void setSku(String sku) {
            this.sku = sku;
        }

        /**
         * Returns the requested quantity of the item
         */
        public int getRequestedQuantity() {
            return requestedQuantity;
        }


        public void setRequestedQuantity(int requestedQuantity) {
            this.requestedQuantity = requestedQuantity;
        }
    }
}