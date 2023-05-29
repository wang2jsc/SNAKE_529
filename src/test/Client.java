package test;

import constant.Const;
import constant.Direction;
import controller.SnakeActionController;
import entity.SnakeCell;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import task.MoveTask;
import view.SnakePane;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//changed


public class Client extends Application {

    private File file = new File("users.txt");
    public static void main(String[] args) {
        launch(args);
    }
    public static int current_direction = Direction.R;
    private volatile static boolean isRequestClose = false;
    private GraphicsContext context; //changed

    @Override
    public void start(Stage primaryStage) throws Exception {

        Label titleLabel = new Label("Greedy Snake");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label userLabel = new Label("User:");
        TextField userField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            String user = userField.getText();
            String password = passwordField.getText();
            HashMap<String, String> userData = loadUserData();

            if (!userData.containsKey(user)) {
                userData.put(user, password);
                saveUserData(userData);
                enterGame(primaryStage, user);
            } else if (userData.get(user).equals(password)) {
                enterGame(primaryStage, user);
            } else {
                Alert alert = new Alert(Alert.AlertType.NONE, "Incorrect password.",
                        new ButtonType("Try Again"));
                alert.setContentText("Your password is not compatible with your account!\nPlease try again. ");
                alert.setTitle("WARNING");
                alert.showAndWait();
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.addRow(1, userLabel, userField);
        gridPane.addRow(2, passwordLabel, passwordField);
        gridPane.add(loginButton, 0, 3, 2, 1);

        Image image = new Image(getClass().getResourceAsStream("/image/SNAKE.jpg"));
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(991);
        imageView.setFitHeight(704);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView, gridPane);

        StackPane.setAlignment(gridPane, Pos.CENTER);
        Scene scene = new Scene(stackPane);
        primaryStage.setTitle("Greedy Snake Login");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void enterGame(Stage primaryStage, String user) {

        primaryStage.setTitle("Snake (by wzy & zzy)");
        SnakePane pane = new SnakePane();

        pane.drawCell();
        pane.setAccountName(user);

        Scene scene = new Scene(pane, 850, 800);
        scene.setOnKeyPressed(new SnakeActionController());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        showDifferentButton(primaryStage,pane);
        showDifferentSpeed(pane);
        showDifferentMap(pane);
        showTime(primaryStage);

    }

    static class MyTaskTimer extends Service<Void>{
        private int startNumber = -1;
        public void setStartNumber(int startNumber) {
            this.startNumber = startNumber;
        }
        public int getStartNumber(){return startNumber;}
        private static int currentTime;
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (startNumber == -1)startNumber = 0;
                    if (startNumber == 1000)startNumber = 0;
                    for (int i = startNumber; i <= 1000 ; i++) {
                        currentTime = i;
                        updateMessage(Integer.toString(i));
                        Thread.sleep(1000);
                        if (MoveTask.isOverBound || MoveTask.isEatItSelf) {
                            cancel();
                            break;
                        }
                    }
                    return null;
                }
            };
        }
    }

    private void showDifferentButton(Stage primaryStage, SnakePane pane){
        MyTaskTimer myTaskTimer = new MyTaskTimer();
        AtomicInteger showNumber = new AtomicInteger(-1);

        Button btnStop = new Button("Pause");
        btnStop.setFont(Font.font(10));
        btnStop.setLayoutX(650);
        btnStop.setLayoutY(300);
        pane.getChildren().add(btnStop);
        btnStop.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            MoveTask.isPaused = !MoveTask.isPaused;
            if (MoveTask.isPaused) {
                btnStop.setText("Resume");
            } else {
                btnStop.setText("Pause");
            }
        });
        btnStop.setOnAction(actionEvent -> {
            if (showNumber.get() == -1) {
            } else {
                String message = myTaskTimer.getMessage();
                myTaskTimer.cancel();
                showNumber.set(Integer.parseInt(message));
                MyTaskTimer.currentTime = Integer.parseInt(message);
            }
            if (!MoveTask.isPaused) {
                myTaskTimer.setStartNumber(MyTaskTimer.currentTime);
                myTaskTimer.restart();
            }

        });

        Button btnMusic = new Button("Music");
        btnMusic.setFont(Font.font(10));
        btnMusic.setLayoutX(650);
        btnMusic.setLayoutY(350);
        pane.getChildren().add(btnMusic);
        MusicPalyer musicPlayer = new MusicPalyer();

        btnMusic.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                String filepath = "snake.wav";
                if (musicPlayer.isMusicPlaying) {
                    musicPlayer.stopMusic();
                } else {
                    musicPlayer.playMusic(filepath);
                }
            }
        });
        Button btnPlay = new Button(" Play ");
        btnPlay.setFont(Font.font(10));
        btnPlay.setLayoutX(650);
        btnPlay.setLayoutY(400);
        pane.getChildren().add(btnPlay);
        btnPlay.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 2 ) {
                action(primaryStage, pane);
                btnPlay.setStyle("Restart");
            } else if(event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 1 ){
                preStart(pane);
            }
        });
        btnPlay.setOnAction(actionEvent -> {
            String message = myTaskTimer.getMessage();
            if(message == null || message.length() < 1){
                if(showNumber.get() == -1){
                    showNumber.set(0);
                }
                myTaskTimer.setStartNumber(showNumber.get());
            }else{
                myTaskTimer.setStartNumber(showNumber.get());
            }
            myTaskTimer.restart();
        });

        Button btnQuit = new Button(" Quit ");
        btnQuit.setFont(Font.font(10));
        btnQuit.setLayoutX(650);
        btnQuit.setLayoutY(450);
        pane.getChildren().add(btnQuit);
        btnQuit.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Platform.exit();
            } else {}
        });

        HashMap<String, Double> positions = new HashMap<>();

        Button btnSave = new Button(" Save ");
        btnSave.setFont(Font.font(10));
        btnSave.setLayoutX(720);
        btnSave.setLayoutY(300);
        pane.getChildren().add(btnSave);
        btnSave.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Const.SNAKE_CELL_LIST.stream().forEach(cell -> {
                    positions.put("snakeX" + Const.SNAKE_CELL_LIST.indexOf(cell), cell.getX());
                    positions.put("snakeY" + Const.SNAKE_CELL_LIST.indexOf(cell), cell.getY());
                });
                positions.put("beanX", pane.getFood().getX());
                positions.put("beanY", pane.getFood().getY());
                positions.put("score", (double) pane.getScore());

                try (PrintWriter out = new PrintWriter("save.txt")) {
                    positions.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().toString()).forEach(out::println);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnLoad = new Button(" Load ");
        btnLoad.setFont(Font.font(10));
        btnLoad.setLayoutX(720);
        btnLoad.setLayoutY(350);
        pane.getChildren().add(btnLoad);
        btnLoad.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                try (Stream<String> stream = Files.lines(Paths.get("save.txt"))) {
                    stream.map(line -> line.split(":")).forEach(kv -> positions.put(kv[0], Double.parseDouble(kv[1])));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Const.SNAKE_CELL_LIST.stream().forEach(cell -> {
                    double x = positions.get("snakeX" + Const.SNAKE_CELL_LIST.indexOf(cell));
                    double y = positions.get("snakeY" + Const.SNAKE_CELL_LIST.indexOf(cell));
                    double scoreDouble = positions.getOrDefault("score", 0.0);
                    int score = (int) scoreDouble;
                    pane.setScore(score);
                    cell.setX(x);
                    cell.setY(y);
                });
                double beanX = positions.get("beanX");
                double beanY = positions.get("beanY");

                pane.getFood().setX(beanX);
                pane.getFood().setY(beanY);

                pane.repaint();
            }
        });

        Button btnRanking = new Button("Ranking");
        btnRanking.setFont(Font.font(10));
        btnRanking.setLayoutX(720);
        btnRanking.setLayoutY(450);
        pane.getChildren().add(btnRanking);
        Stage rankingStage = new Stage();
        rankingStage.setTitle("Ranking");

        VBox rankingBox = new VBox();
        rankingBox.setSpacing(10);
        rankingBox.setAlignment(Pos.CENTER);
        MoveTask moveTask = new MoveTask(pane);
        HashMap<String, Integer> highestScores = moveTask.loadHighestData();
        
        List<Map.Entry<String, Integer>> sortedScores = highestScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : sortedScores) {
            Label label = new Label(entry.getKey() + ": " + entry.getValue());
            label.setFont(Font.font(14));
            rankingBox.getChildren().add(label);
        }

        Scene rankingScene = new Scene(rankingBox, 300, 400);
        rankingStage.setScene(rankingScene);
        btnRanking.setOnAction(event -> rankingStage.show());

        Button btnRestart = new Button(" Restart ");
        btnRestart.setFont(Font.font(10));
        btnRestart.setLayoutX(720);
        btnRestart.setLayoutY(400);
        pane.getChildren().add(btnRestart);
        btnRestart.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 1) {
                preStart(pane);
            }
        });
        btnRestart.setOnAction(actionEvent -> {
            myTaskTimer.restart();
            myTaskTimer.setStartNumber(-1);
            myTaskTimer.cancel();
            showNumber.set(-1);
            pane.setDuration(showNumber.get());
            myTaskTimer.restart();
        });


        Label time = new Label();
        time.textProperty().bind(myTaskTimer.messageProperty());
        time.setLayoutX(715);
        time.setLayoutY(135);
        pane.getChildren().add(time);

    }
    private void showDifferentSpeed(SnakePane pane){
        Button btnDifficulty1  = new Button("Difficulty1");
        btnDifficulty1.setFont(Font.font(10));
        btnDifficulty1.setLayoutX(425);
        btnDifficulty1.setLayoutY(775);
        pane.getChildren().add(btnDifficulty1);
        btnDifficulty1.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 1) {
                pane.setSpeed(500);
            } else {}
        });
        btnDifficulty1.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().getName().equals(KeyCode.B.getName())) {
                pane.setSpeed(500);
            } else {}
        });
        Button btnDifficulty2  = new Button("Difficulty2");
        btnDifficulty2.setFont(Font.font(10));
        btnDifficulty2.setLayoutX(495);
        btnDifficulty2.setLayoutY(775);
        pane.getChildren().add(btnDifficulty2);
        btnDifficulty2.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 1) {
                pane.setSpeed(200);
            } else {}
        });
        btnDifficulty2.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().getName().equals(KeyCode.N.getName())) {
                pane.setSpeed(200);
            } else {}
        });
        Button btnDifficulty3  = new Button("Difficulty3");
        btnDifficulty3.setFont(Font.font(10));
        btnDifficulty3.setLayoutX(565);
        btnDifficulty3.setLayoutY(775);
        pane.getChildren().add(btnDifficulty3);
        btnDifficulty3.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    && event.getClickCount() == 1) {
                pane.setSpeed(100);
            } else {}
        });
        btnDifficulty3.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().getName().equals(KeyCode.M.getName())) {
                pane.setSpeed(100);
            } else {}
        });
    }
    private void showDifferentMap(SnakePane pane){
        Button btnMap1  = new Button("snakeMap1");
        btnMap1.setFont(Font.font(10));
        btnMap1.setLayoutX(420);
        btnMap1.setLayoutY(735);
        pane.getChildren().add(btnMap1);

        Button btnMap2  = new Button("snakeMap2");
        btnMap2.setFont(Font.font(10));
        btnMap2.setLayoutX(490);
        btnMap2.setLayoutY(735);
        pane.getChildren().add(btnMap2);

        Button btnMap3  = new Button("snakeMap3");
        btnMap3.setFont(Font.font(10));
        btnMap3.setLayoutX(560);
        btnMap3.setLayoutY(735);
        pane.getChildren().add(btnMap3);
    }
    private HashMap<String, String> loadUserData() {
        HashMap<String, String> userData = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                userData.put(data[0], data[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userData;
    }

    private void saveUserData(HashMap<String, String> userData) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String user : userData.keySet()) {
                writer.write(user + "," + userData.get(user));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void action(Stage primaryStage, SnakePane pane){
        preStart(pane);
        SnakeCell food = new SnakeCell(190, 150, 20, Color.BLACK, "rectangle");
        pane.setFood(food);
        MoveTask task = new MoveTask(pane);
        task.setOnCancelled(event -> {
            if (MoveTask.isForceCancel) {
                String msg = "";
                if (MoveTask.isOverBound)
                    msg = "OverBound!";
                if (MoveTask.isEatItSelf)
                    msg = "AteYourself!";
                Alert alert = new Alert(Alert.AlertType.NONE, msg + "\nPLAY AGAIN?",
                        new ButtonType("AGAIN", ButtonBar.ButtonData.YES)
                        , new ButtonType("EXIT", ButtonBar.ButtonData.NO)
                        , new ButtonType("HOMEPAGE", ButtonBar.ButtonData.APPLY));
                alert.setTitle("Do you want to start again?");
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.isPresent()) {
                    if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                        MoveTask.isForceCancel = false;
                        MoveTask.reDetect();
                        action(primaryStage, pane);
                    }else if(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.NO)) {
                        exit(task);
                        Platform.exit();
                    }else{
                        MoveTask.isForceCancel = false;
                        MoveTask.reDetect();
                        pane.rerepaint();
                    }
                }
            }
        });

        new Thread(task).start();

        Task<Integer> task1 = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                while (!isRequestClose) {
                    if (MoveTask.isEatItSelf || MoveTask.isOverBound) {
                        task.cancel(true);
                        MoveTask.isForceCancel = true;
                        break;
                    }

                }
                return null;
            }

        };
        new Thread(task1).start();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                exit(task);
            }
        });
    }


    private void showTime(Stage primaryStage){
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        EventHandler<ActionEvent> eventHandler = e -> {
            primaryStage.setTitle("Snake (by wzy & zzy)           "
                    + df.format(new Date()));
        };
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private synchronized void preStart(SnakePane pane) {
        Const.SNAKE_CELL_LIST.clear();
        current_direction = Direction.R;
        SnakeCell cell = new SnakeCell(290, 410, 20, Color.GREEN, "rectangle");
        SnakeCell cell1 = new SnakeCell(310, 410, 20, Color.GREEN, "rectangle");
        SnakeCell cell2 = new SnakeCell(330, 410, 20, Color.GREEN, "rectangle");

        Const.SNAKE_CELL_LIST.add(cell);
        Const.SNAKE_CELL_LIST.add(cell1);
        Const.SNAKE_CELL_LIST.add(cell2);

        pane.setScore(0);
        pane.setSpeed(500);
        pane.setDuration(0);
        pane.setHighestScore(0);
    }

    private void exit(MoveTask task) {
        isRequestClose = true;
        task.cancel();
    }
}
