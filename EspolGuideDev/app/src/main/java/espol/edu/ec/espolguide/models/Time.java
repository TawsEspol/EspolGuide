package espol.edu.ec.espolguide.models;

public class Time {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public Time(){ }

    public Time(int year, int month, int day, int hour, int minute, int second){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Time(String eventDateTime){
        String eventDate = eventDateTime.split(" ")[0];
        String eventTime = eventDateTime.split(" ")[1];
        this.day = Integer.parseInt(eventDate.split("/")[0].trim());
        this.month = Integer.parseInt(eventDate.split("/")[1].trim());
        this.year = Integer.parseInt(eventDate.split("/")[2].trim());
        this.hour = Integer.parseInt(eventTime.split(":")[0].trim());
        this.minute = Integer.parseInt(eventTime.split(":")[1].trim());
        this.second = Integer.parseInt(eventTime.split(":")[2].trim());
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
