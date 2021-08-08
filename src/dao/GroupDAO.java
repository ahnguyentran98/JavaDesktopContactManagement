package dao;

import entity.Group;


import java.io.*;
import java.util.List;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
public class GroupDAO {

    public GroupDAO() {
    }

    //load group from file to list
    public List<Group> loadGroup(String fname) throws Exception {
        //create empty group list
        List<Group> groupList = new Vector<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fname));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                groupList.add(new Group(line));
            }
        }
        bufferedReader.close();
        return groupList;
    }

    //save current group list to file
    public void saveGroupToFile(List<Group> groupList, String fname) throws Exception {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fname));

        for (Group x : groupList) {
            bufferedWriter.write(x.toString());
        }
        bufferedWriter.close();
    }

    //save to current list
    public void saveGroupToList(List<Group> groupList, Group g) {
        groupList.add(g);
    }

    //searching
    public List<Group> search(List<Group> groupList, String search) {
        List<Group> groupListCheck = new Vector<>();
        for (Group x : groupList) {
            String s = x.toString().toLowerCase();
            if (s.contains(search.toLowerCase())) {
                groupListCheck.add(x);
            }
        }
        return groupListCheck;
    }

    //get group position for Update method
    public int indexOf(List<Group> groupList, Group g) {
        for (int i = 0; i < groupList.size(); i++) {
            Group x =  groupList.get(i);
            if (x.getName().equalsIgnoreCase(g.getName())) {
                return i;
            }
        }
        return -1;
    }

    //update
    public boolean updateGroup(List<Group> groups, int i, String oldGroup, String newGroup) {
        //check duplicate group name
        int c = 0;
        for (Group x : groups) {
            if (x.getName().equalsIgnoreCase(newGroup)) {
                c++;
            }
        }

        int a = indexOf(groups, new Group(oldGroup));
        int b = indexOf(groups, new Group(newGroup));

        //if new group name different with old group name
        if (a != b) {
            if (c >= 1) {
                groups.get(i).setName(oldGroup);
                return false;
            }

            groups.get(i).setName(newGroup);
            return true;
        } else {
            //if new group name equalIgnoreCase with old group name
            if (c >= 2) {
                groups.get(i).setName(oldGroup);
                return false;
            }

            groups.get(i).setName(newGroup);
            return true;
        }

    }
}
