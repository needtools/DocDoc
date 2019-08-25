package com.example.docdoc.models;

import java.io.Serializable;

public class Contact  implements Serializable {

    private String phone;
    private String zcontact;

    public Contact() {
    }

    public Contact(String phone, String zcontact) {
        this.phone = phone;
        this.zcontact = zcontact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZcontact() {
        return zcontact;
    }

    public void setZcontact(String zcontact) {
        this.zcontact = zcontact;
    }
}
