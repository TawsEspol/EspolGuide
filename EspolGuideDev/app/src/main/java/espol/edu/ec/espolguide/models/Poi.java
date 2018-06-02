package espol.edu.ec.espolguide.models;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by galo on 07/01/18.
 */

public abstract class Poi implements View.OnClickListener {
    private String id;
    private String code;
    private String name;
    private String academicUnit;
    private int favoritesCount;
    private String description;
    private String type;
    private ArrayList<String> alternativeNames;

    public Poi(String id){
        this. id = id;
    }

    public Poi(String id,String code, String name, String academicUnit, int favoritesCount,
               String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.academicUnit = academicUnit;
        this.favoritesCount = favoritesCount;
        this.description = description;
    }

    public Poi(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getacAdemicUnit() {
        return academicUnit;
    }

    public void setAcademicUnit(String academicUnit) {
        this.academicUnit = academicUnit;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) { this.favoritesCount = favoritesCount; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(ArrayList<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onClick(View view) {
        view.setVisibility(View.VISIBLE);
    }
}