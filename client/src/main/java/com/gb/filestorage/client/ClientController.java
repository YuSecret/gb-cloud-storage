package com.gb.filestorage.client;

import com.gb.filestorage.common.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
    Label clientPath;

    @FXML
    Label serverPath;

    @FXML
    TextField login;

    @FXML
    TextField password;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootPathClient =  Paths.get("client_storage");
        clientPath.setText(rootPathClient.toAbsolutePath().toString());
        refreshLocalList();
       //gotoPath(rootClient, clientFilesList);
    }

    public void refreshServerList(List<String> files) {
        Platform.runLater(() -> {
            serverPath.setText(rootPathServer.toAbsolutePath().toString());
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
// evants
    public void onClientAuthentic(MouseEvent mouseEvent) throws IOException {
        System.out.println("onClientUpdate");
        Client.sendToServer(new AuthenticationRequest(login.getText(), password.getText()));
    }

    public void clientConnect() throws IOException {
        Client.start();
        Thread t = new Thread( () -> {
            try {
                System.out.println("thread start");
                while (true) {
                    System.out.println("thread start cikl");
                    AbstractMessage am = Client.readFromServer();
                    System.out.println("onClientConnect Client read object");
                    if (am instanceof FileMessage) {
                        System.out.println("onClientConnect FileMessage");
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage", fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalList();
                    }
                    if (am instanceof UpdateRequest) {
                        System.out.println("onClientConnect UpdateMessage");
                        UpdateRequest um = (UpdateRequest) am;
                        rootPathServer = Paths.get(um.getCurrentServerDir());
                        refreshServerList(um.getFileList());
                    }
                    if (am instanceof CloseConnectionRequest) {
                        System.out.println("onClientConnect CloseConnectionRequest");
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                Client.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        Client.sendToServer(new UpdateRequest(new ArrayList<>(), ""));
    }
    public void onClientDisconnect(MouseEvent mouseEvent) {
        System.out.println("onClientDisconnect");
        Client.sendToServer(new CloseConnectionRequest());
    }
    public void onClientDownLoadClick(MouseEvent mouseEvent) throws IOException {
        System.out.println("onClientDownLoadClick");
        FileRequest fr = new FileRequest(serverFilesList.getSelectionModel().getSelectedItem(), rootPathServer.toAbsolutePath().toString());
        Client.sendToServer(fr);
    }
    public void onServerDownLoadClick(MouseEvent mouseEvent) throws IOException {
        System.out.println("onServerDownLoadClick run!!!");
        if (Files.exists(Paths.get(rootPathClient.toAbsolutePath().toString(), clientFilesList.getSelectionModel().getSelectedItem()))) {
            System.out.println("На клиенте файл есть");
            FileMessage fm = new FileMessage(Paths.get(rootPathClient.toAbsolutePath().toString(), clientFilesList.getSelectionModel().getSelectedItem()));
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
        Client.sendToServer(new DeleteRequest(serverFilesList.getSelectionModel().getSelectedItem()));
    }

    public void onServerDeleteClick(MouseEvent mouseEvent) throws IOException {
        Files.delete(Paths.get(rootPathClient.toAbsolutePath().toString(), clientFilesList.getSelectionModel().getSelectedItem()));
        refreshLocalList();
    }
}
