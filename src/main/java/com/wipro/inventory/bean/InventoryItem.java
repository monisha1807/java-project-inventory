package com.wipro.inventory.bean;

public class InventoryItem {
    private final String itemId;
    private final String name;
    private int currentStock;
    private final int reorderThreshold;

    public InventoryItem(String itemId, String name, int currentStock, int reorderThreshold) {
        this.itemId = itemId;
        this.name = name;
        this.currentStock = currentStock;
        this.reorderThreshold = reorderThreshold;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public synchronized int getCurrentStock() {
        return currentStock;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public synchronized void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    @Override
    public String toString() {
        return itemId + " - " + name + " Stock: " + currentStock
                + " Threshold: " + reorderThreshold;
    }
}
