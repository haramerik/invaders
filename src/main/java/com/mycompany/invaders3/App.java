package com.mycompany.invaders3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class App extends Application {

    private Pane root = new Pane();
    
    private Group group = new Group();
    
    private boolean game_over = false;

    private double t = 0;

    private Sprite player = new Sprite(300, 350, 40, 40, "player", Color.BLUE);

    private Parent createContent() {
        root.setPrefSize(600, 400);

        root.getChildren().add(player);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        timer.start();

        nextLevel();
        
        group.getChildren().add(root);

        return group;
    }

    private void nextLevel() {
        for (int i = 0; i < 5; i++) {
            Sprite s = new Sprite(90 + i*100, 150, 30, 30, "enemy", Color.PURPLE);

            root.getChildren().add(s);
        }
    }

    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite)n).collect(Collectors.toList());
    }

    private void update() {
        t += 0.016;
        
        sprites().forEach(s -> {
            switch (s.type) {

                case "enemybullet":
                    s.moveDown();

                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.dead = true;
                        s.dead = true;
                    }
                    break;

                case "playerbullet":
                    s.moveUp();

                    sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.dead = true;
                            s.dead = true;
                        }
                    });

                    break;

                case "enemy":

                    if (t > 2) {
                        if (Math.random() < 0.3) {
                            shoot(s);
                        }
                    }

                    break;
            }
        });

        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if (t > 2) {
            t = 0;
        }
    }

    private void shoot(Sprite who) {
        Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);

        root.getChildren().add(s);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            if(player.dead) {
                if(game_over == false) {
                    
                    Text t = new Text(250, 100, "GAME OVER!");
                    t.setFont(new Font(20));
                    t.setFill(Color.RED);
                    group.getChildren().add(t);

                    game_over = true;
                }
                return;
            }
            switch (e.getCode()) {
                case A:
                    player.moveLeft();
                    break;
                case D:
                    player.moveRight();
                    break;
                case SPACE:
                    shoot(player);
                    break;
                case W:
                    player.moveUp();
                    break;
                case S: 
                    player.moveDown();
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private static class Sprite extends Rectangle {
        boolean dead = false;
        final String type;

        Sprite(int x, int y, int w, int h, String type, Color color) {
            super(w, h, color);
            
            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }

        void moveLeft() {
            setTranslateX(getTranslateX() - 5);
        }

        void moveRight() {
            setTranslateX(getTranslateX() + 5);
        }

        void moveUp() {
            setTranslateY(getTranslateY() - 5);
        }

        void moveDown() {
            setTranslateY(getTranslateY() + 5);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
