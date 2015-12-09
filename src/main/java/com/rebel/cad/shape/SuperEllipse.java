package com.rebel.cad.shape;

import com.rebel.cad.controllers.MainController;
import sun.applet.Main;

import java.util.ArrayList;

/**
 * Created by Slava on 01.12.2015.
 */
public class SuperEllipse extends TPolyline {
    private double centerX;
    private double centerY;
    private double a;
    private double b;
    private double n;

    public SuperEllipse(double centerX, double centerY, double a, double b, double n) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.a = a;
        this.b = b;
        this.n = n;

        ArrayList<Dot> dots = new ArrayList<>();
        for (int i = 0; i <= 360; i += 1) {
            double rad = Math.toRadians(i);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double x = a * Math.pow(Math.abs(cos), 2 / n) * Math.signum(cos) + centerX;
            double y = b * Math.pow(Math.abs(sin), 2 / n) * Math.signum(sin) + centerY;
            dots.add(new Dot(x, y));
        }
        addPoints(dots);
    }

    public double derivative(double x, double y) {
        return (b * b * x * Math.pow(Math.abs(x / a), n - 2)) / (a * a * y * Math.pow(Math.abs(y / b), n - 2));
    }

    public double function(double x) {
        return - b * Math.pow(1 - Math.pow(Math.abs(x / a), n), 1 / n);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getN() {
        return n;
    }
}
