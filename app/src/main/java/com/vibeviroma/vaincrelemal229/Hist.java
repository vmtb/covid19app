package com.vibeviroma.vaincrelemal229;

/**
 * Created by Marcos T VITOULEY on 03/11/2018 for TrueLocation.
 */

public class Hist {
    String id, code, date;

    public Hist(String id, String code, String date) {
        this.id = id;
        this.code = code;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Hist() {
    }
}
