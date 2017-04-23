package parts;

import UI.Menu;
import UI.ScreenHandler;
import UI.ScreenMaster;
import common.Database;
import diagrep.logic.BookingController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.AlertBox;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

//controller for the main parts window
public class PartsController implements Initializable, ScreenMaster {

    @FXML
    private Button closeBtn;
    @FXML
    private Button minBtn;
    @FXML
    private BorderPane draggable;
    @FXML
    private Label title;

    //////////////////////////////////////////
    //buttons for parts
    @FXML
    private Button addstock;
    @FXML
    private Button newstock;
    @FXML
    private Button editstock;

    @FXML
    private Button viewdel;
    @FXML
    private Button adddel;


    /////////////////////////////////////////////
    //button for bookings
    @FXML
    private Button dltbook;

    ////////////////////////////////////////////////
    // buttons for repairs
    @FXML
    private Button addpart;

    @FXML
    private Button edtpart;
    @FXML
    private Button dltpart;

    //////////////////////////////////////////////////
    //buttons for search
    @FXML
    private Button srchpart;
    @FXML
    private Button srchlist;

    //////////////////////////////////////////////////

    @FXML
    private Label repairidL;
    @FXML
    private Label partnameL;
    @FXML
    private Label partnameL2;
    @FXML
    private Label partcostL;

    @FXML
    private Label partidL;

    @FXML
    private Label stocklevelL;
    @FXML
    private Label descriptionL;
    @FXML
    private Label partcostL2;
    @FXML
    private Label IDnumL;
    @FXML
    private Label installdateL;
    @FXML
    private Label warrantydateL;
    @FXML
    private Label costL;

    @FXML
    private Button sRep;


    @FXML
    private Label custfnameL;
    @FXML
    private Label custsnameL;
    @FXML
    private Label regL;
    @FXML
    private Label covwarrL;

    @FXML
    private Label billL;
    @FXML
    private Label warrantcovL;

    ///////////////////////////////////////////////////////////////
    //repairs table
    @FXML
    private TableView<PartsData> parttable;

    @FXML
    private TableColumn<PartsData, String> idcol;
    @FXML
    private TableColumn<PartsData, String> namecol;
    @FXML
    private TableColumn<PartsData, String> installcol;
    @FXML
    private TableColumn<PartsData, String> warrantycol;

    @FXML
    private TableColumn<PartsData, String> regcol;
    @FXML
    private TableColumn<PartsData, String> snamecol;
    @FXML
    private TableColumn<PartsData, String> fnamecol;
    @FXML
    private TableColumn<PartsData2, String> bookidcol2;
///////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////
    //booking table
    @FXML
    private TableView<PartsData2> booktable;

    @FXML
    private TableColumn<PartsData2, String> bookidcol;
    @FXML
    private TableColumn<PartsData2, String> typecol;
    @FXML
    private TableColumn<PartsData2, String> datecol;


    @FXML
    private TableColumn<PartsData2, String> vehregcol;
    @FXML
    private TableColumn<PartsData2, String> snamecol2;
    @FXML
    private TableColumn<PartsData2, String> fnamecol2;
///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    //booking2 table
    @FXML
    private TableView<PartsData2> booktable2;

    @FXML
    private TableColumn<PartsData2, String> bookidcolv2;
    @FXML
    private TableColumn<PartsData2, String> typecolv2;
    @FXML
    private TableColumn<PartsData2, String> datecolv2;


    @FXML
    private TableColumn<PartsData2, String> vehregcolv2;
    @FXML
    private TableColumn<PartsData2, String> snamecolv2;
    @FXML
    private TableColumn<PartsData2, String> fnamecolv2;
///////////////////////////////////////////////////////////////
//combobox, choicebox, search, and tabs

    @FXML
    private ComboBox<String> partdroplist;
    @FXML
    private ChoiceBox<String> filterdroplist;
    @FXML
    private TextField searchField;

    @FXML
    private TabPane chatTab;

    @FXML private Button navUP, navVech, navOut, navCust, navBook;


    private Database db;

    private ScreenHandler sh;

    ///////////////////////////////////////////////////
    // variables

    //store current part selected in parts combobox
    public static String currentpart = null;

    public static int currentrepair=-1;

    //stores the current filter selected
    private String currentfilter = "";

    public static int selectedpid=-1;
    private static int selectedrid =-1;

    //store repair id when adding repair
    public static int addrid=-1;

    static int editid = 0; //used to see what repair is being selected in the repairs table
    static boolean status=true;

    ///////////////////////////////////////////////////


    //set all the combobox, tables and choiceboxes
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();
        Menu.loadTopBars(title, closeBtn, minBtn, draggable);
        navOut.setOnAction(e -> logout());
        navUP.setOnAction(e -> goToUP());
        navCust.setOnAction(e -> goToCust());
        navBook.setOnAction(e -> goToBookings());
        navVech.setOnAction(e -> goToVeh());
        sRep.setOnAction(e -> goToSPC());



        Fields();
        Fields2();
        Fields3();

        filterdroplist.setItems(fillchoicebox());
        partdroplist.setItems(fillcombobox());
        parttable.setItems(loadPartsData());
        booktable.setItems(loadPartsData2("past"));
        booktable2.setItems(loadPartsData2("future"));

        //added listeners for all the tables, choicebox, combobox, tabs
        filterdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        currentfilter = newValue
        );

        partdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    //currentpart = newValue.toString()
                    try {
                        updateparts(newValue.toString());
                    }
                    catch(NullPointerException e) {}
                }
        );

        parttable.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                    updaterepairs(parttable.getSelectionModel().getSelectedItem().getrepairid(), parttable.getSelectionModel().getSelectedItem().getprtid());
                }
                    catch(NullPointerException e) {}
                }
        );

        booktable.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) ->{
                    try{
                        calculatebill(booktable.getSelectionModel().getSelectedItem().getbookid());
                }
                    catch(NullPointerException e) {}
                }
        );
        booktable2.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        calculatebill(booktable2.getSelectionModel().getSelectedItem().getbookid());
                    }
                    catch(NullPointerException e) {}
                }
        );
        //if past bookings tab is selected, the status is changed.
        //if future bookings tab is selected, the status is changed
        chatTab.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if(status){
                status=false;
            }
            else{
                status=true;
            }
        });

    }

    //get the data for the repair table
    private ObservableList<PartsData> loadPartsData() {
        ObservableList<PartsData> list = FXCollections.observableArrayList();
        //query for every column needed
        String query = "SELECT PartforRepair.RepairID, PartforRepair.InstallDate, PartforRepair.ExpiryDate, Parts.PartName, Parts.PartID, Bookings.RegNumber, Bookings.BookingType, Bookings.BookingID, Customers.Firstname, Customers.Surname FROM PartforRepair INNER " +
                "JOIN Parts ON PartforRepair.PartID = Parts.PartID " +
                "JOIN Bookings ON PartforRepair.RepairID=Bookings.BookingID" +
                " JOIN Customers ON Bookings.CustomerID = Customers.CustomerID";

        ResultSet rsParts = db.query(query);

        try {
            //only show the repair booking's parts
            while (rsParts.next()) {
                if (rsParts.getInt("BookingType") == 0) {
                    list.add(new PartsData(rsParts));
                }
            }
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }

        return list;
    }

    //get the data for the booking table
    private ObservableList<PartsData2> loadPartsData2(String tense) {
        ArrayList<PartsData2> list = new ArrayList<>();
        //get columns that are needed
        String query = "SELECT Bookings.BookingID, Bookings.BookingDate, Bookings.BookingType, Bookings.RegNumber, Customers.Firstname, Customers.Surname FROM Bookings" +
                " JOIN Customers ON Bookings.CustomerID = Customers.CustomerID";

        ResultSet rsParts = db.query(query);

        try {

                //for past booking get dates before today
                // for future bookings, get dates in the future
                while (rsParts.next()) {
                    LocalDate date = LocalDate.parse(rsParts.getString("BookingDate"));
                    if(tense.equals("past")) {
                        if (date.isBefore(LocalDate.now())) {
                            list.add(new PartsData2(rsParts));
                        }
                    }
                    else{
                        if (!date.isBefore(LocalDate.now())) {
                            list.add(new PartsData2(rsParts));
                        }
                    }
                }

        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }
        return FXCollections.observableArrayList(list);
    }

    private void Fields() {

        idcol.setCellValueFactory(new PropertyValueFactory<>("repairid"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("partname"));
        installcol.setCellValueFactory(new PropertyValueFactory<>("installdate"));
        warrantycol.setCellValueFactory(new PropertyValueFactory<>("warrantydate"));
        regcol.setCellValueFactory(new PropertyValueFactory<>("regnum"));
        fnamecol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        snamecol.setCellValueFactory(new PropertyValueFactory<>("sname"));
        bookidcol2.setCellValueFactory(new PropertyValueFactory<>("bookid2"));

    }

    private void Fields2() {

        vehregcol.setCellValueFactory(new PropertyValueFactory<>("vehreg"));
        bookidcol.setCellValueFactory(new PropertyValueFactory<>("bookid"));
        typecol.setCellValueFactory(new PropertyValueFactory<>("booktype"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("time"));
        fnamecol2.setCellValueFactory(new PropertyValueFactory<>("fname"));
        snamecol2.setCellValueFactory(new PropertyValueFactory<>("sname"));

    }
    private void Fields3() {

        vehregcolv2.setCellValueFactory(new PropertyValueFactory<>("vehreg"));
        bookidcolv2.setCellValueFactory(new PropertyValueFactory<>("bookid"));
        typecolv2.setCellValueFactory(new PropertyValueFactory<>("booktype"));
        datecolv2.setCellValueFactory(new PropertyValueFactory<>("time"));
        fnamecolv2.setCellValueFactory(new PropertyValueFactory<>("fname"));
        snamecolv2.setCellValueFactory(new PropertyValueFactory<>("sname"));

    }

    @FXML
    private void handleButtonAction(ActionEvent event)//for add button repair
            throws IOException {

        Stage stage;
        Parent root;

        if (event.getSource() == addpart) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("popup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addpart.getScene().getWindow());
            stage.showAndWait();


            parttable.setItems(loadPartsData());

            //update sum of part costs for a booking when a repair is deleted
            if(status){
                if(booktable.getSelectionModel().getSelectedItem() !=null &&booktable.getSelectionModel().getSelectedItem().getbookid()==addrid){
                    calculatebill(addrid);
                }
            }
            else{
                if(booktable2.getSelectionModel().getSelectedItem() !=null && booktable2.getSelectionModel().getSelectedItem().getbookid()==addrid){
                    calculatebill(addrid);
                }
            }


            //set labels to ...
            repairidL.setText("...");
            installdateL.setText("...");
            warrantydateL.setText("...");
            partnameL.setText("...");
            partcostL.setText("...");
            custfnameL.setText("...");
            custsnameL.setText("...");
            //////////////////////////////////////////////////////////////////////////





        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }

                //decrement stock level label if the part being added is also selcted in part details section

                stocklevelL.setText("...");
                int num = popcontroller.stocklv;
                num--;

                if(currentpart.equals(partnameL2.getText())) {

                    StringProperty valueProperty = new SimpleStringProperty(Integer.toString(num));
                    stocklevelL.textProperty().bind(valueProperty);
                    stocklevelL.textProperty().unbind();
                }

                parttable.setItems(loadPartsData());

                if(currentpart!=null){
                    updateparts(currentpart);
                }



    }

    @FXML
    private void handleButtonAction2(ActionEvent event)//for add new part in stocklist
            throws IOException {


        Stage stage;
        Parent root;

        if (event.getSource() == newstock) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("stockpopup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(newstock.getScene().getWindow());
            stage.showAndWait();
            partdroplist.setItems(fillcombobox());
        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }


    }

    @FXML
    private void handleButtonAction3(ActionEvent event)//for edit repair
            throws IOException {

        if (parttable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a repair First",
                    "Please select a repair to edit");
            return;
        }

        editid = parttable.getSelectionModel().getSelectedItem().getrepairid();

        Stage stage;
        Parent root;

        if (event.getSource() == edtpart) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("editpopup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(edtpart.getScene().getWindow());
            stage.showAndWait();

            parttable.setItems(loadPartsData());


            //update sum of part costs for a booking when a repair is deleted
            if(status){
                if(booktable.getSelectionModel().getSelectedItem() !=null &&booktable.getSelectionModel().getSelectedItem().getbookid()==editid){
                    calculatebill(editid);
                }
            }
            else{
                if(booktable2.getSelectionModel().getSelectedItem() !=null && booktable2.getSelectionModel().getSelectedItem().getbookid()==editid){
                    calculatebill(editid);
                }
            }

            repairidL.setText("...");
            installdateL.setText("...");
            warrantydateL.setText("...");
            partnameL.setText("...");
            partcostL.setText("...");
            custfnameL.setText("...");
            custsnameL.setText("...");
            //////////////////////////////////////////////////////////////////////////



        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }

                //decrement stock level label if the part being added is also selcted in part details section
                stocklevelL.setText("...");
                int num = editcontroller.stocklv;
                num--;

                if(currentpart.equals(partnameL2.getText())) {

                    StringProperty valueProperty = new SimpleStringProperty(Integer.toString(num));
                    stocklevelL.textProperty().bind(valueProperty);
                    stocklevelL.textProperty().unbind();
                }

                parttable.setItems(loadPartsData());
                if(currentpart!=null){
                    updateparts(currentpart);
                }

    }

    @FXML
    private void handleButtonAction4(ActionEvent event)//for edit stock
            throws IOException {

                if (partdroplist.getSelectionModel().getSelectedItem() == null) {
                    AlertBox.info("Warning", "Pick a part First",
                            "Please select a part to edit");
                    return;
                }

        Stage stage;
        Parent root;

        if (event.getSource() == editstock) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("editstockpopup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(editstock.getScene().getWindow());
            stage.showAndWait();


            //load tables
            partdroplist.setItems(fillcombobox());

            //reset part the labels
            partnameL2.setText("...");
            descriptionL.setText("...");
            stocklevelL.setText("...");
            partidL.setText("...");
            partcostL2.setText("...");

            //reload the combobox and table
            partdroplist.setItems(fillcombobox());
            parttable.setItems(loadPartsData());

            //update the labels
            updaterepairs(selectedrid,selectedpid);

	    //reset repair labels
	    if(parttable.getSelectionModel().getSelectedItem()==null){
                partnameL.setText("...");
                repairidL.setText("...");
                installdateL.setText("...");
                warrantydateL.setText("...");
                partcostL.setText("...");
		custsnameL.setText("...");
                custfnameL.setText("...");
                covwarrL.setText("...");
                regL.setText("...");


            }

        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }
    }

    @FXML
    private void handleButtonAction5(ActionEvent event)//for viewing deliveries
            throws IOException {

        Stage stage;
        Parent root;

        if (event.getSource() == viewdel) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("viewdeliverypopup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(viewdel.getScene().getWindow());
            stage.showAndWait();


        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }
    }

    @FXML
    private void handleButtonAction6(ActionEvent event)//for adding deliveries
            throws IOException {

        Stage stage;
        Parent root;

        if (event.getSource() == adddel) {
            stage = new Stage();
            root = FXMLLoader.load(getClass().getResource("deliverypopup.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("window");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(adddel.getScene().getWindow());
            stage.showAndWait();


        } else {
            //stage=(Stage)addrep.getScene().getWindow();
            //stage.close();
        }
    }


    public void updaterepairs(int num,int num2) { //

         selectedpid=num2;
         selectedrid =num;

        currentrepair=num2;

        ResultSet rsrepair = db.query("SELECT * FROM PartforRepair " +
                "WHERE RepairID =" +num+" AND PartID="+num2);
        try {
            //set labels
            repairidL.setText(rsrepair.getString("RepairID"));
            installdateL.setText(rsrepair.getString("InstallDate"));
            warrantydateL.setText(rsrepair.getString("ExpiryDate"));



            String Query0 = "SELECT * FROM Parts WHERE PartID ="+num2;
                    //+ rspfr.getInt("PartID");
            ResultSet rsparts = db.query(Query0);
            partnameL.setText(rsparts.getString("PartName"));

            ///////////////////////////////////////////////////////////////////////////


////////////add another 0 if the price is 1.0 or 1.1 or something
            String cost=rsparts.getString("PartCost");

            int dot = -1;

            for (int i = 0; i < cost.length(); i++) {
                if (cost.charAt(i) == '.')
                    dot = i;

            }

            if(dot == -1){
                cost=cost+"0";
            }
            else {
                String afterdec = cost.substring(dot);
                if (afterdec.length() <= 2) {
                    cost = cost + "0";
                }
            }

            /////////////////////////reset label of sum of parts
            boolean warrant = false;
            String query1 = "SELECT Bookings.RegNumber, Vehicle.Warranty FROM Bookings " +
                    "INNER JOIN Vehicle ON Bookings.RegNumber = Vehicle.RegNumber WHERE Bookings.BookingID=" + num;
            ResultSet warrbook = db.query(query1);
            warrant = warrbook.getBoolean("Warranty");
            if (warrant) {
                partcostL.setText("£" + cost + " (covered by warranty)");
                covwarrL.setText("YES");
            } else {
                partcostL.setText("£" + cost);
                covwarrL.setText("NO");
            }
            /////////////////////////


            //set labels for vehicle and customer details
            String Query2 = "SELECT * FROM Bookings WHERE BookingID =" + num;
            ResultSet rsbook = db.query(Query2);
            regL.setText(rsbook.getString("RegNumber"));


            String Query3 = "SELECT * FROM Customers WHERE CustomerID =" + rsbook.getInt("CustomerID");
            ResultSet rscust = db.query(Query3);
            custfnameL.setText(rscust.getString("Firstname"));
            custsnameL.setText(rscust.getString("Surname"));



        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


    public void updateparts(String name) { // sets the labels for the parts info
        currentpart = name;
        ResultSet rsparts = db.query("SELECT * FROM Parts WHERE PartName LIKE'%" + name + "%'");
        try {
            //set labels
            partnameL2.setText(rsparts.getString("PartName"));
            partidL.setText(rsparts.getString("PartID"));
            descriptionL.setText(rsparts.getString("Description"));
            stocklevelL.setText(rsparts.getString("StockLevel"));


            //check at what character number the dot is at
            String message=rsparts.getString("PartCost");
            int dot = -1;

            for (int i = 0; i < message.length(); i++) {

                if (message.charAt(i) == '.')
                    dot = i;

            }

            //if the price is 1.0 or something, add another 0
            if(dot == -1){
                partcostL2.setText("£"+rsparts.getString("PartCost")+"0");
                return;
            }

            //adding an extra 0 if price is .1 or something
            String afterdec = message.substring(dot);
            if (afterdec.length() <= 2) {
                partcostL2.setText("£"+rsparts.getString("PartCost")+"0");
            } else {
                partcostL2.setText("£"+rsparts.getString("PartCost"));
            }



        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void updateStock() throws SQLException { //used to add and update stock for a part

        //validations
        if (partdroplist.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a part First",
                    "Please select a part to add stock to");
            return;
        }

        /*
        boolean stock = AlertBox.choice("Confirmation", "Update Stock",
                "Do you want to add stock to " + currentpart + "?");
    */

        String ans = AlertBox.input("Update Stock", "Do you want to add stock to " + currentpart + "?");


        //validations
        if(isEmpty(ans)){
            AlertBox.info("Warning", "nothing typed",
                    "Please enter a number");
            return;
        }
        else if(!isInt(ans)){
            AlertBox.info("Warning", "number not typed",
                    "Please enter a number");
            return;
        }



        int input = Integer.parseInt(ans);

           //check if stock limit is reached (100)

            ResultSet rsparts1 = db.query("SELECT * FROM Parts WHERE PartName LIKE'%" + currentpart + "%'");
            int num = rsparts1.getInt("StockLevel");
        if (num == 100) {
            //System.out.println("stock is full");
            return;
        }

            //////////////////////////////////////////////////////////////////
            int prtid = rsparts1.getInt("PartID");

            try {
                String query2 = "SELECT * FROM StockDeliveries WHERE IsDelivered=" + 1 + " AND PartID=" + prtid + " AND QuantityLeft>0";
                ResultSet rsdel = db.query(query2);
                int quantnum = Integer.parseInt(rsdel.getString("QuantityLeft"));


               // for (int i = 1; i < 20; i++)
                    //System.out.println(quantnum);

                int delid = rsdel.getInt("DeliveryID");


                if ((quantnum - input) < 0) {//if input is larger than the quantity
                    AlertBox.info("Warning", "Not enough stock in warehouse",
                            "please withdraw from a range of: 1 to " + quantnum);
                    return;
                }

                //minus the quntity number from the input.
                quantnum = quantnum - input;

                ResultSet rsdel2 = db.query("UPDATE StockDeliveries SET QuantityLeft=" + quantnum + " WHERE DeliveryID=" + delid);

                //////////////////////////////////////////////////////////////////


                num = num + input;
                //String number=Integer.toString(num);


                //update stock level for a certain part
                ResultSet rsparts = db.query("UPDATE Parts SET StockLevel = " + num + " WHERE PartName LIKE'%" + currentpart + "%'");

                //reload the stock level label
                StringProperty valueProperty = new SimpleStringProperty(Integer.toString(num));
                stocklevelL.textProperty().bind(valueProperty);
                stocklevelL.textProperty().unbind();
            }
             catch (SQLException se) { //if stock has ran out
                 AlertBox.info("Warning", "Not enough stock in warehouse",
                         "out of stock! please order more stock " +
                                 "\n or wait for the stock to be delivered");

            }

        //}

    }

    public void deleteclicked() { //deletes a repair in the repairs table

        //if no repair is chosen to delete, a prompt will show up
        if (parttable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a part First",
                    "Please select a part to delete");
            return;
        }

        //a prompt is shown to confirm the delete
        boolean del = AlertBox.choice("Confirmation", "About to delete!",
                "Are you sure you want to delete the repair?");


        if (del) {
            try {

                //get the repair id and part name from the selected repair in the table
                int ridnum = parttable.getSelectionModel().getSelectedItem().getrepairid();
                String pname = parttable.getSelectionModel().getSelectedItem().getpartname();

                //query to get part name from part id
                ResultSet rsparts1 = db.query("SELECT PartID FROM Parts WHERE PartName LIKE'%" + pname + "%'");
                int pidnum = rsparts1.getInt("PartID");
                rsparts1.close();


                String Query = "DELETE FROM PartforRepair WHERE RepairID = ? AND PartID=?";

                //delete the repair
                PreparedStatement stmt = db.getCon().prepareStatement(Query);
                stmt.setInt(1, ridnum);
                stmt.setInt(2, pidnum);
                stmt.executeUpdate();
                parttable.setItems(loadPartsData());
                stmt.close();



                //update sum of part costs for a booking when a repair is deleted
                if(status){
                    if(booktable.getSelectionModel().getSelectedItem() !=null &&booktable.getSelectionModel().getSelectedItem().getbookid()==ridnum){
                        calculatebill(ridnum);
                    }
                }
                else{
                    if(booktable2.getSelectionModel().getSelectedItem() !=null && booktable2.getSelectionModel().getSelectedItem().getbookid()==ridnum){
                        calculatebill(ridnum);
                    }
                }



            } catch (SQLException se) {
                System.out.println("error");
            }
            //load table again
            parttable.setItems(loadPartsData());

        }
        //reset labels
        repairidL.setText("...");
        installdateL.setText("...");
        warrantydateL.setText("...");
        partnameL.setText("...");
        partcostL.setText("...");
        custfnameL.setText("...");
        custsnameL.setText("...");

    }

    public void deleteclicked2() { // Deletes a booking. when a booking is deleted, all repairs for that booking is deleted.

        //validation for deleting a booking. checks if a past or future booking is being deleted
        if(status) {
            if (booktable.getSelectionModel().getSelectedItem() == null) {
                AlertBox.info("Warning", "Pick a booking First",
                        "Please select a booking to delete");
                return;
            }
        }
        else{
            if (booktable2.getSelectionModel().getSelectedItem() == null) {
                AlertBox.info("Warning", "Pick a booking First",
                        "Please select a booking to delete");
                return;
            }
        }

        boolean del = AlertBox.choice("Confirmation", "About to delete!",
                "Are you sure you want to delete the booking?");
        //System.out.println(del);

        if (del) {
            try {
                //get the selected booking's booking id
                int idnum;
                if(status){
                    idnum = booktable.getSelectionModel().getSelectedItem().getbookid();
                }
                else{
                    idnum = booktable2.getSelectionModel().getSelectedItem().getbookid();
                }
                String Query = "DELETE FROM Bookings WHERE BookingID = ?";

                PreparedStatement stmt = db.getCon().prepareStatement(Query);
                //delete the booking
                stmt.setInt(1, idnum);
                stmt.executeUpdate();
				db.update("DELETE FROM RepairAndParts WHERE BookingID = " + idnum);
                stmt.close();
            } catch (SQLException se) {
                System.out.println("error");
            }
            //reload the tables
            if(status)
            booktable.setItems(loadPartsData2("past"));
            else
            booktable2.setItems(loadPartsData2("future"));

            parttable.setItems(loadPartsData());
			
			BookingController bookingController =(BookingController) ScreenHandler.getController(common.Main.BOOKINGS);
            bookingController.refreshTable();
        }

        //reset labels
        repairidL.setText("...");
        installdateL.setText("...");
        warrantydateL.setText("...");
        partnameL.setText("...");
        partcostL.setText("...");
        custfnameL.setText("...");
        custsnameL.setText("...");

        billL.setText("...");
        warrantcovL.setText("");

    }

    public void deleteclicked3() { // Delete a part

        //validations when deleting a part
        if (partdroplist.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a Part First",
                    "Please select a Part to delete");
            return;
        }

        boolean del = AlertBox.choice("Confirmation", "About to delete!",
                "Are you sure you want to delete the part?" +
                        "\n deleting this part will delete all the repairs that use this part");
        //System.out.println(currentpart);

        if (del) {//if delete has been confirmed
            try {

                //query for all columns for a particular part
                String Query0 = "Select * FROM Parts WHERE PartName LIKE'%" + currentpart + "%'";
                ResultSet rsParts = db.query(Query0);
                int id = rsParts.getInt("PartID");

                String Query = "DELETE FROM Parts WHERE PartID = ?";

                String Query2 = "DELETE FROM PartforRepair WHERE PartID=?";

                //delete part
                PreparedStatement stmt = db.getCon().prepareStatement(Query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();

                //delete partforrepair
                PreparedStatement stmt2 = db.getCon().prepareStatement(Query2);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();

                //calculate new sum of parts after part has been deleted
                if(status){
                    if(booktable.getSelectionModel().getSelectedItem() !=null &&booktable.getSelectionModel().getSelectedItem().getbookid()==booktable.getSelectionModel().getSelectedItem().getbookid()){
                        calculatebill(booktable.getSelectionModel().getSelectedItem().getbookid());
                    }
                }
                else{
                    if(booktable2.getSelectionModel().getSelectedItem() !=null && booktable2.getSelectionModel().getSelectedItem().getbookid()==booktable2.getSelectionModel().getSelectedItem().getbookid()){
                        calculatebill(booktable2.getSelectionModel().getSelectedItem().getbookid());
                    }
                }



            } catch (SQLException se) {
                System.out.println("error");
            }
            //load table and combobox
            partdroplist.setItems(fillcombobox());
            parttable.setItems(loadPartsData());

            //reset the labels
            partnameL2.setText("...");
            partidL.setText("...");
            descriptionL.setText("...");
            stocklevelL.setText("...");
            partcostL2.setText("...");



            if(partnameL.getText().equals(currentpart)){
                repairidL.setText("...");
                installdateL.setText("...");
                warrantydateL.setText("...");
                partnameL.setText("...");
                partcostL.setText("...");
                custfnameL.setText("...");
                custsnameL.setText("...");
            }


        }
    }


    private ObservableList<String> fillcombobox() { //fills the combobox with part names
        ArrayList<String> list = new ArrayList<>();
        //query to get part details
        String query = "SELECT PartName FROM Parts";
        ResultSet rsParts = db.query(query);

        try {
            while (rsParts.next())
                list.add(rsParts.getString("PartName"));
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<String> fillchoicebox() { //fills the choicebox with the name of the filters
        ArrayList<String> list = new ArrayList<>();
        list.add("Customer First Name");
        list.add("Customer Surname");
        list.add("Vehicle Registration");
        return FXCollections.observableArrayList(list);
    }

    @FXML
    public void search() { //searches while filtering the result based on whats selected in the choicebox

        FilteredList<PartsData> filteredData = new FilteredList<>(loadPartsData(), e -> true);
        searchField.setOnKeyPressed((KeyEvent e) -> {
            searchField.textProperty().addListener((observableValue, OldValue, newValue) -> {
                filteredData.setPredicate(PartsData -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();

                    //based on what it chosen in the choicebox, the repair table is filtered

                    if (currentfilter.equals("Customer First Name")) {

                        if (PartsData.getfname().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }
                    if (currentfilter.equals("Customer Surname")) {

                        if (PartsData.getsname().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }
                    if (currentfilter.equals("Vehicle Registration")) {

                        if (PartsData.getregnum().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                    }
                    return false;
                });

            });
            SortedList<PartsData> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(parttable.comparatorProperty());
            parttable.setItems(sortedData);
        });

    }

    private void calculatebill(int idnum) { //calculates the sum of part costs for a specific booking


        //query to check if vehicle is covered by warranty
        boolean warrant = false;
        String query0 = "SELECT Bookings.RegNumber, Vehicle.Warranty FROM Bookings " +
                "INNER JOIN Vehicle ON Bookings.RegNumber = Vehicle.RegNumber WHERE Bookings.BookingID=" + idnum;

        try {
            ResultSet rsbook = db.query(query0);
            warrant = rsbook.getBoolean("Warranty");
        } catch (SQLException se) {

        }

        //query to get a part cost for a part that is being used for a repair
            String query = "SELECT PartforRepair.RepairID, Parts.PartCost FROM PartforRepair INNER " +
                    "JOIN Parts ON PartforRepair.PartID = Parts.PartID WHERE PartforRepair.RepairID=" + idnum;

            ResultSet rsParts = db.query(query);

            double bill = 0;
            try {
                while (rsParts.next()) { //sum the part costs
                    bill = bill + rsParts.getDouble("PartCost");
                }
            } catch (SQLException se) {

            }

            //if the cost is .1 or something, add another 0
        String cost = Double.toString(bill);

        int dot = -1;

        for (int i = 0; i < cost.length(); i++) {

            if (cost.charAt(i) == '.')
                dot = i;

        }

        if(dot == -1){
            cost=cost+"0";
        }
        else {
            String afterdec = cost.substring(dot);
            if (afterdec.length() <= 2) {
                cost = cost + "0";
            }
        }


        if (warrant) { //if the vehicle has a warranty, it will by shown next to the price
            billL.setText("£" + cost);
            warrantcovL.setText("(covered by warranty)");

        } else {
            billL.setText("£" + cost);
            warrantcovL.setText("");
        }


    }

    //validation for integer
    private boolean isInt(String message){

        try{
            int n = Integer.parseInt(message);
            if(n>0)
                return true;
            else
                return false;

        }
        catch(NumberFormatException e){
            return false;
        }

    }
    //validation if empty string is entered
    private boolean isEmpty(String message){

        if(message.equals("")){
            return true;
        }
        else{
            return false;
        }

    }
    //Hossain: Added this method to refrsh table from my module
    public void refresh(){
        parttable.setItems(loadPartsData());
        booktable.setItems(loadPartsData2("past"));
        booktable2.setItems(loadPartsData2("future"));
    }

    private void logout() {
        Menu.logout(sh);
    }

    private void goToUP() {
        Menu.toUP(sh);
    }

    private void goToCust(){ Menu.toCust(sh); }

    private void goToBookings() { Menu.toBookings(sh); }

    private void goToVeh() { Menu.toVeh(sh); }

    private void goToSPC() { Menu.toSPC(); }

    @Override
    public void setScreenParent(ScreenHandler currentScreen) {
        sh = currentScreen;
    }
}
