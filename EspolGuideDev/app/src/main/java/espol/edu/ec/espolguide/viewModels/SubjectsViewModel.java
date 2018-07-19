package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Observable;

import espol.edu.ec.espolguide.SubjectsActivity;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.User;
import espol.edu.ec.espolguide.utils.assync.SubjectsSoapHelper;

/**
 * Created by fabricio on 14/07/18.
 */

public class SubjectsViewModel extends Observable{
    public static String SUBJECTS_REQUEST_STARTED = "subjects_request_started";
    public static String SUBJECTS_REQUEST_SUCCEED = "subjects_request_succeed";
    public static String SUBJECTS_REQUEST_FAILED = "subjects_request_failed";

    private SubjectsActivity activity;

    public SubjectsViewModel(SubjectsActivity activity) { this.activity = activity; }

}
