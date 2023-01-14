package chatServer.data;

import chatProtocol.Message;
import chatProtocol.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    Database db;

    public MessageDao(){db = Database.instance();}

    public void create(Message m) throws Exception{
        String sql = "insert into "+
                "Message " +
                "(sender, message, receiver) "+
                "values(?,?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1,m.getSender().getId());
        stm.setString(2,m.getMessage());
        stm.setString(3,m.getReceiver().getId());

        db.executeUpdate(stm);
    }

    public void delete(User sender, User receiver) throws Exception{
        String sql = "delete from Message where sender=? and receiver=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1,sender.getId());
        stm.setString(2,receiver.getId());
        int count = db.executeUpdate(stm);
        if(count == 0){
            throw new Exception("MENSAJE NO EXISTE");
        }
    }

    public List<Message> getMessages(User sender, User receiver) throws Exception{
        UserDao ud = new UserDao();
        List<Message> resultado = new ArrayList<>();
        String sql = "select * from message m inner join User u on m.sender=u.id "+
                "inner join User us on m.receiver=us.id where m.sender=? and m.receiver=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1,sender.getId());
        stm.setString(2,receiver.getId());
        ResultSet rs = db.executeQuery(stm);
        Message message;
        while(rs.next()){
            message = from(rs,"m");
            message.setSender(ud.from(rs,"u"));
            message.setReceiver(ud.from(rs,"us"));
            resultado.add(message);
        }
        return resultado;
    }

    public Message from(ResultSet rs, String alias)throws Exception{
        Message m = new Message();
        m.setMessage(rs.getString(alias+".message"));
        return m;
    }
}
