package com.gb.filestorage.server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.io.IOException;

public class ServerController implements  ServerListener {
    @FXML
    TextArea log;
    @FXML
    Label status;
    private final Server server = new Server(this);

    public void actionServerStart(ActionEvent actionEvent) throws IOException {
        server.start(8189);
        status.setText("Server started!");
    }

    public void actionServerStop(ActionEvent actionEvent) throws IOException {
        server.stop();
        status.setText("Server stopped!");
    }
    @Override
    public void onServerMessage(Server server, String msg) {
        if ("".equals(msg)) return;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.appendText(msg + "\n");
            }
        });
    }
}
