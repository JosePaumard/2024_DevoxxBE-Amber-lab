package org.paumard.hol.sealedswitch.model;

public record Circle(double radius) implements Shape {

//    @Override
    public double surface() {
        return Math.PI * radius * radius;
    }
}
