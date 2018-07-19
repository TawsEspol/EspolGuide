package espol.edu.ec.espolguide.utils;

/**
 * Created by fabricio on 14/07/18.
 */

public class Subject {
    private String day;
    private String place;

    public Subject(String day, String place){
        this.day = day;
        this.place = place;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}

