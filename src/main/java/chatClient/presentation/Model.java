/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatClient.presentation;

import chatClient.data.Data;
import chatClient.data.XMLPersister;
import chatProtocol.Message;
import chatProtocol.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Model extends java.util.Observable {
    User currentUser;
    List<Message> messages;
    Data data;
    User currentChat;
    List<User> contacts;

    public Model() {
        data = new Data();
        currentUser = null;
        currentChat = null;
        messages= new ArrayList<>();
        contacts = new ArrayList<>();
    }

    public void store(){
        try{
            if(currentUser != null) {
                XMLPersister xmlPersister = new XMLPersister(currentUser.getId() + "_dat.xml");
                if(!data.getChats().isEmpty() && !data.getChats().get(data.getChats().size()-1).getReceiver().getId().equals(currentChat.getId()))
                    data.addChat(messages,currentChat);
                xmlPersister.store(data);
                data.getUsers().clear();
                data.getChats().clear();
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getContacts(){return contacts;}

    public void addContact(User c) throws Exception{
        User result = data.getUsers().stream().filter(u->u.getId().equals(c.getId())).findFirst().orElse(null);
        if(result == null)
            data.getUsers().add(c);
        else throw new Exception("Contacto ya existe");
        contacts.add(c);
    }

    public List<User> contactsSearch(String filtro){
        return data.getUsers().stream().filter(u->u.getId().contains(filtro)).collect(Collectors.toList());
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        try{
            XMLPersister xmlPersister = new XMLPersister(currentUser.getId()+"_dat.xml");
            data = xmlPersister.load();
            contacts = data.getUsers();
        }catch (Exception ex){
            data = new Data();
        }
    }

    public void setContacts(List<User> contacts){this.contacts = contacts;}

    public void setCurrentChat(int row, int lastRow){
        if(!messages.isEmpty()) {
            data.addChat(new ArrayList<>(this.messages), contacts.get(lastRow));
            messages.clear();
        }
        currentChat = contacts.get(row);
        try {
            messages.addAll(data.getChat(currentChat.getId()));
        }catch (Exception e){}
        this.commit(CHAT);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addObserver(java.util.Observer o) {
        super.addObserver(o);
        this.commit(Model.USER+Model.CHAT);
    }
    
    public void commit(int properties){
        this.setChanged();
        this.notifyObservers(properties);        
    }

    public int getUserIndex(String id){
        for(int i = 0; i<contacts.size();i++){
            if(contacts.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }
    
    public static int USER=1;
    public static int CHAT=2;
    public static int CONTACT=3;

    public static int ONLINE = 12;

    public static int OFFLINE = 13;
}
