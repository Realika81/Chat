package chatProtocol;

public class Protocol {

    public static final String SERVER = "192.168.18.8";
    //"PC20LAB1005";//IP adress or computer name
    public static final int PORT = 1433;

    public static final int LOGIN=1;
    public static final int LOGOUT=2;    
    public static final int POST=3;

    public static final int DELIVER=10;
    
    public static final int ERROR_NO_ERROR=7;
    public static final int ERROR_LOGIN=1;
    public static final int ERROR_LOGOUT=2;    
    public static final int ERROR_POST=3;
    public static final int REGISTER=4;
    public static final int ERROR_REGISTER = 5;
    public static final int CONTACT = 6;
    public static final int CONTACT_RESPONSE_OK = 0;
    public static final int ERROR_CONTACT = 8;
    public static final int LOAD_MESSAGES = 9;

    public static final int STATUS = 11;
    public static final int ONLINE = 12;
    public static final int OFFLINE = 13;
    public static final int CHECK = 14;

    public static final int ERROR_PASS = 15;
}