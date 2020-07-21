package com.example.saturn.diaryaditya;

public class Diary {
    public String day;
    public String matter;
    public String title;
    public String month;
    public String year;

    public Diary() {
    }

    public Diary(String day, String matter, String title, String month, String year) {
        this.day = day;
        this.matter = matter;
        this.title = title;
        this.month = month;
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMatter() {
        return matter;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
