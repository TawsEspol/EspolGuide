package espol.edu.ec.espolguide.viewModels;

import java.util.Observable;

import espol.edu.ec.espolguide.SubjectsActivity;

/**
 * Created by fabricio on 14/07/18.
 */

public class SubjectsViewModel extends Observable{
    public static String SUBJECTS_REQUEST_STARTED = "subjects_request_started";
    public static String SUBJECTS_REQUEST_SUCCEED = "subjects_request_succeed";
    public static String SUBJECTS_REQUEST_FAILED = "subjects_request_failed";

    private final SubjectsActivity activity;

    public SubjectsViewModel(SubjectsActivity activity) { this.activity = activity; }

}
