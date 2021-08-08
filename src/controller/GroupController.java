package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;

import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class GroupController {
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnClose;
    @FXML
    private ListView<Group> tblGroup;
    @FXML
    private TextField search;
    @FXML
    private TextField groupName;

    private final String GROUP = "src/data/group.txt";
    GroupDAO groupDAO = new GroupDAO();
    List<Group> groups;
    ContactController contactController;

    public void setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    @FXML
    void initialize() {
        try {
            //load groups
            groups = groupDAO.loadGroup(GROUP);
            //show groups on listview
            showGroup(groups);
            tblGroup.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            tblGroup.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Group>() {
                @Override
                public void changed(ObservableValue<? extends Group> observableValue, Group group, Group t1) {
                    //show selected group name to update text field
                    if (tblGroup.getSelectionModel().getSelectedItem() != null) {
                        groupName.setText(GroupController.this.tblGroup.getSelectionModel().getSelectedItem().getName());
                    }
                }
            });
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("" + e);
            alert.showAndWait();
        }
    }

    //show group
    public void showGroup(List<Group> groupList) {
        tblGroup.getItems().clear();
        if (tblGroup.getItems() != null) {
            for (Group x : groupList) {
                tblGroup.getItems().add(x);
            }
        }
    }

    //search group
    public void searchAction() {
        List<Group> group = groupDAO.search(groups, search.getText());
        showGroup(group);
    }

    //add group
    public void addAction() throws Exception {
        String name = groupName.getText().trim();
        if (name.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Group name cannot be empty");
            alert.showAndWait();
        } else {
            Group group = new Group(name);
            //log
            System.out.println("add " + group);

            int i = groupDAO.indexOf(groups, group);
            if (i != -1) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Group name existed, please chose another name");
                alert.showAndWait();
            } else {
                groupDAO.saveGroupToList(groups, group);
                groupDAO.saveGroupToFile(groups, GROUP);
                showGroup(groups);
                contactController.showGroup(groups);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("New group has been added");
                alert.showAndWait();
            }
        }
    }

    //update group name
    public void updateAction() throws Exception {
        int i = tblGroup.getSelectionModel().getSelectedIndex();
        if (i >= tblGroup.getItems().size() || i < 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please select a group to update");
            alert.showAndWait();
            return;
        }

        String oldGroup = tblGroup.getItems().get(i).getName();
        String newGroup = groupName.getText().trim();
        int index = groupDAO.indexOf(groups, new Group(oldGroup));
        //check duplicate
        if (newGroup.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("New group name can not be empty");
            alert.showAndWait();
            return;
        } else if (!groupDAO.updateGroup(groups, index, oldGroup, newGroup)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please select another group name");
            alert.showAndWait();
        } else {
            contactController.updateContactGroup(oldGroup, newGroup);
            showGroup(groups);
            contactController.showGroup(groups);
            groupDAO.saveGroupToFile(groups, GROUP);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("New group has been updated");
            alert.showAndWait();
        }
    }

    public void deleteAction() throws Exception {
        int i = tblGroup.getSelectionModel().getSelectedIndex();
        List<Contact> contactDelete = ((Group) tblGroup.getItems().get(i)).contacts();

        String groupDelete = tblGroup.getItems().get(i).getName();
        if (i < 0 || i >= tblGroup.getItems().size()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Select a Group to delete");
            alert.showAndWait();
            return;
        }

        int size = ((Group) tblGroup.getItems().get(i)).contacts().size();
        if (size > 0) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Group has contacts, are you sure to delete ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                contactController.deleteContactGroup(groupDelete);
                groups.remove(i);
                showGroup(groups);
                groupDAO.saveGroupToFile(groups, GROUP);
                contactController.showGroup(groups);
            }
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure to delete this group?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                groups.remove(i);
                showGroup(groups);
                groupDAO.saveGroupToFile(groups, GROUP);
                contactController.showGroup(groups);
            }
        }
    }


    //action handle
    public void groupAction(ActionEvent actionEvent) throws Exception {
        if (actionEvent.getSource() == btnSearch) {
            searchAction();
        } else if (actionEvent.getSource() == btnAdd) {
            addAction();
        } else if (actionEvent.getSource() == btnUpdate) {
            updateAction();
        } else if (actionEvent.getSource() == btnDelete) {
            deleteAction();
        } else if (actionEvent.getSource() == btnClose) {
            final Node node = (Node) actionEvent.getSource();
            final Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }


}
