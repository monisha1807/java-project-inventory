package com.wipro.inventory.util;

public class NegativeStockException extends Exception {
    public NegativeStockException(String message) {
        super(message);
    }
}
