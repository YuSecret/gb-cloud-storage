package com.gb.filestorage.server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

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
    @FXML
    Button btnStopServer;
    private final CloudServer server = new CloudServer(8189, this);
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private boolean isRun = false;

    public void actionServerStart(ActionEvent actionEvent) throws Exception {
        if (isRun) {return;}
        isRun = true;
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
        status.setTextFill(Paint.valueOf("#05a482"));
    }
    public void putLog(String msg) {
        log.appendText(dateFormat.format(System.currentTimeMillis())+" "+msg +"\n");
    }

    public void actionServerStop(ActionEvent actionEvent) throws Exception {
        if (!isRun) {return;}
        server.stop();
        isRun = false;
        status.setText("stopped");
        status.setTextFill(Paint.valueOf("#000000"));
    }
}
