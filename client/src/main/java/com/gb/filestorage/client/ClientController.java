package com.gb.filestorage.client;

import com.gb.filestorage.common.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    Path rootPathClient;
    Path rootPathServer;
    @FXML
    ListView<String> serverFilesList;

    @FXML
    ListView<String> clientFilesList;

    @FXML
    Label lbl_clientPath;

    @FXML
    Label lbl_serverPath;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    Label lbl_state;

    @FXML
    Label lbl_password;

    @FXML
    Button btn_connect;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootPathClient =  Paths.get("client_storage");
        lbl_clientPath.setText(rootPathClient.toAbsolutePath().toString());
        lbl_state.setText("Отключено");
        refreshLocalList();
       //gotoPath(rootClient, clientFilesList);
    }

    public void refreshServerList(List<String> files) {
        Platform.runLater(() -> {
            lbl_serverPath.setText(rootPathServer.toAbsolutePath().toString());
            serverFilesList.getItems().clear();
            serverFilesList.getItems().addAll(files);
        });
    }
    public void refreshLocalList() {
        Platform.runLater(() -> {
            clientFilesList.getItems().clear();
            try {
                List<String> files = new ArrayList<>();
                Files.list(rootPathClient).map(path -> path.getFileName().toString()).forEach(files::add);
                clientFilesList.getItems().addAll(files);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onClientAuthentic(MouseEvent mouseEvent) throws IOException {
        clientConnect();
        System.out.println("onClientAuthentication "+login.getText());
        Client.sendToServer(new AuthenticationRequest(login.getText(), password.getText()));
    }

    public void clientConnect() throws IOException {
        Client.start();
        Thread t = new Thread( () -> {
            try {
                while (true) {
                    AbstractMessage am = Client.readFromServer();
                    if (am instanceof AuthenticationRequest) {
                        System.out.println("ClientConnect AuthenticationRequest");
                        onAuthentic(true);
                        Client.sendToServer(new UpdateRequest(new ArrayList<>(), ""));
                    }
                    if (am instanceof FileMessage) {
                        System.out.println("ClientConnect FileMessage");
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage", fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalList();
                    }
                    if (am instanceof UpdateRequest) {
                        System.out.println("ClientConnect UpdateMessage");
                        UpdateRequest um = (UpdateRequest) am;
                        rootPathServer = Paths.get(um.getCurrentServerDir());
                        refreshServerList(um.getFileList());
                    }
                    if (am instanceof CloseConnectionRequest) {
                        System.out.println("ClientConnect CloseConnectionRequest");
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                onAuthentic(false);
                Client.stop();
            }
        });
        t.setDaemon(true);
        t.start();
       // Client.sendToServer(new UpdateRequest(new ArrayList<>(), ""));
    }
    public void onAuthentic(boolean isAthentic) {
        if (isAthentic) {
            login.setEditable(false);
            password.setVisible(false);
            lbl_password.setVisible(false);
            btn_connect.setVisible(false);
            lbl_state.setText("Подключено");
            //lbl_state.setTextFill(Color.GREEN);
        }
        else {
            login.setEditable(true);
            password.setVisible(true);
            lbl_password.setVisible(true);
            btn_connect.setVisible(true);
            lbl_state.setText("Отключено");
            //lbl_state.setTextFill(Color.BLACK);
        }
    }
    public void onClientDisconnect(MouseEvent mouseEvent) {
        System.out.println("onClientDisconnect");
        Client.sendToServer(new CloseConnectionRequest());
    }
    public void onClientDownLoadClick(MouseEvent mouseEvent) throws IOException {
        String item = serverFilesList.getSelectionModel().getSelectedItem();
        if (item == null || item.isEmpty()) {return;}
        FileRequest fr = new FileRequest(item, rootPathServer.toAbsolutePath().toString());
        Client.sendToServer(fr);
    }
    public void onServerDownLoadClick(MouseEvent mouseEvent) throws IOException {
        String item = clientFilesList.getSelectionModel().getSelectedItem();
        if (item == null || item.isEmpty()) {return;}
        if (Files.exists(Paths.get(rootPathClient.toAbsolutePath().toString(), item))) {
            System.out.println("На клиенте файл есть");
            FileMessage fm = new FileMessage(Paths.get(rootPathClient.toAbsolutePath().toString(), item));
            Client.sendToServer(fm);
        }
    }
    public void onClientFilesListDblClick(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {
            String clickedItem = clientFilesList.getSelectionModel().getSelectedItem();
            /*
            if (fileMessage !=null) {
                if (fileMessage.isDirectory()) {
                    Path pathTo = rootPathClient.resolve(fileMessage.getFileName());
                    gotoPath(pathTo, clientFilesList);
                }
                if (fileMessage.isUpElement()) {
                    Path pathTo = rootPathClient.toAbsolutePath().getParent();
                    gotoPath(pathTo, clientFilesList);
                }
            }
             */
        }
    }

    public void onClientExit(MouseEvent mouseEvent) {
        Platform.exit();
        Client.stop();
    }

    public void onClientDeleteClick(MouseEvent mouseEvent) {
        String item = serverFilesList.getSelectionModel().getSelectedItem();
        if (item == null || item.isEmpty()) {return;}
        Client.sendToServer(new DeleteRequest(item));
    }

    public void onServerDeleteClick(MouseEvent mouseEvent) throws IOException {
        String item = clientFilesList.getSelectionModel().getSelectedItem();
        if (item == null || item.isEmpty()) {return;}
        Files.delete(Paths.get(rootPathClient.toAbsolutePath().toString(), item));
        refreshLocalList();
    }
}
