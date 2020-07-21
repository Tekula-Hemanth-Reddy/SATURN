package com.example.saturn.aditya;

public class Task
{
    public String uid;
    public String percentage_completion;
    public String taskname;
    public String taskuid;
    public String hour,minute,day,month,year;
    public String starred;

    public Task(String uid, String percentage_completion, String taskname, String taskuid,String std) {
        this.uid = uid;
        this.percentage_completion = percentage_completion;
        this.taskname = taskname;
        this.taskuid=taskuid;
        starred=std;
    }

    public Task()
    {

    }

    public Task(String uid, String percentage_completion, String taskname, String taskuid, String hour, String minute, String day, String month, String year,String std) {
        this.uid = uid;
        this.percentage_completion = percentage_completion;
        this.taskname = taskname;
        this.taskuid = taskuid;
        this.hour = hour;
        this.minute = minute;
        this.day = day;
        this.month = month;
        this.year = year;
        starred=std;
    }

    public String getUid() {
        return uid;
    }

    public String getPercentage_completion() {
        return percentage_completion;
    }

    public String getTaskname() {
        return taskname;
    }

    public String getTaskuid() {
        return taskuid;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPercentage_completion(String percentage_completion) {
        this.percentage_completion = percentage_completion;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public void setTaskuid(String taskuid) {
        this.taskuid = taskuid;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
