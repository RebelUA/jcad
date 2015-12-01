package com.rebel.cad.controllers;

import com.rebel.cad.MainApp;
import com.rebel.cad.collections.ShapeGroup;
import com.rebel.cad.shapes.*;
import com.rebel.cad.util.Helper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainController extends Controller implements Initializable {

    private static int dotCount;
    private ArrayList<HintedDot> dotsList = new ArrayList<>();
    private HintedDot rotationPoint;
    @FXML
    private ShapeGroup figure;


    @FXML
    private VBox rootBox;
    @FXML
    private Pane groupPane;
    @FXML
    private Label xCoord;
    @FXML
    private Label yCoord;
    @FXML
    private TextField stepField;
    @FXML
    private TextField rotateField;
    @FXML
    private TextField deltaX;
    @FXML
    private TextField deltaY;

    @FXML
    private TextField affXX;
    @FXML
    private TextField affXY;
    @FXML
    private TextField affYX;
    @FXML
    private TextField affYY;
    @FXML
    private TextField affDX;
    @FXML
    private TextField affDY;

    @FXML
    private TextField projXX;
    @FXML
    private TextField projXY;
    @FXML
    private TextField projXW;
    @FXML
    private TextField projYX;
    @FXML
    private TextField projYY;
    @FXML
    private TextField projYW;
    @FXML
    private TextField projX;
    @FXML
    private TextField projY;
    @FXML
    private TextField projW;

    private ShapeGroup grid;
    private ShapeGroup axises;

    private int step = 20;

    private static double width = MainApp.INITIAL_WIDTH;
    private static double height = MainApp.INITIAL_HEIGHT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grid = createGrid(width, height - 40, step);
        axises = createAxis(width, height - 40);

        groupPane.getChildren().addAll(grid, axises);

        groupPane.setOnMouseMoved(event -> {
            xCoord.setText(Double.toString(toFakeX(event.getX())));
            yCoord.setText(Double.toString(toFakeY(event.getY())));
        });

        rootBox.setPrefWidth(800);
        rootBox.setPrefHeight(600);

        rootBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.doubleValue();
            double delta = width - oldValue.doubleValue();
            if (oldValue.intValue() != 0)
                figure.move(delta / 2, 0);
            resize();
        });
        rootBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.doubleValue();
            double delta = height - oldValue.doubleValue();
            if (oldValue.intValue() != 0)
                figure.move(0, delta / 2);
            resize();
        });

//        figure.getChildren().addAll(new Line(toRealX(-50), toRealY(50), toRealX(-50), toRealY(-50), toRealX(50), toRealY(-50), toRealX(50), toRealY(50), toRealX(-50), toRealY(50)));
//        figure.getChildren().addAll(new TText(toRealX(60), toRealY(60), "Hello"));
    }

    private ShapeGroup createGrid(double width, double height, int step) {
        ShapeGroup grid = new ShapeGroup();

        double x1;
        for (double x = (width / 2) % step; x < width; x += step) {
            x1 = x + 0.5;
            grid.getChildren().add(new Line(x1, 0, x1, height, 0.4));
        }

        double y1;
        for (double y = (height / 2) % step; y < height; y += step) {
            y1 = y + 0.5;
            grid.getChildren().add(new Line(0, y1, width, y1, 0.4));
        }

        TText text = new TText(width / 2 - 20, height / 2 - step - step / 10, Integer.toString(step));
        text.setOpacity(1);
        grid.getChildren().add(text);

        return grid;
    }

    private ShapeGroup createAxis(double width, double height) {
        ShapeGroup axis = new ShapeGroup();

        axis.getChildren().add(new Line(width / 2, 0, width / 2, height));
        axis.getChildren().add(new Line(0, height / 2, width, height / 2));

        axis.getChildren().add(new TText(width - 20, height / 2 + 20, "X"));
        axis.getChildren().add(new TText(width / 2 - 20, 20, "Y"));

        return axis;
    }

    @FXML
    private void erase() {
        dotsList.add(rotationPoint);
        groupPane.getChildren().removeAll(dotsList);
        dotsList.clear();
        dotCount = 0;
        figure.getChildren().removeAll(figure.getChildren());
        figure.getChildren().clear();
//        figure.project(width, height/2, 0.01, width/2, height, 0.01, width/2, height/2, 0.01);
//        axises.project(width, height / 2, 0.01, width / 2, height, 0.01, width/2, height/2, 0.01);
//        grid.project(width, height/2, 0.01, width/2, height, 0.01, width/2, height/2, 0.01);
//        clip();

    }

    public static double toRealX(double x) {
        return x + width / 2 - 90;
    }

    public static double toFakeX(double x) {
        return x - width / 2 + 90;
    }

    public static double toRealY(double y) {
        return -y + height / 2;
    }

    public static double toFakeY(double y) {
        return -(y - height / 2);
    }

    private void drawFigure(List<HintedDot> dots) {
        HintedDot centerDot = new HintedDot(toRealX(0), toRealY(0), 0);
        double figureHeight = 400;
        double radius = 30;
        double y = centerDot.getCenterY() + figureHeight / 2 - radius;
        double x = centerDot.getCenterX() - figureHeight / 3;
        Dot dotOnFirstCircle = Helper.getDotOnArc(x, y, radius, 300);
        Dot dotOnLastCircle = Helper.getDotOnArc(x + figureHeight / 1.5, y, radius, 250);
        for (int i = 0; i < 5; i++) {
            figure.getChildren().add(new Circle(x, y, radius));//, Axis.Both));
            x += figureHeight / 6;
        }

        Dot leftSecond = new Dot(centerDot.getCenterX() - figureHeight / 6 - radius, y);
        Dot rightSecond = new Dot(centerDot.getCenterX() - figureHeight / 6 + radius, y);
        Dot leftThird = new Dot(centerDot.getCenterX() - radius, y);
        Dot rightThird = new Dot(centerDot.getCenterX() + radius, y);
        Dot leftFourth = new Dot(centerDot.getCenterX() + figureHeight / 6 - radius, y);
        Dot rightFourth = new Dot(centerDot.getCenterX() + figureHeight / 6 + radius, y);

        x = centerDot.getCenterX();
        y = centerDot.getCenterY() - figureHeight / 2 + figureHeight / 14;
        figure.getChildren().add(new Arc(x, y, figureHeight / 14, 190, 350));//, Axis.Both));
        Dot topStart = Helper.getDotOnArc(x, y, figureHeight / 14, 190);
        Dot topEnd = Helper.getDotOnArc(x, y, figureHeight / 14, 350);

        double radius2 = 60;
        y = centerDot.getCenterY();
        x = centerDot.getCenterX() - figureHeight / 6;
        figure.getChildren().add(new Line(x, y, topStart.getX(), topStart.getY()));
        figure.getChildren().add(new Line(x, y, dotOnFirstCircle.getX(), dotOnFirstCircle.getY()));
        figure.getChildren().add(new Line(x, y, leftSecond.getX(), leftSecond.getY()));
        figure.getChildren().add(new Line(x, y, rightSecond.getX(), rightSecond.getY()));
        figure.getChildren().add(new Arc(x, y, radius2, 111, 282));//, Axis.Horizontal));

        x = centerDot.getCenterX() + figureHeight / 6;
        figure.getChildren().add(new Line(x, y, topEnd.getX(), topEnd.getY()));
        figure.getChildren().add(new Line(x, y, dotOnLastCircle.getX(), dotOnLastCircle.getY()));
        figure.getChildren().add(new Line(x, y, leftFourth.getX(), leftFourth.getY()));
        figure.getChildren().add(new Line(x, y, rightFourth.getX(), rightFourth.getY()));
        figure.getChildren().add(new Arc(x, y, radius2, 258, 68));//, Axis.Horizontal));

        figure.getChildren().add(new Line(centerDot.getCenterX(), centerDot.getCenterY(), leftThird.getX(), leftThird.getY()));
        figure.getChildren().add(new Line(centerDot.getCenterX(), centerDot.getCenterY(), rightThird.getX(), rightThird.getY()));

        Line horAx = new Line(centerDot.getCenterX() - figureHeight / 4, centerDot.getCenterY(), centerDot.getCenterX() + figureHeight / 4, centerDot.getCenterY());
        horAx.getStrokeDashArray().addAll(10d);
        figure.getChildren().add(horAx);
        Line verAx = new Line(centerDot.getCenterX(), centerDot.getCenterY() - figureHeight / 1.8, centerDot.getCenterX(), centerDot.getCenterY() + figureHeight / 1.8);
        verAx.getStrokeDashArray().addAll(10d);
        figure.getChildren().add(verAx);
        clip();
    }

    @FXML
    private void quickDraw() {
        HintedDot centerDot = new HintedDot(toRealX(0), toRealY(0), 0);
        double figureHeight = 400;
        double radius = 30;
        double y = centerDot.getCenterY() + figureHeight / 2 - radius;
        double x = centerDot.getCenterX() - figureHeight / 3;
        Dot dotOnFirstCircle = Helper.getDotOnArc(x, y, radius, 300);
        Dot dotOnLastCircle = Helper.getDotOnArc(x + figureHeight / 1.5, y, radius, 250);
        for (int i = 0; i < 5; i++) {
            figure.getChildren().add(new Circle(x, y, radius));//, Axis.Both));
            x += figureHeight / 6;
        }

        Dot leftSecond = new Dot(centerDot.getCenterX() - figureHeight / 6 - radius, y);
        Dot rightSecond = new Dot(centerDot.getCenterX() - figureHeight / 6 + radius, y);
        Dot leftThird = new Dot(centerDot.getCenterX() - radius, y);
        Dot rightThird = new Dot(centerDot.getCenterX() + radius, y);
        Dot leftFourth = new Dot(centerDot.getCenterX() + figureHeight / 6 - radius, y);
        Dot rightFourth = new Dot(centerDot.getCenterX() + figureHeight / 6 + radius, y);

        x = centerDot.getCenterX();
        y = centerDot.getCenterY() - figureHeight / 2 + figureHeight / 14;
        figure.getChildren().add(new Arc(x, y, figureHeight / 14, 190, 350));//, Axis.Both));
        Dot topStart = Helper.getDotOnArc(x, y, figureHeight / 14, 190);
        Dot topEnd = Helper.getDotOnArc(x, y, figureHeight / 14, 350);

        double radius2 = 60;
        y = centerDot.getCenterY();
        x = centerDot.getCenterX() - figureHeight / 6;
        figure.getChildren().add(new Line(x, y, topStart.getX(), topStart.getY()));
        figure.getChildren().add(new Line(x, y, dotOnFirstCircle.getX(), dotOnFirstCircle.getY()));
        figure.getChildren().add(new Line(x, y, leftSecond.getX(), leftSecond.getY()));
        figure.getChildren().add(new Line(x, y, rightSecond.getX(), rightSecond.getY()));
        figure.getChildren().add(new Arc(x, y, radius2, 111, 282));//, Axis.Horizontal));

        x = centerDot.getCenterX() + figureHeight / 6;
        figure.getChildren().add(new Line(x, y, topEnd.getX(), topEnd.getY()));
        figure.getChildren().add(new Line(x, y, dotOnLastCircle.getX(), dotOnLastCircle.getY()));
        figure.getChildren().add(new Line(x, y, leftFourth.getX(), leftFourth.getY()));
        figure.getChildren().add(new Line(x, y, rightFourth.getX(), rightFourth.getY()));
        figure.getChildren().add(new Arc(x, y, radius2, 258, 68));//, Axis.Horizontal));

        figure.getChildren().add(new Line(centerDot.getCenterX(), centerDot.getCenterY(), leftThird.getX(), leftThird.getY()));
        figure.getChildren().add(new Line(centerDot.getCenterX(), centerDot.getCenterY(), rightThird.getX(), rightThird.getY()));

        Line horAx = new Line(centerDot.getCenterX() - figureHeight / 4, centerDot.getCenterY(), centerDot.getCenterX() + figureHeight / 4, centerDot.getCenterY());
        horAx.getStrokeDashArray().addAll(10d);
        figure.getChildren().add(horAx);
        Line verAx = new Line(centerDot.getCenterX(), centerDot.getCenterY() - figureHeight / 1.8, centerDot.getCenterX(), centerDot.getCenterY() + figureHeight / 1.8);
        verAx.getStrokeDashArray().addAll(10d);
        figure.getChildren().add(verAx);
    }

    private void clip() {
        figure.setClip(new Rectangle(0, 0, width - 180, height));
        axises.setClip(new Rectangle(0, 0, width - 180, height));
        grid.setClip(new Rectangle(0, 0, width - 180, height));

    }

    @FXML
    private void draw() {
        showInform("Center point", "Place center point on the plane");
        createDot();
    }

    private void createDot() {
        groupPane.setOnMouseClicked(event -> {
            double fx = toFakeX(event.getX());
            double fy = toFakeY(event.getY());
            HintedDot dot = new HintedDot(event.getX(), event.getY(), dotCount++);
            dot.setTooltip(fx, fy);
            dotsList.add(dot);
            figure.getChildren().add(dot);
            drawFigure(dotsList);
            groupPane.setOnMouseClicked(null);
        });
    }

    private double getPointOnLine(HintedDot a, HintedDot b, double x) {
        return (x - a.getCenterX()) * (b.getCenterY() - a.getCenterY()) / (b.getCenterX() - a.getCenterX()) + a.getCenterY();
    }

    @FXML
    private void rebuild() {
        int prevStep = step;
        step = Integer.parseInt(stepField.getText());
        groupPane.getChildren().removeAll(axises, grid);
        grid = createGrid(width - 180, height, step);
        axises = createAxis(width - 180, height);
        groupPane.getChildren().add(axises);
        axises.toBack();
        groupPane.getChildren().add(grid);
        grid.toBack();

        figure.getChildren().clear();

//        figure.getChildren().addAll(new Line(toRealX(-50), toRealY(50), toRealX(-50), toRealY(-50), toRealX(50), toRealY(-50), toRealX(50), toRealY(50), toRealX(-50), toRealY(50)));
//        figure.getChildren().addAll(new TText(toRealX(60), toRealY(60), "Hello"));
        figure.setScaleX(step / prevStep);
        figure.setScaleY(step / prevStep);
    }

    private void resize() {
        groupPane.getChildren().removeAll(axises, grid);
        grid = createGrid(width - 180, height, step);
        axises = createAxis(width - 180, height);
        groupPane.getChildren().add(axises);
        axises.toBack();
        groupPane.getChildren().add(grid);
        grid.toBack();
    }

    @FXML
    private void rotateWithAxis() {
        groupPane.getChildren().remove(rotationPoint);
        showInform("Set param", "Set rotation point");
        groupPane.setOnMouseClicked(event -> {
            double fx = toFakeX(event.getX());
            double fy = toFakeY(event.getY());
            rotationPoint = new HintedDot(event.getX(), event.getY(), dotCount++);
            rotationPoint.setTooltip(fx, fy);
            groupPane.getChildren().add(rotationPoint);
            double degrees = Double.parseDouble(rotateField.getText());
            figure.rotate(rotationPoint.getCenterX(), rotationPoint.getCenterY(), degrees);
            groupPane.setOnMouseClicked(null);
        });
    }

    @FXML
    private void rotate() {
        groupPane.getChildren().remove(rotationPoint);
        rotationPoint = new HintedDot(toRealX(0), toRealY(0), dotCount++);
        double degrees = Double.parseDouble(rotateField.getText());
        figure.rotate(rotationPoint.getCenterX(), rotationPoint.getCenterY(), degrees);
    }

    @FXML
    private void move() {
        double x;
        double y;
        if (deltaX.getText().isEmpty()) {
            x = 0;
        } else {
            x = Double.parseDouble(deltaX.getText());
        }
        if (deltaY.getText().isEmpty()) {
            y = 0;
        } else {
            y = Double.parseDouble(deltaY.getText());
        }
        figure.move(x, y);
    }

    @FXML
    private void affinis() {
        double xx = Double.parseDouble(affXX.getText()) / step;
        double xy = Double.parseDouble(affXY.getText()) / step;
        double yx = Double.parseDouble(affYX.getText()) / step;
        double yy = Double.parseDouble(affYY.getText()) / step;
        double dx = Double.parseDouble(affDX.getText());
        double dy = Double.parseDouble(affDY.getText());

        axises.affinis(xx, xy, yx, yy, dx, dy);
        grid.affinis(xx, xy, yx, yy, dx, dy);
        figure.affinis(xx, xy, yx, yy, dx, dy);

        clip();
    }

    @FXML
    private void project() {
        double xx = Double.parseDouble(projXX.getText());
        double xy = Double.parseDouble(projXY.getText());
        double xw = Double.parseDouble(projXW.getText());
        double yx = Double.parseDouble(projYX.getText());
        double yy = Double.parseDouble(projYY.getText());
        double yw = Double.parseDouble(projYW.getText());
        double x = Double.parseDouble(projX.getText());
        double y = Double.parseDouble(projY.getText());
        double w = Double.parseDouble(projW.getText());

        figure.project(xx, xy, xw, yx, yy, yw, x, y, w);
        axises.project(xx, xy, xw, yx, yy, yw, x, y, w);
        grid.project(xx, xy, xw, yx, yy, yw, x, y, w);
        clip();
    }

    @FXML
    private void showImage() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(MainApp.getMainStage());
        Pane pane = new Pane();
        Image image = new Image(getClass().getResourceAsStream("/figure.png"));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        pane.getChildren().add(imageView);
        Scene dialogScene = new Scene(pane, 450, 450);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}