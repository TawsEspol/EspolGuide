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
    public static final String LOAD_EVENTS_STARTED = "load_events_started";
    public static final String LOAD_EVENTS_SUCCEEDED = "load_events_succeeded";
    public static final String EVENTS_NOT_FOUND = "events_not_found";
    public static final String LOAD_EVENTS_FAILED = "load_events_failed";

    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";
    public static final String GET_EVENTS_REQUEST_STARTED = "get_events_request_started";
    public static final String GET_EVENTS_REQUEST_SUCCEEDED = "get_events_request_succeeded";
    public static final String GET_EVENTS_REQUEST_FAILED_LOADING = "get_events_request_failed_loading";

    private ArrayList<String> eventsList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private final EventsActivity activity;

    public EventsViewModel(EventsActivity activity){
        this.activity = activity;
    }

    public void loadEvents(){
        setChanged();
        notifyObservers(LOAD_EVENTS_STARTED);
        new EventsLoader().execute();
    }

    private class EventsLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String date1 = "Lunes 25 de junio";
            String date2 = "Martes 26 de junio";
            String date3 = "Miércoles 27 de junio";
            String event1 = "Charla 'Inteligencia Artificial en la Industria';11A-A103 - Auditorio de FIEC;11h00";
            String event2 = "Conversatorio 'Mujeres en la Inteligencia Artificial';11A-A103 - Auditorio de FIEC;11h00";
            String event3 = "Inauguración de DataJam 2019';7B-S105 - Sala de Eventos de FIMCP;11h00";
            String event4 = "Taller de Analítica de Datos en el Sector Público jajajaja asfcsaf';6C-L201 - Laboratorio de Computación de FCSH;13h30";
            eventsList = new ArrayList<>();
            eventsList.add(date1);
            eventsList.add(event1);
            eventsList.add(event2);
            eventsList.add(date2);
            eventsList.add(event3);
            eventsList.add(date3);
            eventsList.add(event4);
            eventAdapter = new EventAdapter(activity, eventsList);
            activity.getViewHolder().eventsLv.setAdapter(eventAdapter);
            /**if(!SessionHelper.hasFavorites(activity)){
                setChanged();
                notifyObservers(FAVORITES_NOT_FOUND);
            }
            else{
                Set<String> favoritesSet = SessionHelper.getFavoritePois(activity);
                favoritePlaces.addAll(favoritesSet);
                favoriteAdapter = new FavoriteAdapter(activity, favoritePlaces);
                activity.getViewHolder().favoritesLv.setAdapter(favoriteAdapter);
                setChanged();
                notifyObservers(LOAD_FAVORITES_SUCCEEDED);
            }*/
            return null;
        }
    }




}
