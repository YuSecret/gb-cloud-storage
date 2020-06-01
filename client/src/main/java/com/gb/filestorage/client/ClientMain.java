package com.gb.filestorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ClientGUI.fxml"));
        primaryStage.setTitle("com.gb.filestorage.client File Storage");
        primaryStage.setScene(new Scene(root, 860, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
