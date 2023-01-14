package chatClient.data;

import chatProtocol.Chat;
import chatProtocol.Message;
import chatProtocol.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Data {
    private List<User> users;

    private List<Chat> chats;

    public Data() {
        users = new ArrayList<>();
        chats = new ArrayList<>();
//        users.add(new User("Manudo15","","ramiro??"));
//        users.add(new User("Carlitos30","","Carlos"));
//        users.add(new User("Juan","","Pedro"));
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Chat> getChats(){
        return chats;
    }

    public void addChat(List<Message> chat, User receiver){
        chats.add(new Chat(chat, receiver));
    }

    public void setChats(List<Chat> chats){this.chats = chats;}

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getChat(String id){
        for(Chat l: chats){
            if(l.getChat().get(0).getReceiver().getId().equals(id)){
                return l.getChat();
            }
        }
        return null;
    }
}


