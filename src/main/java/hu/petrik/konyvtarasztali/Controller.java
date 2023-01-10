package hu.petrik.konyvtarasztali;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.Optional;

public class Controller {

    @FXML
    private TableColumn<Konyv, String> authorfield;
    @FXML
    private Button torlesButton;
    @FXML
    private TableColumn<Konyv, Integer> pages_field;
    @FXML
    private TableColumn<Konyv, String> titlefield;
    @FXML
    private TableView<Konyv> tableview;
    @FXML
    private TableColumn<Konyv, Integer> publish_field;
    private KonyvDB db;

    @FXML
    private void initialize() {
        titlefield.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorfield.setCellValueFactory(new PropertyValueFactory<>("author"));
        publish_field.setCellValueFactory(new PropertyValueFactory<>("publish_year"));
        pages_field.setCellValueFactory(new PropertyValueFactory<>("page_count"));
        try {
            db = new KonyvDB();
            tableview.getItems().addAll(db.readKonyv());
        } catch (SQLException e) {
            Platform.runLater(() -> {
                sqlAlert(e);
                Platform.exit();
            });
        }
    }

    private void sqlAlert(SQLException e) {
        alert(Alert.AlertType.ERROR,
                "Hiba történt az adatbázis kapcsolat kialakításakor",
                e.getMessage());
    }

    private Optional<ButtonType> alert(Alert.AlertType alertType, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }

    @FXML
    public void torlesClick(ActionEvent actionEvent) {
        Konyv selected = getSelectedKonyv();
        if (selected == null){
            alert(Alert.AlertType.WARNING, "Törléshez előbb válasszon ki könyvet", "");
            return;
        }
        Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                "Biztos szeretné törölni a kiválasztott könyvet?", "");
        if (optionalButtonType.isEmpty() ||
                (!optionalButtonType.get().equals(ButtonType.OK) &&
                        !optionalButtonType.get().equals(ButtonType.YES))) {
            return;
        }

        try {
            if (db.deleteKonyv(selected.getId())) {
                alert(Alert.AlertType.WARNING, "Sikeres törlés", "");
            } else {
                alert(Alert.AlertType.WARNING, "Sikertelen törlés", "");
            }
            tableview.setItems(FXCollections.observableList(db.readKonyv()));
        } catch (SQLException e) {
            sqlAlert(e);
        }
    }

    private Konyv getSelectedKonyv() {
        int selectedIndex = tableview.getSelectionModel().getSelectedIndex();
        return tableview.getSelectionModel().getSelectedItem();
    }
}