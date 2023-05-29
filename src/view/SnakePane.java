package view;

import constant.Const;
import entity.SnakeCell;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;

public class SnakePane extends Pane {
    private Canvas canvas;
    private GraphicsContext context;
    private List<SnakeCell> cells = Const.SNAKE_CELL_LIST;
    private int speed = 500;
    private int score = 0;
    private SnakeCell food;
    private int duration;
    private int highestScore;
    private String accountName;
    public void setSpeed(int speed) {this.speed = speed;}
    public void setScore(int score){
        this.score = score;
    }
    public void setFood(SnakeCell food){
        this.food = food;
    }
    public void setDuration(int duration){this.duration = duration;}
    public void setHighestScore(int highestScore){this.highestScore = highestScore;}
    public void setAccountName(String accountName){this.accountName = accountName;}
    public int getSpeed() {return speed;}
    public int getScore(){return score;}
    public int getDuration(){return duration;}
    public int getHighestScore(){return highestScore;}
    public SnakeCell getFood(){return food;}
    public String getAccountName(){return accountName;}
    public void drawCell(){
        cells.forEach(cell->{
            context.setFill(cell.getColor());
            context.fillRect(cell.getX(), cell.getY(), cell.getCellLen(), cell.getCellLen());
        });
    }
    //changed
    public SnakePane() {
        canvas = new Canvas(850, 800);
        context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setStroke(Color.GREEN);
        context.setFill(Color.LIGHTGRAY);
        context.setFont(Font.font("Cascadia Code SemiBold",12)); //ONLY
        context.fillRect(0,0,630,730);
        context.strokeRect(0,0,630,730);
        context.setStroke(Color.BLACK);
        context.strokeRect(630,0,170,800);

        context.strokeText("Hi! ", 632,12,200);

        context.strokeText("Current Score: " + score ,660, 50, 100);
        context.strokeRect(630,0,170,250);
        context.strokeText("Speed:" + speed ,660, 100, 100);
        context.strokeText("TimeAlive: " + duration + " s",660,150,100);
        context.strokeText("History Highest: " + highestScore,660,200,150);
        context.strokeRect(630,250,170,250);
        context.strokeText("RANKING LIST",670,520,200);;
        context.strokeRect(630,500,170,300);
        context.strokeRect(0,730,630,100);

        context.strokeRect(350,730,280,70);
        context.strokeText("Difficulties: ",352,790,100);
        context.strokeText("MAPS",362,750,100);

        context.setStroke(Color.RED);
        context.strokeText("World's worest ProjectFrame -- The Frame From SA!!!"
                ,0,750,688);
        context.setStroke(Color.GREEN);
        context.setFont(Font.font("Magneto",50));
        context.strokeText("Greedy SNAKE",95,365,500);

        getChildren().add(canvas);
    }
    public void repaint(){
        context.setFont(Font.font("Cascadia Code SemiBold",12));
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setFill(Color.LIGHTYELLOW);
        context.fillRect(0,0,630,730);
        context.setStroke(Color.GREEN);
        context.strokeRect(0,0,630,730);
        context.setStroke(Color.BLACK);
        context.strokeRect(630,0,170,800);

        context.strokeText("Hi, " + accountName + "! ", 632,12,200);

        context.strokeText("Current Score: " + score ,660, 50, 100);
        context.strokeRect(630,0,170,250);
        context.strokeText("Speed:" + speed ,660, 100, 100);
        context.strokeText("TimeAlive: " + "            s",660,150,100);
        context.strokeText("History Highest: " + highestScore,660,200,150);
        context.strokeRect(630,250,170,250);
        context.strokeText("RANKING LIST",670,520,200);;
        context.strokeRect(630,500,170,300);
        context.strokeRect(0,730,630,100);

        context.strokeRect(350,730,280,70);
        context.strokeText("Difficulties: ",352,790,100);
        context.strokeText("MAPS",362,750,100);

        context.setStroke(Color.RED);
        context.strokeText("World's worest ProjectFrame -- The Frame From SA!!!"
                ,0,750,688);

        cells.forEach(cell->{
            context.setFill(cell.getColor());
            context.fillRect(cell.getX(), cell.getY(), cell.getCellLen(), cell.getCellLen());
        });
        context.setFill(food.getColor());
        context.fillRect(food.getX(), food.getY(), food.getCellLen(), food.getCellLen());
    }
    public void rerepaint(){
        context.setStroke(Color.GREEN);
        context.setFill(Color.LIGHTGRAY);
        context.setFont(Font.font("Cascadia Code SemiBold",12)); //ONLY
        context.fillRect(0,0,630,730);
        context.strokeRect(0,0,630,730);
        context.setStroke(Color.BLACK);
        context.strokeRect(630,0,170,800);

        context.strokeText("Hi! ", 632,12,200);

        context.strokeText("Current Score: " + score ,660, 50, 100);
        context.strokeRect(630,0,170,250);
        context.strokeText("Speed:" + speed ,660, 100, 100);
        //context.strokeText("TimeAlive: " + duration + " s",660,150,100);
        context.strokeText("History Highest: " + highestScore,660,200,150);
        context.strokeRect(630,250,170,250);
        context.strokeText("RANKING LIST",670,520,200);;
        context.strokeRect(630,500,170,300);
        context.strokeRect(0,730,630,100);

        context.strokeRect(350,730,280,70);
        context.strokeText("Difficulties: ",352,790,100);
        context.strokeText("MAPS",362,750,100);

        context.setStroke(Color.RED);
        context.strokeText("World's worest ProjectFrame -- The Frame From SA!!!"
                ,0,750,688);
        context.setStroke(Color.GREEN);
        context.setFont(Font.font("Magneto",50));
        context.strokeText("Greedy SNAKE",95,365,500);
    }
}