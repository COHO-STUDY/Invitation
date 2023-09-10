package com.coho.invitation.dto;

public class Contact {
    private String contactId;
    private String muid;
    private String eid;
    private String name;
    private String phoneNum;
    private boolean is_sent = false;
    private boolean is_kakao_used;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isIs_sent() {
        return is_sent;
    }

    public void setIs_sent(boolean is_sent) {
        this.is_sent = is_sent;
    }

    public boolean isIs_kakao_used() {
        return is_kakao_used;
    }

    public void setIs_kakao_used(boolean is_kakao_used) {
        this.is_kakao_used = is_kakao_used;
    }
}
