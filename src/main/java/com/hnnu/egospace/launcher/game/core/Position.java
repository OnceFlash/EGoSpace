package com.hnnu.egospace.launcher.game.core;

import lombok.Data;
import java.io.Serializable;

@Data
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    
    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
    
    public Position add(Position other) {
        return new Position(x + other.x, y + other.y);
    }
    
    public Position subtract(Position other) {
        return new Position(x - other.x, y - other.y);
    }
    
    public Position scale(double factor) {
        return new Position(x * factor, y * factor);
    }
    
    public Position normalize() {
        double length = Math.sqrt(x * x + y * y);
        if (length == 0) return new Position(0, 0);
        return new Position(x / length, y / length);
    }
    
    public double angleTo(Position other) {
        return Math.toDegrees(Math.atan2(other.y - y, other.x - x));
    }
    
    public boolean isWithinRange(Position other, double range) {
        return distanceTo(other) <= range;
    }
    
    public Position clone() {
        return new Position(x, y);
    }
    
    public Position rotate(double degrees) {
        double rad = Math.toRadians(degrees);
        double newX = x * Math.cos(rad) - y * Math.sin(rad);
        double newY = x * Math.sin(rad) + y * Math.cos(rad);
        return new Position(newX, newY);
    }
    
    public Position lerp(Position target, double t) {
        return new Position(
            x + (target.x - x) * t,
            y + (target.y - y) * t
        );
    }
    
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    public static Position zero() {
        return new Position(0, 0);
    }
    
    public static Position fromAngle(double degrees) {
        double rad = Math.toRadians(degrees);
        return new Position(Math.cos(rad), Math.sin(rad));
    }
    
    public static Position random(double minX, double maxX, double minY, double maxY) {
        return new Position(
            minX + Math.random() * (maxX - minX),
            minY + Math.random() * (maxY - minY)
        );
    }
    
    @Override
    public String toString() {
        return String.format("Position(x=%.2f, y=%.2f)", x, y);
    }
}
