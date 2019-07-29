package espol.edu.ec.espolguide.viewModels;

import java.util.Observable;

import espol.edu.ec.espolguide.EventsActivity;


/**
 * Created by galo on 22/06/19.
 */

public class EventsViewModel extends Observable {

    private final EventsActivity activity;

    public EventsViewModel(EventsActivity activity){
        this.activity = activity;
    }

}
