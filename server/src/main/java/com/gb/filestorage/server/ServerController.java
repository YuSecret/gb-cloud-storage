package com.gb.filestorage.server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ServerController {
    @FXML
    TextArea log;
    @FXML
    Label status;
    @FXML
    Button btnStartServer;

    private final CloudServer server = new CloudServer(8189, this);
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    public void actionServerStart(ActionEvent actionEvent) throws Exception {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    server.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        status.setText("started");

        btnStartServer.setVisible(false);
    }
    public void putLog(String msg) {
        log.appendText(dateFormat.format(System.currentTimeMillis())+" "+msg +"\n");
    }

}
