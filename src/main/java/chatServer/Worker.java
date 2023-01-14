package chatServer;

import chatProtocol.User;
import chatProtocol.Protocol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import chatProtocol.IService;
import chatProtocol.Message;

import javax.swing.*;

public class Worker {
    Server srv;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;

    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv=srv;
        this.in=in;
        this.out=out;
        this.user=user;
        this.service=service;
    }

    public User getUser(){ return user; }

    boolean continuar;    
    public void start(){
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable(){
                public void run(){
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {  
        }
    }
    
    public void stop(){
        continuar=false;
        System.out.println("Conexion cerrada...");
    }
    
    public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("Operacion: "+method);

                switch(method){
                //case Protocol.LOGIN: done on accept
                case Protocol.LOGOUT:
                    try {
                        srv.remove(user, Protocol.OFFLINE);
                        //service.logout(user); //nothing to do
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    stop();
                    break;                 
                case Protocol.POST:
                    boolean flag;
                    Message message=null;
                    try {
                        message = (Message)in.readObject();
                        message.setSender(user);
                        flag = srv.deliver(message);
                        if(!flag)
                            service.post(message); // if wants to save messages, ex. recivier no logged on
                        System.out.println(user.getNombre()+": "+message.getMessage());
                    } catch (ClassNotFoundException ex) {}
                    break;

                case Protocol.CONTACT:
                    try {
                        User contact = (User) in.readObject();
                        service.checkContact(contact);
                        out.writeInt(Protocol.CONTACT_RESPONSE_OK);
                        out.writeObject(contact);
                    }catch(IOException ex){
                        System.out.println("ERROR AL ENVIAR");
                    }
                    catch (Exception e){
                        try {
                            out.writeInt(Protocol.ERROR_CONTACT);
                        }catch (IOException ex){
                            System.out.println("ERROR AL ENVIAR");
                        }
                    }
                    break;
                    case Protocol.LOAD_MESSAGES:
                        List<Message> messages;
                        try{
                            User sender = (User) in.readObject();
                            User receiver = (User) in.readObject();
                            if(!sender.getId().equals(user.getId())) {
                                messages = service.load(sender, receiver);
                                out.writeInt(Protocol.ERROR_NO_ERROR);
                                for (Message m : messages) {
                                    out.writeBoolean(true);
                                    out.writeObject(m);
                                }
                                out.writeBoolean(false);
                            }
                            else
                                out.writeInt(Protocol.ERROR_POST);
                        }catch (Exception ex){
                            out.writeInt(Protocol.ERROR_POST);
                        }
                        break;
                    case Protocol.CHECK:
                        try {
                            String id = (String) in.readObject();
                            boolean isConnected;
                            isConnected = srv.checkIfConnected(id);
                            out.writeInt(Protocol.CHECK);
                            out.writeObject(id);
                            out.writeBoolean(isConnected);
                        }catch (Exception ex){
                            out.writeInt(Protocol.ERROR_POST);
                        }
                }
                out.flush();
            } catch (IOException  ex) {
                System.out.println(ex.getMessage());
                continuar = false;
            }                        
        }
    }
    
    public void deliver(Message message){
        try {
            out.writeInt(Protocol.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }

    public void notifyStatus(int status, String id){
        try {
            out.writeInt(Protocol.STATUS);
            out.writeInt(status);
            out.writeObject(id);
            out.flush();
        }catch (Exception ex){}
    }
}
