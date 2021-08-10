package com.simul.model;

public class UserModel {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private String lastMsgTime;
    private String userId;
    private String display_simul;

    public UserModel(String id, String username, String imageURL, String status, String search, String lastMsgTime) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.lastMsgTime = lastMsgTime;
    }

    public UserModel(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getDisplay_simul() {
        return display_simul;
    }

    public void setDisplay_simul(String display_simul) {
        this.display_simul = display_simul;
    }
}
