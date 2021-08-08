package controller;

import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;


import java.util.List;
import java.util.Optional;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ContactController {
    @FXML
    private TextField search;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private TableView tblContact;
    @FXML
    private Button btnSearch, btnAdd, btnDelete, btnUpdate, btnGroup;


    //list of contact
    List<Contact> contacts;
    ContactDAO contactDAO = new ContactDAO();
    //data source for contact and group
    private final String GROUP = "src/data/group.txt";
    private final String CONTACT = "src/data/contact.txt";

    @FXML
    void initialize() {
        try {
            //load contact
            contacts = new ContactDAO().loadContact(CONTACT);
            //create table columns
            TableColumn<String, Contact> fname = new TableColumn("First Name");
            fname.setCellValueFactory(new PropertyValueFactory("firstName"));
            tblContact.getColumns().add(fname);

            TableColumn<String, Contact> lname = new TableColumn("Last Name");
            lname.setCellValueFactory(new PropertyValueFactory("lastName"));
            tblContact.getColumns().add(lname);

            TableColumn<String, Contact> phone = new TableColumn("Phone");
            phone.setCellValueFactory(new PropertyValueFactory("phone"));
            tblContact.getColumns().add(phone);

            TableColumn<String, Contact> email = new TableColumn("Email");
            email.setCellValueFactory(new PropertyValueFactory("email"));
            tblContact.getColumns().add(email);

            TableColumn<String, Contact> dob = new TableColumn("Birth Date");
            dob.setCellValueFactory(new PropertyValueFactory("dob"));
            tblContact.getColumns().add(dob);

            TableColumn<String, Contact> group = new TableColumn("Group Name");
            group.setCellValueFactory(new PropertyValueFactory("group"));
            tblContact.getColumns().add(group);

            //get all group
            showGroup(new GroupDAO().loadGroup(GROUP));
            //show contact to table
            showContact(new ContactDAO().loadContact(CONTACT));
            tblContact.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("" + e);
        }
    }

    //show contact to table
    public void showContact(List<Contact> contactList) {
        //clear old data
        tblContact.getItems().clear();
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        //show contact in contactList to tableview
        if (group.equals("All")) {
            for (Contact x : contactList) {
                tblContact.getItems().add(x);
            }
        } else {
            for (Contact x : contactList) {
                if (x.getGroup().equalsIgnoreCase(group)) {
                    tblContact.getItems().add(x);
                }
            }
        }
    }

    //show group to comboBox list
    public void showGroup(List<Group> groupList) {
        //clear old data
        cbGroup.getItems().clear();
        cbGroup.getItems().add(new Group("All"));
        for (Group x : groupList) {
            cbGroup.getItems().add(x);
            //default: select all
            cbGroup.getSelectionModel().select(0);
        }
    }

    //group management view
    public void groupPanel() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/group.fxml"));
        Parent parent = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.setTitle("Group a Management");
        stage.show();
        GroupController groupController = loader.getController();
        groupController.setContactController(this);
    }

    //update contact
    public void updateContact() throws Exception {
        int i = tblContact.getSelectionModel().getSelectedIndex();
        if (i >= tblContact.getItems().size() || i < 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Select a contact to update");
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/updateContact.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("Update a contact");
            stage.show();
            //pass selected contact to addContact view
            UpdateContactController updateContactController = loader.getController();
            updateContactController.setContacts(contacts);
            updateContactController.setContactController(this);
            updateContactController.setUpdatedContact((Contact) tblContact.getItems().get(i));
        }
    }
    //delete contact
    public void deleteContact() throws Exception {
        int i = tblContact.getSelectionModel().getSelectedIndex();
        if (i >= tblContact.getItems().size() || i < 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Select a contact to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure to deleted this contact ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                contacts.remove(i);
                //update tbl view
                showContact(contacts);
                //update data
                contactDAO.saveToFile(contacts, CONTACT);
            }
        }
    }

    //search, delete, update & add contact
    public void contactActions(ActionEvent actionEvent) throws Exception {
        if (actionEvent.getSource() == btnSearch) {
            String group = cbGroup.getSelectionModel().getSelectedItem().getName();
            //load new contact list (after update group name)
            List<Contact> newContact = new ContactDAO().loadContact(CONTACT);
            List<Contact> contactList = contactDAO.search(newContact, group, search.getText());
            this.showContact(contactList);
            if (contactList.isEmpty()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Empty");
                alert.setContentText("Contact not available");
                alert.showAndWait();
            }
        } else if (actionEvent.getSource() == btnAdd) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/addContact.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("Add a new contact");
            stage.show();
            //pass list of current contact to addContactController
            AddContactController addContactController = loader.getController();
            addContactController.setContacts(contacts);
            addContactController.setAddContactController(this);
        } else if (actionEvent.getSource() == btnDelete) {
            deleteContact();
        } else if (actionEvent.getSource() == btnUpdate) {
            updateContact();
        } else if (actionEvent.getSource() == btnGroup) {
            groupPanel();
        }
    }



    //update contact group for class GroupController
    public void updateContactGroup(String oldGroup, String newGroup) throws Exception {
        for (Contact x : contacts) {
            if (x.getGroup().equals(oldGroup)) {
                x.setGroup(newGroup);
            }
            int i = contactDAO.indexOfGroup(contacts, x);
            contactDAO.updateContact(contacts, x, i);
        }
        contactDAO.saveToFile(contacts, CONTACT);
        //load group updated in mainView
        contactDAO.loadContact(CONTACT);
        //show contact to table
        showContact(new ContactDAO().loadContact(CONTACT));
    }

    //delete contact group for class GroupController
    public void deleteContactGroup(String deletedContactGroup) throws Exception {
        //log
        System.out.println("group delete " + deletedContactGroup);

        contacts.removeIf(x -> x.getGroup().equals(deletedContactGroup));
        contactDAO.saveToFile(contacts, CONTACT);
        contactDAO.loadContact(CONTACT);
        showContact(new ContactDAO().loadContact(CONTACT));
    }





}
