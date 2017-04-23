package userpanel;

/**
 * Created by Prodige on 19/01/2017. //Belal Added stuff on 14/03/2017
 */
//#181722

import UI.Menu;
import UI.ScreenHandler;
import UI.ScreenMaster;
import common.Database;
import common.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import misc.AlertBox;
import users.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserPanelController implements Initializable, ScreenMaster {

    @FXML private Button closeBtn;
    @FXML private Button minBtn;
    @FXML private Pane adminSection;
    @FXML private Pane adminView;
    @FXML private Label title;
    @FXML private Label entUserId;
    @FXML private Label entFirst;
    @FXML private Label entLast;
    @FXML private Label entType;
    @FXML private BorderPane draggable;
    @FXML private Button btnCreateUser;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private TextField searchBox;
    @FXML private Button navVech, navCust, navParts, sRep;

    @FXML
    private TableView<AdminData> adminTable;
    @FXML
    private TableColumn<AdminData, Number> colID;
    @FXML
    private TableColumn<AdminData, String> colFirstname;
    @FXML
    private TableColumn<AdminData, String> colSurname;
    @FXML
    private TableColumn<AdminData, String> colPass;
    @FXML
    private TableColumn<AdminData, String> colType;

    private ScreenHandler sh;

    private static Database db;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        Menu.loadTopBars(title, closeBtn, minBtn, draggable);

        navCust.setOnAction(e -> goToCust());
        navVech.setOnAction(e -> goToVeh());
        navParts.setOnAction(e -> goToParts());
        sRep.setOnAction(e -> goToSPC());

        entUserId.setText(User.getUserID());
        entFirst.setText(User.getFirstName());
        entLast.setText(User.getSurname());
        if (User.isAdmin()) entType.setText("Administrator");
        else{
            adminSection.setVisible(false);
            adminView.setVisible(false);
            entType.setText("Standard Employee");
            return;
        }
        setFields();
        adminTable.setItems(fillTable());
        btnCreateUser.setOnAction(event -> showAddEdit(false));
        adminTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        btnEdit.setDisable(false);
                        btnDelete.setDisable(false);
                    } else {
                        btnEdit.setDisable(true);
                        btnDelete.setDisable(true);
                    }
                }
        );
        btnEdit.setOnAction(e -> showAddEdit(true));
        btnDelete.setOnAction(e -> deleteUser());
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> search());
    }

    private void deleteUser(){
        if(AlertBox.choice("Deleting", "You're about to delete a user",
                "Are you sure you want to delete this user?")) {
            String sql = "DELETE FROM Users WHERE ID = '"
                    + adminTable.getSelectionModel().getSelectedItem().getcolID() + "'";
            adminTable.getSelectionModel().clearSelection();
            db.update(sql);
            adminTable.setItems(fillTable());
        }
    }

    private void showAddEdit(boolean isEditing){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AddEditUser.fxml"));
            AnchorPane page = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initOwner(Main.stage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);

            AddEditController controller = loader.getController();
            controller.setDialog(dialog);
            if(isEditing) controller.setUser(adminTable.getSelectionModel().getSelectedItem());

            dialog.showAndWait();
            if(controller.isDone()) adminTable.setItems(fillTable());
        } catch (IOException e){
            e.printStackTrace();
            AlertBox.error("Loading failed", "Unable to load dialog. IO Exception caught.", e);
        }
    }

    private void setFields(){
        colID.setCellValueFactory(new PropertyValueFactory<AdminData, Number>("colID"));
        colFirstname.setCellValueFactory(new PropertyValueFactory<AdminData, String>("colFirstname"));
        colSurname.setCellValueFactory(new PropertyValueFactory<AdminData, String>("colSurname"));
        colPass.setCellValueFactory(new PropertyValueFactory<AdminData, String>("colPass"));
        colType.setCellValueFactory(new PropertyValueFactory<AdminData, String>("colType"));
    }

    private ObservableList<AdminData> fillTable(){
        String sql = "SELECT * FROM Users";
        ResultSet rs = db.query(sql);
        ArrayList<AdminData> data = new ArrayList<>();
        try{
            while(rs.next())
                data.add(new AdminData(rs));
        } catch(SQLException se){
            se.printStackTrace();
            AlertBox.error("Admin table error", "Unable to fill the table", se);
        }
        return FXCollections.observableArrayList(data);
    }

    private void search(){
        String toSearch = searchBox.getText();
        if (toSearch.isEmpty()){
            adminTable.setItems(fillTable());
            return;
        }
        //ResultSet rs = db.query("SELECT LastName FROM Users WHERE LastName LIKE '%" + toSearch + "%'");
        FilteredList<AdminData> filteredList = new FilteredList<>(fillTable(), predicate -> false);
        filteredList.setPredicate(adminData -> adminData.getcolSurname().toLowerCase().contains(toSearch));

        SortedList<AdminData> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(adminTable.comparatorProperty());
        adminTable.setItems(sortedList);
    }

    @FXML
    private void logout(){
        Menu.logout(sh);
    }

    @FXML
    private void goToBookings(){ Menu.toBookings(sh);}

    private void goToVeh(){ Menu.toVeh(sh); }

    private void goToParts(){ Menu.toParts(sh); }

    private void goToCust(){Menu.toCust(sh);}

    private void goToSPC() { Menu.toSPC(); }

    @Override
    public void setScreenParent(ScreenHandler currentScreen) {
        sh = currentScreen;
    }
}
