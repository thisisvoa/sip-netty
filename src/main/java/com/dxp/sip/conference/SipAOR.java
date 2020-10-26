package com.dxp.sip.conference;

public class SipAOR {

    private String userName;
    private  String domain ;

    public SipAOR(String userName, String domain) {
        this.userName = userName;
        this.domain = domain;
    }

    public SipAOR(String aor){

        String [] splites = aor.split("@");
        assert (splites.length == 2 && splites[0].startsWith("sip:"));
        this.userName = splites[0].substring(splites[0].indexOf(":")+1);
        this.domain = splites[1];

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


}






















