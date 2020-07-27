package asm.uabierta.models;

import asm.uabierta.utils.UtilsFunctions;

/**
 * Created by Alex on 26/07/2016.
 */
public class Found {

    private int id;
    private String title;
    private String description;
    private String image;
    private int hasPhoto;
    private String foundDate;
    private Building place;
    //private String placeDetails;
    private String property;
    private int haveIt;
    private Building holder;
    //private String havePlaceDetails;
    private int type;
    private User user;
    private String status;
    private String createdAt;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getFoundDate() {
        return UtilsFunctions.formatDate(foundDate);
    }

    public void setFoundDate(String foundDate) {
        this.foundDate = foundDate;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Building getHolder() {
        return holder;
    }

    public void setHolder(Building holder) {
        this.holder = holder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Building getPlace() {
        return place;
    }

    public void setPlace(Building place) {
        this.place = place;
    }

    public int getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(int hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public int getHaveIt() {
        return haveIt;
    }

    public void setHaveIt(int haveIt) {
        this.haveIt = haveIt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
