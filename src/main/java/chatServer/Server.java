
package chatServer;

import chatProtocol.Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import chatProtocol.IService;
import chatProtocol.Message;
import chatProtocol.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

public class Server {
    ServerSocket srv;
    List<Worker> workers; 
    
    public Server() {
        try {
            srv = new ServerSocket(Protocol.PORT);
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Servidor iniciado...");
        } catch (IOException ex) {
        }
    }
    
    public void run(){
        IService service = new Service();

        boolean continuar = true;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;
        int method;
        while (continuar) {
            try {
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");

                method = in.readInt();
                switch(method){
                    case Protocol.LOGIN:
                        try{
                            User user=this.login(in,out,service);
                            Worker worker = new Worker(this,in,out,user, service);
                            workers.add(worker);
                            worker.start();
                        }catch(Exception ex){
                            out.writeInt(Protocol.ERROR_REGISTER);
                            out.flush();
                            skt.close();
                            System.out.println("Conexion cerrada...");
                        }
                        break;
                    case Protocol.REGISTER:
                        try{
                            service.register((User) in.readObject());
                            out.writeInt(Protocol.ERROR_NO_ERROR);
                            out.flush();
                        }catch(Exception ex){
                            out.writeInt(Protocol.ERROR_REGISTER);
                            out.flush();
                            skt.close();
                            System.out.println("Conexion cerrada...");
                        }
                        break;
                    default:
                        out.writeInt(Protocol.ERROR_LOGIN);
                        out.flush();
                        skt.close();
                        System.out.println("Conexion cerrada...");
                }

            } catch (IOException ex) {}
            catch (Exception ex) {
                try {
                    out.writeInt(Protocol.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex1) {}
               System.out.println("Conexion cerrada...");
            }
        }
    }
    
    private User login(ObjectInputStream in,ObjectOutputStream out,IService service) throws IOException, ClassNotFoundException, Exception{
//        int method = in.readInt();
//        if (method!=Protocol.LOGIN) throw new Exception("Should login first");
        User user=(User)in.readObject();
        User user1 = service.login(user);
        if(!user.getClave().equals(user1.getClave())) {
            out.writeInt(Protocol.ERROR_PASS);
            out.flush();
        }
        else {
            user = user1;
            out.writeInt(Protocol.ERROR_NO_ERROR);
            out.writeObject(user);
            out.flush();
            this.notifyStatus(Protocol.ONLINE, user.getId());
        }

        return user;
    }
    
    public boolean deliver(Message message){
        boolean flag = false;
        for(Worker wk:workers){
            if(message.getReceiver() != null) {
                if (wk.getUser().getId().equals(message.getReceiver().getId()) || wk.getUser().getId().equals(message.getSender().getId())) {
                    wk.deliver(message);
                    if(wk.getUser().getId().equals(message.getReceiver().getId()))
                        flag = true;
                }
            }
        }
        return flag;
    }

    public void notifyStatus(int status, String id){
        for(Worker wk: workers){
            wk.notifyStatus(status, id);
        }
    }
    
    public void remove(User u, int status){
        int index = -1;
        for(int i = 0; i<workers.size();i++) {
            if (workers.get(i).getUser().getId().equals(u.getId())) {
                index = i;
            }
        }
        if(index != -1) {
            workers.remove(index);
            for (Worker wk : workers)
                wk.notifyStatus(status, u.getId());
        }

        System.out.println("Quedan: " + workers.size());
    }

    public boolean checkIfConnected(String id){
        for(Worker wk: workers){
            if(wk.getUser().getId().equals(id)){
                return true;
            }
        }
        return false;
    }
}