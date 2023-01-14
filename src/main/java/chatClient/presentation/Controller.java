package chatClient.presentation;

import chatClient.logic.ServiceProxy;
import chatProtocol.Chat;
import chatProtocol.Message;
import chatProtocol.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Controller {
    View view;
    Model model;
    ServiceProxy localService;
    
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.instance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
    }

    public void notifyStatus(int status, String id){
        view.setStatus(status,id);
    }

    public void showMessageDialog(String message){
        view.showMessageDialog(message);
    }

    public void login(User u) throws Exception{
        User logged=ServiceProxy.instance().login(u);
        model.setCurrentUser(logged);
        model.commit(Model.USER);
    }

    public void checkIfConnected(String id){
        localService.checkIfConnected(id);
    }

    public void setStatus(){
        for(User u: model.getContacts()){
            checkIfConnected(u.getId());
        }
    }

    public void register(User u)throws Exception{
        ServiceProxy.instance().register(u);
    }

    public void processContact(User contact)throws Exception{
        localService.checkContact(contact);
    }

    public void addContact(User contact) throws Exception{
        model.addContact(contact);
        model.commit(Model.CONTACT);
    }

    public void post(String text, int row){
        Message message = new Message();
        message.setMessage(text);
        message.setSender(model.getCurrentUser());
        message.setReceiver(model.getContacts().get(row));
        ServiceProxy.instance().post(message);
        model.commit(Model.CHAT);
    }

    public void buscar(String filtro){
        List<User> rows = model.contactsSearch(filtro);
        model.setContacts(rows);
        model.commit(Model.CONTACT);
    }

    public void setCurrentChat(int row, int lastRow){
        User sender = model.getContacts().get(row);
        try {
            localService.loadChatMessages(sender, model.getCurrentUser());
        }catch (Exception ex){
            System.out.println("error de obtencion de datos");
        }
        model.setCurrentChat(row, lastRow);
        model.commit(Model.CHAT);
    }

    public void addMessages(List<Message> messages){
        model.getMessages().addAll(messages);
        model.commit(Model.CHAT);
    }

    public void logout(){
        try {
            ServiceProxy.instance().logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        model.commit(Model.USER+Model.CHAT);
    }
        
    public void deliver(Message message){
        model.messages.add(message);
        model.commit(Model.CHAT);       
    }    
}
