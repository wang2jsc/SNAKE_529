package controller;

import constant.Direction;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import task.MoveTask;
import test.Client;

public class SnakeActionController implements EventHandler<KeyEvent> {

    @Override
    public void handle(KeyEvent event) {

        if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {

            if ((event.getCode().getName().equals(KeyCode.A.getName())
                    || event.getCode().getName().equals(KeyCode.LEFT.getName()))
                    && Client.current_direction != Direction.R) {
                Client.current_direction = Direction.L;
            } else if ((event.getCode().getName().equals(KeyCode.D.getName())
                    || event.getCode().getName().equals(KeyCode.RIGHT.getName()))
                    && Client.current_direction != Direction.L) {
                Client.current_direction = Direction.R;
            } else if ((event.getCode().getName().equals(KeyCode.W.getName())
                    || event.getCode().getName().equals(KeyCode.UP.getName()))
                    && Client.current_direction != Direction.D) {
                Client.current_direction = Direction.U;
            } else if ((event.getCode().getName().equals(KeyCode.S.getName())
                    || event.getCode().getName().equals(KeyCode.DOWN.getName()))
                    && Client.current_direction != Direction.U) {
                Client.current_direction = Direction.D;
            }
        }
    }
}