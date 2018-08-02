package espol.edu.ec.espolguide.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by fabricio on 02/08/18.
 */

public class SubjectsSet extends HashSet {
    private HashMap<String,HashMap<String,String>> daysTraductor = Constants.getDaysTraductor();
    public String toString() {
        String result = "";
        String language = Locale.getDefault().getLanguage();
        Iterator it = this.iterator();
        ArrayList<String> days = new ArrayList<>();
        while (it.hasNext()) {
            if (language != "es") {
                days.add(daysTraductor.get(language).get(it.next()));
            } else {
                days.add((String) it.next());
            }
        }
        for (int i = 0; i < days.size(); i++) {
            if (i == 0) {
                result = days.get(i);
            } else {
                result = result + "," + days.get(i);


            }
        }
        return result;
    }
}
