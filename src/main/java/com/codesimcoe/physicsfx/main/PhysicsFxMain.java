package com.codesimcoe.physicsfx.main;

import com.codesimcoe.physicsfx.configuration.Configuration;
import com.codesimcoe.physicsfx.ui.ConfigurationUI;
import com.codesimcoe.physicsfx.ui.PhysicsUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PhysicsFxMain extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        PhysicsUI physicsUI = new PhysicsUI();
        ConfigurationUI configurationUI = new ConfigurationUI();

        BorderPane root = new BorderPane();
        root.setCenter(physicsUI.getNode());
        root.setRight(configurationUI.getNode());

        Scene scene = new Scene(root, Configuration.UI_WIDTH, Configuration.UI_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        EventHandler<ActionEvent> update = event -> {
            physicsUI.update();
            physicsUI.draw();
        };

        //
        double fps = 30;
        double frameDurationMs = 1000 / fps;

        Duration duration = Duration.millis(frameDurationMs);
        Animation loop = new Timeline(new KeyFrame(duration, update));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();

        primaryStage.setOnCloseRequest(e -> {
            loop.stop();
            System.exit(0);
        });
    }
}