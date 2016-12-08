package com.VakhovYS;

import java.util.Set;
import java.util.TreeSet;

public class Users {

    private Set<String> onlineUsers = new TreeSet<>();

    public void addUser(String login) {


        this.onlineUsers.add(login);

    }

    public void deleteUser(String login) {
        this.onlineUsers.remove(login);
    }

    public String getUsers() {
        return "Online users: " + this.onlineUsers.toString();
    }

}