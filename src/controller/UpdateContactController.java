package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateContactController {
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
    private Contact updatedContact;


    public void setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @FXML
    public void initialize() throws Exception {
        lblFirstName.setText("");
        lblEmail.setText("");
        lblLastName.setText("");
        lblPhone.setText("");
        lbldob.setText("");
    }

    //set update contact from tableview
    public void setUpdatedContact(Contact updatedContact) throws Exception {
        this.updatedContact = updatedContact;

        //output current info of updated contact
        firstName.setText(updatedContact.getFirstName());
        lastName.setText(updatedContact.getLastName());
        email.setText(updatedContact.getEmail());
        phone.setText(updatedContact.getPhone());
        //take all group to combobox group list
        cbGroup.getItems().clear();
        for (Group x : new GroupDAO().loadGroup("src/data/group.txt")) {
            cbGroup.getItems().add(x);
        }
        cbGroup.getSelectionModel().select(new Group(updatedContact.getGroup()));

        Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(updatedContact.getDob());
        dob.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    //save contact method
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
        if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d+")) {
            phone.setText("Phone number contains digit only");
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

        String birthDate = ((LocalDate)this.dob.getValue()).toString();;
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        Contact contactUpdateInfo = new Contact(fname, lname, phoneNumber, mail, birthDate, group);
        //updated contact position
        int i = contactController.contactDAO.indexof(contacts, updatedContact);
        //new contact position
        int j = contactController.contactDAO.indexof(contacts, contactUpdateInfo);
        //check contact FirstName and LastName (cant update firstname and lastname)
        if (i == j) {
            contactController.contactDAO.updateContact(contacts, contactUpdateInfo, i);
            contactController.showContact(contacts);
            contactController.contactDAO.saveToFile(contacts, "src/data/contact.txt");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Contact has been updated");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("contact name is not allowed to update");
            alert.showAndWait();
        }
    }

    //update event
    public void updateContact(ActionEvent actionEvent) throws Exception {
        if (actionEvent.getSource() == btnAdd) {
            saveContact();
        } else if (actionEvent.getSource() == btnClose) {
            final Node node = (Node) actionEvent.getSource();
            final Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }


}
