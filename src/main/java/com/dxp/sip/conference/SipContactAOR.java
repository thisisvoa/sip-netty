package com.dxp.sip.conference;

public class SipContactAOR {

    public static final int DEFAULT_SIP_PORT = 5060;

    private String userName = null;
    private String address  = null;
    private int    port     = DEFAULT_SIP_PORT;


    private SipAOR sipAOR   = null;

    public SipContactAOR(String userName, String address, int port, SipAOR sipAOR) {
        this.userName = userName;
        this.address = address;
        this.port = port;
        this.sipAOR = sipAOR;
    }

    public SipContactAOR(String userName, String address, int port) {
        this(userName, address, port, null);
    }

    public SipContactAOR(String sipURI) {
        String[] splits = sipURI.split("@");
        assert (2 == splits.length && splits[0].startsWith("sip:"));
        this.userName = splits[0].substring(splits[0].indexOf(':') + 1);

        int index = splits[1].indexOf(':');
        assert (0 < index);
        this.address = splits[1].substring(0, index);
        this.port = Integer.parseInt(splits[1].substring(index + 1));
        assert (0 <= this.port && this.port < 65536);
    }

    public boolean attachTo( SipAOR sipAOR) {
//        if (!this.userName.equals(sipAOR.getUserName())) {
//            return false;
//        }

        this.sipAOR = sipAOR;
        return true;
    }

    @Override
    public String toString() {
        return "sip:" + userName + "@" + address + ":" + port;
    }
    
}


