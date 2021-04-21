package ru.ntr.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import lombok.extern.log4j.Log4j;

@Log4j
public class ClientController implements Initializable {
    @FXML
    public TableView<FileEntity> fileTable;
    @FXML
    public TableColumn nameCol;
    @FXML
    private TableColumn sizeCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FileManager fileManager = new FileManager();
        ObservableList<FileEntity> files = FXCollections.observableList(fileManager.getFiles());
        nameCol.setCellValueFactory(
                new PropertyValueFactory<FileEntity, String>("name")
        );
        sizeCol.setCellValueFactory(
                new PropertyValueFactory<FileEntity, String>("size")
        );
        fileTable.setItems(files);

        fileTable.setRowFactory(tv -> {
            TableRow<FileEntity> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    FileEntity clickedFile = row.getItem();
                    fileManager.sendFile(clickedFile.getPath());
                }
            });
            return row;
        });
    }
}
