package dao;

import entity.Contact;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.BufferedReader;
public class ContactDAO {
    public ContactDAO() {
    }

    //load all contact from contact file into a list
    public List<Contact> loadContact(String fname) throws Exception {
        //create new empty list
        List<Contact> contactList = new Vector<>();
        //read file
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fname));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] strings = line.split(":");
                contactList.add(new Contact(strings[0].trim(), strings[1].trim(), strings[2].trim(), strings[3].trim(), strings[4].trim(), strings[5].trim()));
            }
        }
        bufferedReader.close();
        return contactList;
    }

    //save all Contact from the list to txt file
    public void saveToFile(List<Contact> contactList, String fname) throws Exception {
        //open file for writing
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fname));
        for (Contact x : contactList) {
            bufferedWriter.write(x.toString());
        }
        bufferedWriter.close();
    }

    //return first position of contact in list base on first name and last name
    public int indexof(List<Contact> contactList, Contact contact) {
        for (int i = 0; i < contactList.size(); i++) {
            Contact x = (Contact) contactList.get(i);
            if (x.getFirstName().equalsIgnoreCase(contact.getFirstName()) && x.getLastName().equalsIgnoreCase(contact.getLastName())) {
                return i;
            }
        }
        return -1;
    }

    //return contact position base on group
    public int indexOfGroup(List<Contact> contactList, Contact contact) {
        for (int i = 0; i < contactList.size(); i++) {
            Contact x = (Contact) contactList.get(i);
            if (x.getGroup().equalsIgnoreCase(contact.getGroup())) {
                return i;
            }
        }
        return -1;
    }

    public void updateContactGroup(List<Contact> contactList, Contact contact, int i) {
        Contact x = contactList.get(i);
        x.setFirstName(contact.getFirstName());
        x.setLastName(contact.getLastName());
        x.setEmail(contact.getEmail());
        x.setPhone(contact.getPhone());
        x.setDob(contact.getDob());
        x.setGroup(contact.getGroup());
    }

    //save contact from current list
    public void saveToList(List<Contact> contactList, Contact contact) {
        contactList.add(contact);
    }

    //Update contact information at position i of contact in the list
    public void updateContact(List<Contact> contactList, Contact contact, int i) {
        Contact x = contactList.get(i);
        x.setFirstName(contact.getFirstName());
        x.setLastName(contact.getLastName());
        x.setEmail(contact.getEmail());
        x.setPhone(contact.getPhone());
        x.setDob(contact.getDob());
        x.setGroup(contact.getGroup());
    }



    //return matched contact when searching
    public List<Contact> search(List<Contact> contactList, String group, String search) {
        //when select all in group combobox
        if (group.equals("All")) {
            //if cbGroup is All set group check condition is none
            group = "";
        }

        List<Contact> contactListCheck = new Vector<>();

        for (Contact x : contactList) {
            String s = x.toString().toLowerCase();
            if (s.contains(search.toLowerCase()) && x.getGroup().contains(group)) {
                contactListCheck.add(x);
            }
        }
        return contactListCheck;
    }

    //return contacts in the search group
    public List<Contact> contactByGroup(List<Contact> contactList, String group) {
        if (group.equals("All")) {
            return contactList;
        }

        List<Contact> contactListCheck = new Vector<>();
        for (Contact x : contactList) {
            String s = x.getGroup().toLowerCase();
            if (s.contains(group.toLowerCase())) {
                contactListCheck.add(x);
            }
        }
        return contactListCheck;
    }


}
