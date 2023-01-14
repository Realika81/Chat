package chatProtocol;

import java.util.List;

public interface IService {
    public User login(User u) throws Exception;
    public void logout(User u) throws Exception; 
    public void post(Message m);
    public void register(User u) throws Exception;

    public void checkContact(User contact) throws Exception;

    public List<Message> load(User sender, User receiver)throws Exception;

    public void checkIfConnected(String id);
}
