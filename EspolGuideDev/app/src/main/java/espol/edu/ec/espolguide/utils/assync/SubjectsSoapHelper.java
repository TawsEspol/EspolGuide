package espol.edu.ec.espolguide.utils.assync;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.adapters.SubjectsAdapter;
import espol.edu.ec.espolguide.controllers.listeners.ToPoiListener;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SubjectsSet;
import espol.edu.ec.espolguide.utils.User;
import espol.edu.ec.espolguide.utils.Util;

/**
 * Created by fabricio on 14/07/18.
 */
public class SubjectsSoapHelper extends AsyncTask<User, Void,HashMap>{

    private Context ctx;
    private ListView layout;
    private LayoutInflater inflater;

    //Creates a box for containing info about subject (room, place)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void addSubject(List container, HashMap subjectData, String name){




        LinearLayout subjectBox = (LinearLayout) inflater.inflate(R.layout.subject_box, null);
        ctx = subjectBox.getContext();

        subjectBox.setOrientation(LinearLayout.VERTICAL);
        TextView subjectName = subjectBox.findViewById(R.id.subjectName);
        name = Util.toTitleCase(name);
        subjectName.setText(name);
        addRooms(subjectBox,subjectData);

        container.add(subjectBox);

    }


    //Creates a box for containing info about subject (room, place)

    private void addRooms(LinearLayout subjectBox, HashMap rooms) {
        for (Object o : rooms.entrySet()) {
            LinearLayout roomContainer = subjectBox.findViewById(R.id.subjectRooms);
            LinearLayout roomBox = (LinearLayout) inflater.inflate(R.layout.room_box, null);

            Map.Entry e = (Map.Entry) o;

            String date = e.getValue().toString();
            String place = (String) e.getKey();

            ArrayList placeInfo = new ArrayList<>();
            placeInfo.addAll(Arrays.asList(place.split("\\|")));
            roomBox.setClickable(true);
            roomBox.setOnClickListener(new ToPoiListener(((String)placeInfo.get(1)).trim(), ctx));
            String[] labels = {ctx.getString(R.string.room),ctx.getString(R.string.block),ctx.getString(R.string.day)};
            placeInfo.add(date);

            TextView block = roomBox.findViewById(R.id.block);
            TextView block_value = roomBox.findViewById(R.id.block_value);
            block.setText(labels[0]);
            block_value.setText((String)placeInfo.get(0));


            TextView room = roomBox.findViewById(R.id.room);
            TextView room_value = roomBox.findViewById(R.id.room_value);
            room.setText(labels[1]);
            try {
                room_value.setText(((String) placeInfo.get(1)).split("BLOQUE")[1].trim());
            }catch(Exception a){
                room_value.setText(((String) placeInfo.get(1)).trim());
            }

            TextView day = roomBox.findViewById(R.id.day);
            TextView day_value = roomBox.findViewById(R.id.day_value);
            day.setText(labels[2]);
            day_value.setText((String)placeInfo.get(2));

            roomContainer.addView(roomBox);


        }
    }
    @Override
    protected HashMap doInBackground(User... data) {
        HashMap<String,HashMap> subjectsMap = new HashMap<>();
        ctx = data[0].getContext();
        layout = data[0].getLayout();
        inflater = LayoutInflater.from(ctx);

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
                        HashMap<String,HashSet<String>> lecturesMap = new HashMap<>();
                        //Key:place - block
                        //Value: Day(s)

                        SoapObject lectures = (SoapObject) subject.getProperty("HorarioClase");
                        for ( int lecture = 0; lecture < lectures.getPropertyCount(); lecture ++ ){
                            SoapObject classroom = (SoapObject) lectures.getProperty(lecture);
                            String block = classroom.getPropertyAsString("Bloque");
                            String place = classroom.getPropertyAsString("Lugar");
                            String key = place + " | " + block;
                            String day = classroom.getPropertyAsString("Dia");
                            HashSet<String> days;
                            if (lecturesMap.get(key) != null){
                                days = lecturesMap.get(key);
                                days.add(day);
                                lecturesMap.put(key, days);
                            }else{
                                days = new SubjectsSet();
                                days.add(day);
                                lecturesMap.put(key,days);
                            }
                        }
                        if (subjectsMap.get(subjectName) != null){
                            for (Object o : lecturesMap.entrySet()) {
                                Map.Entry e = (Map.Entry) o;
                                if (subjectsMap.get(subjectName).get(e.getKey()) != null) {
                                    //distribuidos,bloque16c,com3
                                    HashSet sM = (HashSet) subjectsMap.get(subjectName).get(e.getKey());
                                    sM.addAll(lecturesMap.get(e.getKey()));
                                    subjectsMap.get(subjectName).put(e.getKey(), sM);
                                } else {
                                    subjectsMap.get(subjectName).put(e.getKey(),
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
                            String block = classroom.getPropertyAsString("Bloque");
                            String place = classroom.getPropertyAsString("Lugar");
                            String examType = classroom.getPropertyAsString("Examen");
                            String key = place +" | "+block;
                            if (exams_map.keySet().contains(key)){
                                exams_map.put(key,exams_map.get(key)+  "," + examType);
                            }else{
                                exams_map.put(key, examType);
                            }

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
        List<LinearLayout> subjects = new ArrayList<>();


        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            addSubject(subjects, (HashMap) data.get(e.getKey()), (String) e.getKey());
        }

        SubjectsAdapter favoriteAdapter = new SubjectsAdapter(ctx, subjects);
        layout.setAdapter(favoriteAdapter);
    }
}

