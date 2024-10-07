package org.paumard.hol.sealedswitch.model;

public record Rectangle(double length, double width) {

//    @Override
    public double surface() {
        return length*width;
    }
}
