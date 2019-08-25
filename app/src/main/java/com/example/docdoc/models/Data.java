package com.example.docdoc.models;

import java.io.Serializable;

public class Data  implements Serializable {
    private String time;
    private String phone;
    private String temperature;
    private String systolic;
    private String diastolic;
    private String pulse;
    private String sugar;

    public Data() {
    }

    public Data(String time, String phone, String temperature, String systolic, String diastolic, String pulse, String sugar) {
        this.time = time;
        this.phone = phone;
        this.temperature = temperature;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
        this.sugar = sugar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
}
