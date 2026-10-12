package com.wipro.inventory.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wipro.inventory.bean.InventoryItem;
import com.wipro.inventory.util.NegativeStockException;

public class InventoryMonitor {
    private static final Logger LOGGER = Logger.getLogger(InventoryMonitor.class.getName());

    public static void main(String[] args) {
        InventoryService service = new InventoryService();
        String startupArgs = Arrays.toString(args);

        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            if (!startupArgs.equals("[]")) {
                LOGGER.log(Level.INFO, "Startup arguments: {0}", startupArgs);
            }

            service.addItem(new InventoryItem("I101", "Mouse", 20, 5));
            service.addItem(new InventoryItem("I102", "Keyboard", 5, 10));
            service.addItem(new InventoryItem("I103", "Monitor", 12, 5));

            scheduler.scheduleAtFixedRate(() -> {
                LOGGER.info("Checking Inventory...");
                List<InventoryItem> lowStockItems = service.getLowStockItems();

                if (lowStockItems.isEmpty()) {
                    LOGGER.info("No low stock items.");
                } else {
                    for (InventoryItem item : lowStockItems) {
                        int shortage = item.getReorderThreshold() - item.getCurrentStock();
                        LOGGER.log(Level.INFO, "Alert: Item needs reorder ({0} units below threshold)", shortage);
                    }
                }
            }, 0, 5, TimeUnit.SECONDS);

            Thread saleThread = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    service.updateStock("I102", -3);
                    service.updateStock("I103", -15);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Inventory update thread was interrupted", e);
                } catch (NegativeStockException e) {
                    LOGGER.log(Level.WARNING, "NegativeStockException thrown and logged for offending update", e);
                }
            }, "StockUpdateThread");

            saleThread.start();

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Main thread was interrupted during monitoring", e);
            }
        }

        LOGGER.info("Program Ended");
    }
}
