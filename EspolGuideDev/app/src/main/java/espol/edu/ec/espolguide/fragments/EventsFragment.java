package espol.edu.ec.espolguide.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.adapters.EventAdapter;
import espol.edu.ec.espolguide.utils.SessionHelper;

public class EventsFragment extends Fragment {
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadEvents();
    }

    private class EventsLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String date1 = "Lunes 25 de junio";
            String date2 = "Martes 26 de junio";
            String date3 = "Miércoles 27 de junio";
            String event1 = "001;Charla Inteligencia Artificial en la Industria;11A-A103 - Auditorio de FIEC;11h00;25/06/2019";
            String event2 = "002;Conversatorio 'Mujeres en la Inteligencia Artificial';11A-A103 - Auditorio de FIEC;11h00;25/06/2019";
            String event3 = "003;Inauguración de DataJam 2019;7B-S105 - Sala de Eventos de FIMCP;11h00;26/06/2019";
            String event4 = "004;Taller de Analítica de Datos en el Sector Público;6C-L201 - Laboratorio de Computación de FCSH;13h30;27/06/2019";
            eventsList = new ArrayList<>();
            eventsList.add(date1);
            eventsList.add(event1);
            eventsList.add(event2);
            eventsList.add(date2);
            eventsList.add(event3);
            eventsList.add(date3);
            eventsList.add(event4);
            eventAdapter = new EventAdapter(getActivity(), eventsList);
            ListView eventsLv = (ListView) getView().findViewById(R.id.events_lv);
            eventsLv.setAdapter(eventAdapter);
            return null;
        }
    }

    public void loadEvents(){
        new EventsLoader().execute();
    }
}