package espol.edu.ec.espolguide.viewModels;

import java.util.Observable;

import espol.edu.ec.espolguide.FavoritesActivity;

public class FavoritesViewModel extends Observable {
    public static String NAMES_REQUEST_STARTED = "names_request_started";

    private FavoritesActivity activity;

    public FavoritesViewModel(FavoritesActivity activity){
        this.activity = activity;
    }

}
