package com.wipro.inventory.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.wipro.inventory.bean.InventoryItem;
import com.wipro.inventory.util.NegativeStockException;

public class InventoryService {
    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());
    private final Map<String, InventoryItem> inventory = new TreeMap<>();

    public synchronized void addItem(InventoryItem item) {
        inventory.put(item.getItemId(), item);
    }

    public synchronized void updateStock(String itemId, int delta)
            throws NegativeStockException {
        InventoryItem item = inventory.get(itemId);

        if (item == null) {
            LOGGER.log(Level.WARNING, "Item not found: {0}", itemId);
            return;
        }

        int newStock = item.getCurrentStock() + delta;
        if (newStock < 0) {
            throw new NegativeStockException(
                    "Stock cannot be negative for " + item.getName()
                    + ". Current stock: " + item.getCurrentStock()
                    + ", requested change: " + delta);
        }

        item.setCurrentStock(newStock);
    }

    public synchronized List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(item -> item.getCurrentStock() <= item.getReorderThreshold())
                .sorted(Comparator.comparingInt(
                        (InventoryItem item) ->
                                item.getReorderThreshold() - item.getCurrentStock())
                        .reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
