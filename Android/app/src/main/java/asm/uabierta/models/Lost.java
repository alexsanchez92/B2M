package asm.uabierta.models;

import android.util.Log;

import asm.uabierta.utils.UtilsFunctions;

/**
 * Created by Alex on 26/07/2016.
 */
public class Lost {

    private int id;
    private String title;
    private String description;
    private String image;
    private int hasPhoto;
    private String startDate;
    private String endDate;
    private Building place;
    //private String placeDetails;
    private int type;
    private User user;
    private String status;
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        String r = image.replace("localhost", "10.0.3.2");
        return r;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return UtilsFunctions.formatDate(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return UtilsFunctions.formatDate(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Building getPlace() {
        return place;
    }

    public void setPlace(Building place) {
        this.place = place;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(int hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
