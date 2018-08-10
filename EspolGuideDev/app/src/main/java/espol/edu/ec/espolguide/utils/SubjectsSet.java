package espol.edu.ec.espolguide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by fabricio on 02/08/18.
 */

public class SubjectsSet extends HashSet {
    private final HashMap<String,HashMap<String,String>> daysTraductor = Constants.getDaysTraductor();
    public String toString() {
        String result = "";
        String language = Locale.getDefault().getLanguage();
        Iterator it = this.iterator();
        ArrayList<String> days = new ArrayList<>();
        while (it.hasNext()) {
            if (!language.equals("es")) {
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
