package espol.edu.ec.espolguide.utils.assync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.User;

/**
 * Created by fabricio on 14/07/18.
 */
public class SubjectsSoapHelper extends AsyncTask<User, Void, HashMap>{

    Context ctx;
    LinearLayout layout;


    public void addBox( LinearLayout d, String name){
        Spinner spinner = new Spinner(ctx);
        //Make sure you have valid layout parameters.
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        String[] months = new String[] {name};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter(ctx,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinner.setAdapter(spinnerArrayAdapter);
        d.addView(spinner);
    }

    @Override
    protected HashMap doInBackground(User... data) {
        HashMap<String,HashMap> subjects_map = new HashMap<>();
        ctx = data[0].getContext();
        layout = data[0].getLayout();

        if (!Constants.isNetworkAvailable(ctx)) {
        } else {
            try {
                SoapObject request = new SoapObject(Constants.NAMESPACE, Constants.SUBJ_METHOD_NAME);
                request.addProperty("usuario",data[0].getUsr());
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(Constants.URL);
                transport.call(Constants.SUBJ_SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for ( int subjects = 0; subjects < response.getPropertyCount();subjects ++ ){
                    SoapObject subject = (SoapObject) response.getProperty(subjects);
                    String subject_name = subject.getPropertyAsString("Materia");

                    if (data[0].getType()){
                        HashMap<String,String> lectures_map = new HashMap<>();

                        SoapObject lectures = (SoapObject) subject.getProperty("HorarioClase");
                        for ( int lecture = 0; lecture < lectures.getPropertyCount(); lecture ++ ){
                            SoapObject classroom = (SoapObject) lectures.getProperty(lecture);

                            //Replace for GTSI service of date
                            lectures_map.put("DAY"+String.valueOf(lecture+1),classroom.getPropertyAsString("Bloque"));
                        }
                        subjects_map.put(subject_name,lectures_map);
                    }else{
                        HashMap<String,String> exams_map = new HashMap<>();

                        SoapObject exams = (SoapObject) subject.getProperty("HorarioExamen");
                        for ( int lecture = 0; lecture < exams.getPropertyCount(); lecture ++ ){
                            SoapObject classroom = (SoapObject) exams.getProperty(lecture);
                            exams_map.put(classroom.getPropertyAsString("Examen"),classroom.getPropertyAsString("Bloque"));
                        }
                        subjects_map.put(subject_name,exams_map);
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return subjects_map;
    }

    @Override
    protected void onPostExecute(HashMap data) {
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            addBox(layout,(String)e.getKey());
        }

    }
}

