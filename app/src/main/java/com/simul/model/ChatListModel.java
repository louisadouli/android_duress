package com.simul.model;

public class ChatListModel {

    public String id , idSender;

    public ChatListModel(String id) {
        this.id = id;
    }

    public ChatListModel(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }
}


