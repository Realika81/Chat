package chatProtocol;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements Serializable{
    User sender;
    String message;

    User receiver;

    public Message() {
    }

    public Message(User sender,String message, User receiver) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getReceiver(){return receiver;}
    public void setReceiver(User receiver){this.receiver = receiver;}
    
}
