package com.example.galo.espolguide;

import android.content.Context;
import android.view.View;

import com.example.galo.espolguide.viewModels.PoiInfoViewModel;

import java.util.ArrayList;

/**
 * Auxiliar class that helps to model and visualize the information of each POI.
 * Created by fabricio on 07/01/18.
 */

public class PoiInfo {
    private Context ctx;
    private View view;
    private String code;
    private String name;
    private String academicUnit;
    private int favoritesCount;
    private String description;
    private ArrayList<String> alternativeNames = new ArrayList<String>();
    private PoiInfoViewModel viewModel;

    public PoiInfo(String id, Context ctx, View view){
        this.code = String.valueOf(Integer.parseInt(id.split("Bloque")[1]) % 69);
        this.ctx = ctx;
        this.view = view;
        viewModel = new PoiInfoViewModel(this);
        viewModel.makePoiInfoRequest();
    }

    public Context getCtx(){
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public View getView(){
        return view;
    }

    public void setView(View view) {
        this.view = view;
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

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

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
}