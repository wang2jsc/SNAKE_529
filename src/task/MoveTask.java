package task;

import constant.Const;
import constant.Direction;
import entity.SnakeCell;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import test.Client;
import view.SnakePane;

import java.io.*;
import java.util.*;

public class MoveTask extends Task<Integer> {
    private SnakePane pane;
    private Random random = new Random();
    public MoveTask(SnakePane pane) {this.pane = pane;}
    public volatile static boolean isForceCancel = false;
    public volatile static boolean isOverBound = false;
    public volatile static boolean isEatItSelf = false;
    public volatile static boolean isPaused = false;
    private int currentSize = 0;

    public static void reDetect() {
        isOverBound = false;
        isEatItSelf = false;
    }

    private boolean isEat(SnakeCell cell) {
        if (cell.getX() == pane.getFood().getX()
                && cell.getY() == pane.getFood().getY()) {
            pane.setScore(pane.getScore() + 1);
            return true;
        }
        return false;
    }

    private boolean isEatItSelf() {
        SnakeCell first = Const.SNAKE_CELL_LIST.getFirst();
        for (int i = 1; i < Const.SNAKE_CELL_LIST.size(); i++) {
            if (first.getX() == Const.SNAKE_CELL_LIST.get(i).getX()
                    && first.getY() == Const.SNAKE_CELL_LIST.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Integer call() throws Exception {
        while (!isCancelled() && !isForceCancel) {
            if (isOverBound || isEatItSelf) {
                cancel();
                break;
            }
            if (!isPaused) {
                if (Const.SNAKE_CELL_LIST.getFirst().getX() < 0
                        || Const.SNAKE_CELL_LIST.getFirst().getY() < 0
                        || Const.SNAKE_CELL_LIST.getFirst().getX() > 600
                        || Const.SNAKE_CELL_LIST.getFirst().getY() > 700) {
                    isOverBound = true;
                    continue;
                }
                if (Const.SNAKE_CELL_LIST.size() > 3 && isEatItSelf()) {
                    isEatItSelf = true;
                    continue;
                }
                String user = pane.getAccountName();
                int score = pane.getScore();
                HashMap<String, Integer> highestScores = loadHighestData();
                if (!highestScores.containsKey(user)) {
                    highestScores.put(user, score);
                    saveHighestData(highestScores);
                } else {
                    int highestScore = highestScores.get(user);
                    if (score >= highestScore) {
                        highestScores.put(user, score);
                        saveHighestData(highestScores);
                        pane.setHighestScore(score);

                    }

                }

                SnakeCell cell;
                switch (Client.current_direction) {
                    case Direction.L:
                        cell = Const.SNAKE_CELL_LIST.removeLast();
                        cell.setX(Const.SNAKE_CELL_LIST.getFirst().getX() - 20);
                        cell.setY(Const.SNAKE_CELL_LIST.getFirst().getY());
                        Const.SNAKE_CELL_LIST.addFirst(cell);
                        if (isEat(cell)) {
                            controlFood(Direction.L);
                        }
                        break;
                    case Direction.R:
                        cell = Const.SNAKE_CELL_LIST.removeLast();
                        cell.setX(Const.SNAKE_CELL_LIST.getFirst().getX() + 20);
                        cell.setY(Const.SNAKE_CELL_LIST.getFirst().getY());
                        Const.SNAKE_CELL_LIST.addFirst(cell);
                        if (isEat(cell)) {
                            controlFood(Direction.R);
                        }
                        break;
                    case Direction.U:
                        cell = Const.SNAKE_CELL_LIST.removeLast();
                        cell.setY(Const.SNAKE_CELL_LIST.getFirst().getY() - 20);
                        cell.setX(Const.SNAKE_CELL_LIST.getFirst().getX());
                        Const.SNAKE_CELL_LIST.addFirst(cell);
                        if (isEat(cell)) {
                            controlFood(Direction.U);
                        }
                        break;
                    case Direction.D:
                        cell = Const.SNAKE_CELL_LIST.removeLast();
                        cell.setY(Const.SNAKE_CELL_LIST.getFirst().getY() + 20);
                        cell.setX(Const.SNAKE_CELL_LIST.getFirst().getX());
                        Const.SNAKE_CELL_LIST.addFirst(cell);
                        if (isEat(cell)) {
                            controlFood(Direction.D);
                        }
                        break;
                }
                pane.repaint();
                Thread.sleep(pane.getSpeed());
            } else {
                Thread.sleep(500);
            }
        }
        return null;
    }

    private void controlFood(int dir) {
        SnakeCell food = pane.getFood();
        switch (dir) {
            case Direction.L:
                food.setX(Const.SNAKE_CELL_LIST.getFirst().getX() + 20);
                break;
            case Direction.R:
                food.setX(Const.SNAKE_CELL_LIST.getFirst().getX() - 20);
                break;
            case Direction.D:
                food.setY(Const.SNAKE_CELL_LIST.getFirst().getY() - 20);
                break;
            case Direction.U:
                food.setY(Const.SNAKE_CELL_LIST.getFirst().getY() + 20);
                break;
        }
        food.setColor(Color.GREEN);
        Const.SNAKE_CELL_LIST.addLast(food);

        SnakeCell food1 = createFood();
        while (checkIsOnSnakeBody(food1)) {
            food1 = createFood();
        }
        pane.setFood(food1);
    }

    public HashMap<String, Integer> loadHighestData() {
        HashMap<String, Integer> highestData = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("highest.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                highestData.put(data[0], Integer.parseInt(data[1]));
                if(data[0].equals(pane.getAccountName())) {
                    pane.setHighestScore(Integer.parseInt(data[1]));
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highestData;
    }

    private void saveHighestData(HashMap<String, Integer> highestData) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("highest.txt"));
            for (String username : highestData.keySet()) {
                writer.write(username + "," + highestData.get(username));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    private boolean checkIsOnSnakeBody(SnakeCell food) {
        for (SnakeCell cell : Const.SNAKE_CELL_LIST) {
            if (cell.getY() == food.getY() && cell.getX() == food.getX())
                return true;
        }
        return false;
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    private SnakeCell createFood() {
        int x = random.nextInt(600);
        x = x - x % 20;
        if (x / 10 % 10 % 2 == 0)
            x = x - 10;
        if (x < 50)
            x = 50;
        int y = random.nextInt(700);
        y = y - y % 20;
        if (y / 10 % 10 % 2 == 0)
            y = y - 10;
        if (y < 50)
            y = 50;
        return new SnakeCell(x, y, Const.cellLen, Color.BLACK, "rectangle");
    }
}