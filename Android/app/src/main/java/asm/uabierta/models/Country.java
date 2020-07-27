package asm.uabierta.models;

/**
 * Created by Alex on 26/07/2016.
 */
public class Country {

    private Integer id;
    private String iso2;
    private String iso3;
    private String prefix;
    private String name;

    @Override
    public String toString() {
        return "+"+prefix+" "+name;
    }

    public Integer getId() {
        return id;
    }

    public String getIso2() {
        return iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public void setId(int id) {
        this.id = id;
    }
}
