package espol.edu.ec.espolguide.utils.assync;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espol.edu.ec.espolguide.controllers.listeners.ToPoiListener;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SubjectRoom;
import espol.edu.ec.espolguide.utils.User;

/**
 * Created by fabricio on 14/07/18.
 */
public class SubjectsSoapHelper extends AsyncTask<User, Void, HashMap>{

    Context ctx;
    LinearLayout layout;

    //Creates a box for containing info about subject (room, place)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void addSubject(LinearLayout container, HashMap subjectData, String name){

        LinearLayout subjectBox = new LinearLayout(ctx);

        subjectBox.setOrientation(LinearLayout.VERTICAL);
        TextView subjectName = new TextView(ctx);
        subjectName.setText(name);
        subjectName.setTextColor(Color.parseColor(Constants.COLOR_FIRST));
        subjectBox.addView(subjectName);
        addRooms(subjectBox,subjectData);

        container.addView(subjectBox);

    }

    //Creates a box for containing info about subject (room, place)

    private void addRooms(LinearLayout subjectBox, HashMap rooms) {
        Iterator it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            SubjectRoom subjectRoom = new SubjectRoom(ctx, (String) e.getKey(), (String) e.getValue());
            subjectRoom.setBackgroundColor(Color.parseColor(Constants.COLOR_FOURTH));
            subjectRoom.setOnClickListener(new ToPoiListener((String) e.getValue(),ctx));
            subjectRoom.setTextColor(Color.parseColor(Constants.COLOR_SECOND));
            subjectRoom.setText(subjectRoom.toString());
            subjectBox.addView(subjectRoom);
        }
    }
    @Override
    protected HashMap doInBackground(User... data) {
        HashMap<String,HashMap> subjectsMap = new HashMap<>();
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
                    String subjectName = subject.getPropertyAsString("Materia");

                    if (data[0].getType()){
                        HashMap<String,String> lecturesMap = new HashMap<>();
                        //Key:place - block
                        //Value: Day(s)

                        SoapObject lectures = (SoapObject) subject.getProperty("HorarioClase");
                        for ( int lecture = 0; lecture < lectures.getPropertyCount(); lecture ++ ){
                            SoapObject classroom = (SoapObject) lectures.getProperty(lecture);
                            String block = classroom.getPropertyAsString("Bloque");
                            String place = classroom.getPropertyAsString("Lugar");
                            String key = place + " | " + block;
                            String day = "DAY" + " " +String.valueOf(lecture+1);
                            if (lecturesMap.get(key) != null){
                                lecturesMap.put(key,lecturesMap.get(key) + "," + day);
                            }else{
                                lecturesMap.put(key,day);
                            }
                        }
                        if (subjectsMap.get(subjectName) != null){
                            Iterator it = lecturesMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry e = (Map.Entry)it.next();
                                if (subjectsMap.get(subjectName).get( e.getKey()) != null ){
                                    //distribuidos,bloque16c,com3
                                    subjectsMap.get(subjectName).put( e.getKey(),
                                            subjectsMap.get(subjectName).get( e.getKey())
                                                    +","+lecturesMap.get(e.getKey()));
                                }else{
                                    subjectsMap.get(subjectName).put( e.getKey(),
                                            lecturesMap.get(e.getKey()));
                                }
                            }
                        }else{
                            subjectsMap.put(subjectName,lecturesMap);
                        }

                    }else{
                        HashMap<String,String> exams_map = new HashMap<>();

                        SoapObject exams = (SoapObject) subject.getProperty("HorarioExamen");
                        for ( int lecture = 0; lecture < exams.getPropertyCount(); lecture ++ ){
                            SoapObject classroom = (SoapObject) exams.getProperty(lecture);
                            exams_map.put(classroom.getPropertyAsString("Examen"),classroom.getPropertyAsString("Bloque"));
                        }
                        if (!exams_map.isEmpty()) {
                            subjectsMap.put(subjectName, exams_map);
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return subjectsMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onPostExecute(HashMap data) {
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            addSubject(layout,(HashMap) data.get(e.getKey()),(String)e.getKey());
        }


    }
}

