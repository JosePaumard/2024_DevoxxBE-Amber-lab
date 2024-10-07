package org.paumard.flightmonitoring.db.model;

public class PriceEntity {
    private final int price;

    public PriceEntity(int price) {
        this.price = price;
    }

    public int price() {
        return this.price;
    }

    @Override
    public String toString() {
        return "Price[price=" + price + "]";
    }
}
