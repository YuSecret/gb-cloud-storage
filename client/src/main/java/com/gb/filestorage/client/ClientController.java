package com.gb.filestorage.client;

import com.gb.filestorage.common.AbstractMessage;
import com.gb.filestorage.common.FileMessage;
import com.gb.filestorage.common.FileRequest;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
public class ClientController implements Initializable {

    Path rootClient;
    Path rootServer;
    @FXML
    ListView<FileMessage> serverFilesList;

    @FXML
    ListView<FileMessage> clientFilesList;

    @FXML
    Label clientPath;

    @FXML
    Label serverPath;
    public void gotoPath(Path path, ListView<FileMessage> listView) {
        if (listView != clientFilesList) { return; }

        clientPath.setText(rootClient.toAbsolutePath().toString());
        rootClient = path;
        listView.getItems().clear();
        listView.getItems().add(new FileMessage(FileMessage.upDir, -2L));
        listView.getItems().addAll(scanFiles(path));
        listView.getItems().sort(new Comparator<FileMessage>() {
            @Override
            public int compare(FileMessage o1, FileMessage o2) {
                if (o1.getFileName().equals(FileMessage.upDir)) {
                    return -1;
                }
                if ((int) Math.signum(o1.getFileSize())==(int) Math.signum(o2.getFileSize())) {
                    return o1.getFileName().compareTo(o2.getFileName());
                }
                return new Long(o1.getFileSize() - o2.getFileSize()).intValue();
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //refreshLocalList();
       rootClient =  Paths.get("client_storage");
       clientPath.setText(rootClient.toAbsolutePath().toString());

       rootServer = Paths.get("server_storage");
       serverPath.setText(rootServer.toAbsolutePath().toString());

       refreshFilelists();

       //gotoPath(rootClient, clientFilesList);
    }
    public void refreshFilelists() {

        System.out.println("rootServer: "+rootServer.toAbsolutePath().toString());
        List<FileMessage> filesServer = scanFiles(rootServer);
        serverFilesList.getItems().clear();
        serverFilesList.getItems().addAll(filesServer);

        for (FileMessage f : filesServer ) {
            System.out.println("server "+f.getFileName());
        }


        List<FileMessage> filesClient = scanFiles(rootClient);
        clientFilesList.getItems().clear();
        clientFilesList.getItems().addAll(filesClient);

        for (FileMessage f : filesClient ) {
            System.out.println("client "+f.getFileName());
        }


        clientFilesList.setCellFactory(new Callback<ListView<FileMessage>, ListCell<FileMessage>>() {
            @Override
            public ListCell<FileMessage> call(ListView<FileMessage> param) {
                return new ListCell<FileMessage>() {
                    @Override
                    protected void updateItem(FileMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        }
                        else {
                            String formattedFileName = String.format("%-30s", item.getFileName());
                            String formattedFileSize = String.format("%,d bytes", item.getFileSize());
                            if (item.getFileSize() == -1L) {
                                formattedFileSize = String.format("%s", "[ DIR ]");
                            }
                            if (item.getFileSize() == -2L) {
                                formattedFileSize = "";
                            }
                            String st = String.format("%s %-20s", formattedFileName, formattedFileSize);
                            setText(st);
                        }
                    }
                };
            }

        });

        serverFilesList.setCellFactory(new Callback<ListView<FileMessage>, ListCell<FileMessage>>() {
            @Override
            public ListCell<FileMessage> call(ListView<FileMessage> param) {
                return new ListCell<FileMessage>() {
                    @Override
                    protected void updateItem(FileMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        }
                        else {
                            String formattedFileName = String.format("%-30s", item.getFileName());
                            String formattedFileSize = String.format("%,d bytes", item.getFileSize());
                            if (item.getFileSize() == -1L) {
                                formattedFileSize = String.format("%s", "[ DIR ]");
                            }
                            if (item.getFileSize() == -2L) {
                                formattedFileSize = "";
                            }
                            String st = String.format("%s %-20s", formattedFileName, formattedFileSize);
                            setText(st);
                        }
                    }
                };
            }

        });

    }
    public List<FileMessage> scanFiles(Path root) {

        List<FileMessage> out = new ArrayList<>();
        try {
            List<Path> pathsInRoot = Files.list(root).collect(Collectors.toList());
            System.out.println("scanfiles "+root.toAbsolutePath().getFileName()+" "+pathsInRoot.toString());
            for (Path p : pathsInRoot) {
                out.add(new FileMessage(p));
                System.out.println("scanfiles added: "+p.toAbsolutePath().getFileName()+" "+p.toString());
            }
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            new RuntimeException("Files scan exception: "+ root.toString());
            System.out.println("Exception^ ");
        }
        finally {
            return out;
        }
    }

    public void refreshLocalList() {
        Platform.runLater(() -> {
            clientFilesList.getItems().clear();
            List<FileMessage> files = scanFiles(rootClient);
            clientFilesList.getItems().addAll(files);
            //Files.list(Paths.get()).map(path -> path.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
            //.forEach(o -> clientFilesList.getItems().add(o))
        });
    }
// evants
    public void onClientConnect(MouseEvent mouseEvent) throws IOException {
        Client.start();
        Thread t = new Thread( () -> {
            try {
                while (true) {
                    AbstractMessage am = Client.readFromServer();
                    System.out.println("onClientConnect Client read object");
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        System.out.println("onClientConnect Client read object fm"+fm.getFileName());
                        Files.write(Paths.get("client_storage", fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalList();
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
    }
    public void onClientDownLoadClick(MouseEvent mouseEvent) {
        //refreshLocalList();
        System.out.println("onClientDownLoadClick run!!!");
        System.out.println("onClientDownLoadClick clientFilesList: " +clientFilesList);
        System.out.println("onClientDownLoadClick clientFilesList.getSelectionModel(): " +clientFilesList.getSelectionModel());
        clientFilesList.getSelectionModel().select(0);
        //Object object = clientFilesList.getSelectionModel().getSelectedItem();
        System.out.println("onClientDownLoadClick clientFilesList.getSelectionModel().getSelectedItem(): " +clientFilesList.getSelectionModel().getSelectedItem());
        FileMessage fileMessage = serverFilesList.getSelectionModel().getSelectedItem();
        System.out.println("fileMessage: "+fileMessage);
        System.out.println(fileMessage.getFileName());

        if (!fileMessage.isDirectory() && !fileMessage.isUpElement()) {
            Path path = rootServer.resolve(fileMessage.getFileName());
            System.out.println("Забрать файл на сервере: "+path.toAbsolutePath().toString());
            Client.sendToServer(new FileRequest(fileMessage.getFileName(), path.toAbsolutePath().toString()));
        }


    }
    public void onClientFilesListClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            FileMessage fileMessage = clientFilesList.getSelectionModel().getSelectedItem();
            if (fileMessage !=null) {
                if (fileMessage.isDirectory()) {
                    Path pathTo = rootClient.resolve(fileMessage.getFileName());
                    gotoPath(pathTo, clientFilesList);
                }
                if (fileMessage.isUpElement()) {
                    Path pathTo = rootClient.toAbsolutePath().getParent();
                    gotoPath(pathTo, clientFilesList);
                }
            }
        }
    }
    public void onServerFilesListClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            FileMessage fileMessage = serverFilesList.getSelectionModel().getSelectedItem();
            if (fileMessage !=null) {
                if (fileMessage.isDirectory()) {
                    Path pathTo = rootClient.resolve(fileMessage.getFileName());
                    gotoPath(pathTo, serverFilesList);
                }
                if (fileMessage.isUpElement()) {
                    Path pathTo = rootClient.toAbsolutePath().getParent();
                    gotoPath(pathTo, serverFilesList);
                }
            }
        }
    }
}
