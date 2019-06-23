package espol.edu.ec.espolguide.viewModels;

import java.util.Observable;

import espol.edu.ec.espolguide.EventsActivity;

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

    private final EventsActivity activity;

    public EventsViewModel(EventsActivity activity){
        this.activity = activity;
    }

}
