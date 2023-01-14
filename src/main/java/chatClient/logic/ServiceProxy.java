package chatClient.logic;

import chatClient.presentation.Controller;
import chatProtocol.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import chatClient.data.XMLPersister;

public class ServiceProxy implements IService{
    private static IService theInstance;
    public static IService instance(){
        if (theInstance==null){ 
            theInstance=new ServiceProxy();
        }
        return theInstance;
    }

    ObjectInputStream in;
    ObjectOutputStream out;
    Controller controller;

    public ServiceProxy() {           
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    Socket skt;
    private void connect() throws Exception{
        skt = new Socket(Protocol.SERVER,Protocol.PORT);
        out = new ObjectOutputStream(skt.getOutputStream());
        out.flush();
        in = new ObjectInputStream(skt.getInputStream());    
    }

    private void disconnect() throws Exception{
        skt.shutdownOutput();
        skt.close();
    }
    
    public User login(User u) throws Exception{
        connect();
        //out.writeInt(Protocol.LOGIN);
        try {
            out.writeInt(Protocol.LOGIN);
            out.writeObject(u);
            out.flush();

            int response = in.readInt();
            if (response==Protocol.ERROR_NO_ERROR){
                User u1=(User) in.readObject();
                this.start();
                return u1;
            }
            else {
                disconnect();
                if(response == Protocol.ERROR_PASS){
                    throw new Exception("Contraseña invalida");
                }
                else
                    throw new Exception("No remote user");
            }            
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    public void checkContact(User contact) throws Exception{
        out.writeInt(Protocol.CONTACT);
        out.writeObject(contact);
        out.flush();
    }

    public void loadChatMessages(User sender, User receiver) throws Exception{
        out.writeInt(Protocol.LOAD_MESSAGES);
        out.writeObject(sender);
        out.writeObject(receiver);
        out.flush();
    }

    public List<Message> load(User sender, User receiver)throws Exception{return null;}

    public void register(User u) throws Exception{
        connect();
        //out.writeInt(Protocol.REGISTER);
        try{
            out.writeInt(Protocol.REGISTER);
            out.writeObject(u);
            out.flush();
            int response = in.readInt();
            if(response == Protocol.ERROR_NO_ERROR){
                this.start();
            }
            else{
                disconnect();
                throw new Exception("No remote user");
            }
        }catch(IOException | ClassNotFoundException ex){
            System.out.println("No remote user");
        }
    }
    
    public void logout(User u) throws Exception{
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(u);
        out.flush();
        this.stop();
        this.disconnect();
    }
    
    public void post(Message message){
        try {
            out.writeInt(Protocol.POST);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            
        }   
    }

    public void checkIfConnected(String id){
        try {
            out.writeInt(Protocol.CHECK);
            out.writeObject(id);
            out.flush();
        }catch (Exception ex){}
    }

    // LISTENING FUNCTIONS
   boolean continuar = true;    
   public void start(){
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            }
        });
        continuar = true;
        t.start();
    }
    public void stop(){
        continuar=false;
    }
    
   public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operacion: "+method);
                switch(method) {
                    case Protocol.DELIVER:
                        try {
                            Message message = (Message) in.readObject();
                            deliver(message);
                        } catch (ClassNotFoundException ex) {}
                        break;
                    case Protocol.CONTACT_RESPONSE_OK:
                        try {
                            User contact = (User) in.readObject();
                            controller.addContact(contact);
                        }catch (IOException ex){
                            System.out.println("ERROR AL AGREGAR CONTACTO");
                        }
                        catch (Exception ex){
                            controller.showMessageDialog(ex.getMessage());
                        }
                        break;
                    case Protocol.ERROR_CONTACT:
                        System.out.println("Error al encontrar usuario");
                        break;
                    case Protocol.ERROR_NO_ERROR:
                        List<Message> messages = new ArrayList<>();
                        boolean keep = true;
                        try {
                            while(keep) {
                                keep = in.readBoolean();
                                if(keep)
                                    messages.add((Message) in.readObject());
                            }
                        }catch (Exception ex){}
                        controller.addMessages(messages);
                        break;
                    case Protocol.STATUS:
                        try {
                            int status = in.readInt();
                            String id = (String) in.readObject();
                            controller.notifyStatus(status,id);
                        }catch (Exception ex){}
                        break;
                    case Protocol.CHECK:
                        try {
                            String id = (String) in.readObject();
                            boolean isConnected = in.readBoolean();
                            if(isConnected)
                                controller.notifyStatus(Protocol.ONLINE,id);
                            else
                                controller.notifyStatus(Protocol.OFFLINE,id);
                            break;
                        }catch (Exception ex){}

                }
                out.flush();
            } catch (IOException  ex) {
                continuar = false;
            }                        
        }
    }
    
   private void deliver( final Message message ){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               controller.deliver(message);
            }
         }
      );
   }
}
