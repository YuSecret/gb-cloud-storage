package com.gb.filestorage.client;

import com.db.filestorage.common.FileInfo;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientController implements Initializable {

    Path root;
    @FXML
    ListView<String> serverFilesList;

    @FXML
    ListView<FileInfo> clientFilesList;

    @FXML
    Label clientPath;
    public void gotoPath(Path path) {
        root = path;
        clientPath.setText(root.toAbsolutePath().toString());
        clientFilesList.getItems().clear();
        clientFilesList.getItems().add(new FileInfo(FileInfo.upDir, -2L));
        clientFilesList.getItems().addAll(scanFiles(path));
        clientFilesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.getFileName().equals(FileInfo.upDir)) {
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
        serverFilesList.getItems().addAll("11","3233","23","3232");

        List<FileInfo> files = scanFiles(root);
        clientFilesList.getItems().addAll(files);

        clientFilesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
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
        gotoPath(Paths.get("1"));
    }

    public List<FileInfo> scanFiles(Path root) {

        List<FileInfo> out = new ArrayList<>();
        try {
            List<Path> pathsInRoot = Files.list(root).collect(Collectors.toList());
            for (Path p : pathsInRoot) {
                out.add(new FileInfo(p));
            }
        }
        catch (IOException e) {
            new RuntimeException("Files scan exception: "+ root.toString());
        }
        finally {
            return out;
        }
    }

    public void onClientFilesListClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            FileInfo fileInfo = clientFilesList.getSelectionModel().getSelectedItem();
            if (fileInfo!=null) {
                if (fileInfo.isDirectory()) {
                    Path pathTo = root.resolve(fileInfo.getFileName());
                    gotoPath(pathTo);
                }
                if (fileInfo.isUpElement()) {
                    Path pathTo = root.toAbsolutePath().getParent();
                    gotoPath(pathTo);
                }
            }
        }
    }
}
