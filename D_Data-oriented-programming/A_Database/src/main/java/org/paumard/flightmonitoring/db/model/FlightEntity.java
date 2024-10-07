package org.paumard.flightmonitoring.db.model;

public class FlightEntity {
    private FlightPK id;
    private CityEntity from;
    private CityEntity to;
    private PriceEntity price;
    private PlaneEntity plane;

    public FlightEntity(FlightPK id, CityEntity from, CityEntity to, PriceEntity price, PlaneEntity plane) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.price = price;
        this.plane = plane;
    }

    public FlightPK id() {
        return this.id;
    }

    public CityEntity from() {
        return this.from;
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
        return "Flight[id=" + id + ", from=" + from + ", city=" + to +
               ", price=" + price + ", plane = " + plane + "]";
    }
}
