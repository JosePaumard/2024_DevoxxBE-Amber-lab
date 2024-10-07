package org.paumard.flightmonitoring.db.model;

public class MultilegFlightEntity {
    private MultilegFlightPK id;
    private CityEntity from;
    private CityEntity via;
    private CityEntity to;
    private PriceEntity price;
    private PlaneEntity plane;

    public MultilegFlightEntity(MultilegFlightPK id, CityEntity from, CityEntity via, CityEntity to, PriceEntity price, PlaneEntity plane) {
        this.id = id;
        this.from = from;
        this.via = via;
        this.to = to;
        this.price = price;
        this.plane = plane;
    }

    public MultilegFlightPK id() {
        return this.id;
    }

    public CityEntity from() {
        return this.from;
    }

    public CityEntity via() {
        return this.via;
    }

    public CityEntity to() {
        return this.to;
    }

    public PriceEntity price() {
        return this.price;
    }

    public void updatePrice(PriceEntity price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Flight[id=" + id + ", from=" + from + ", via=" + via + ", to=" + to +
               ", price=" + price + ", plane = " + plane + "]";
    }
}
