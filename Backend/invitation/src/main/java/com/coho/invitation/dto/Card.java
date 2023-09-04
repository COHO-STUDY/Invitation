package com.coho.invitation.dto;

public class Card {
    private String cid;
    private char ctype;
    private String eid;
    private String greeting;
    private String letter;
    private String bankAccount;
    private String chost;
    private String adHost;
    private byte[] imgData;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public char getCtype() {
        return ctype;
    }

    public void setCtype(char ctype) {
        this.ctype = ctype;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getChost() {
        return chost;
    }

    public void setChost(String chost) {
        this.chost = chost;
    }

    public String getAdHost() {
        return adHost;
    }

    public void setAdHost(String adHost) {
        this.adHost = adHost;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }
}
