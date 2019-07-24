package espol.edu.ec.espolguide.viewModels;

import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Observable;

import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.adapters.EventAdapter;

/**
 * Created by galo on 22/06/19.
 */

public class EventsViewModel extends Observable {

    private final EventsActivity activity;

    public EventsViewModel(EventsActivity activity){
        this.activity = activity;
    }

}
