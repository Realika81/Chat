package chatServer;

import chatProtocol.Protocol;
import chatProtocol.User;
import chatProtocol.IService;
import chatProtocol.Message;
import chatServer.data.Database;
import chatServer.data.MessageDao;
import chatServer.data.UserDao;

import java.util.List;

public class Service implements IService{

    private UserDao ud;
    private MessageDao md;
    public Service() {
        ud = new UserDao();
        md = new MessageDao();
    }
    
    public void post(Message m){
        // if wants to save messages, ex. recivier no logged on
        try {
            md.create(m);
        }
        catch (Exception ex){
            System.out.println("error al crear");
        }
    }

    public List<Message> load(User sender, User receiver)throws Exception{
        List<Message> list = md.getMessages(sender, receiver);
        md.delete(sender, receiver);
        return list;
    }
    
    public User login(User p) throws Exception{
        return ud.findById(p.getId()); //falta validar contraseña
    }

    public void logout(User p) throws Exception{
        //nothing to do
    }
    public void register(User u) throws Exception{
        ud.create(u);
    }

    public void checkContact(User contact)throws Exception{
        if(ud.findById(contact.getId()) == null){
            throw new Exception("No se encontro el usuario");
        }
    }

    public void checkIfConnected(String id){}
}
