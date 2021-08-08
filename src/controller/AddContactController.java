package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddContactController {
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private DatePicker dob;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private Label lblFirstName;
    @FXML
    private Label lblLastName;
    @FXML
    private Label lblPhone;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lbldob;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnClose;
    private ContactController contactController;
    private List<Contact> contacts;

    public void setAddContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @FXML
    void initialize() throws Exception {
        lblFirstName.setText("");
        lblLastName.setText("");
        lblEmail.setText("");
        lblPhone.setText("");
        lbldob.setText("");
        cbGroup.getItems().clear();

        for (Group x : new GroupDAO().loadGroup("src/data/group.txt")) {
            cbGroup.getItems().add(x);
        }
        cbGroup.getSelectionModel().select(0);
        dob.setValue(LocalDate.now());

    }

    //saveContact method
    public void saveContact() throws Exception {
        String fname = firstName.getText().trim();
        if (fname.isEmpty()) {
            lblFirstName.setText("First Name cannot be empty");
            return;
        }
        lblFirstName.setText("");

        String lname = lastName.getText().trim();
        if (lname.isEmpty()) {
            lblLastName.setText("Last Name cannot be empty");
            return;
        }
        lblLastName.setText("");

        String phoneNumber = phone.getText().trim();
        if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d+")) { //one or more digit
            lblPhone.setText("Phone number contains digit only");
            return;
        }
        lblPhone.setText("");

        String mail = this.email.getText().trim();
        Pattern emailNamePtrn = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = emailNamePtrn.matcher(mail);
        if (!matcher.matches()) {
            lblEmail.setText("Email is invalid");
            return;
        }
        lblEmail.setText("");
        String birthDate = (dob.getValue().toString());
        String group = ((Group) this.cbGroup.getSelectionModel().getSelectedItem()).getName();
        Contact contact = new Contact(fname, lname, phoneNumber, mail, birthDate, group);
        //check existed contact
        if (contactController.contactDAO.indexof(contacts, contact) == -1) {
            this.contactController.contactDAO.saveToList(this.contacts, contact);
            this.contactController.showContact(this.contacts);
            contactController.contactDAO.saveToFile(contacts, "src/data/contact.txt");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("New Contact has been added");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Information of contact is existed");
            alert.showAndWait();
        }
    }

    //save contact event
    public void saveContact(ActionEvent actionEvent) throws Exception {
        if (actionEvent.getSource() == btnAdd) {
            saveContact();
        } else if (actionEvent.getSource() == btnClose) {
            final Node node = (Node) actionEvent.getSource();
            final Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }


}
