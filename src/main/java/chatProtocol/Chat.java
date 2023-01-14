package chatProtocol;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Chat {
    List<Message> chat;
    User receiver;

    public Chat(){
        chat = new ArrayList<>();
    }
    public Chat(List<Message> list, User receiver){
        this.chat = list;
        this.receiver = receiver;
    }

    public List<Message> getChat() {
        return chat;
    }

    public User getReceiver(){return receiver;}
}
