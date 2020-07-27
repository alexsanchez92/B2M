package asm.uabierta.models;

/**
 * Created by Alex on 26/07/2016.
 */
public class Building {

    private Integer id;
    private String name;
    private String placeDetails;
    private String havePlaceDetails;

    @Override
    public String toString() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHavePlaceDetails() {
        return havePlaceDetails;
    }

    public void setHavePlaceDetails(String havePlaceDetails) {
        this.havePlaceDetails = havePlaceDetails;
    }

    public String getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(String placeDetails) {
        this.placeDetails = placeDetails;
    }
}
