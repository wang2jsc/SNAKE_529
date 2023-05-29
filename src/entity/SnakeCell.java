package entity;

import javafx.scene.paint.Color;

public class SnakeCell {
    private double x;
    private double y;
    private double cellLen;
    private Color color;
    private String shade;
    public SnakeCell(int x, int y, double cellLen, Color color, String shade){
        this.x = x;
        this.y = y;
        this.cellLen = cellLen;
        this.color = color;
        this.shade = shade;
    }
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setCellLen(double cellLen) {
        this.cellLen = cellLen;
    }
    public void setColor(Color color) {this.color = color;}
    public void setShade(String shade) {this.shade = shade;}
    public double getX() {return x;}
    public double getY() {return y;}
    public double getCellLen() {return cellLen;}
    public Color getColor() {return color;}
    public String getShade() {return shade;}

    @Override
    public String toString() {
        return "SnakeCell{" + "x=" + x + ", y=" + y + ", cellLen=" + cellLen +
                ", color=" + color + ", shade='" + shade + '\'' + '}';
    }
}
