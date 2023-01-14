package chatServer.data;

import chatProtocol.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    Database db;

    public UserDao() {
        db = Database.instance();
    }

    public void create(User u) throws Exception {
        String sql = "insert into " +
                "User " +
                "(id, clave, nombre) " +
                "values(?,?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, u.getId());
        stm.setString(2, u.getClave());
        stm.setString(3, u.getNombre());

        db.executeUpdate(stm);
    }

    public User read(String id) throws Exception {
        String sql = "select " +
                "* " +
                "from  user u " +
                "where u.id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);
        ResultSet rs = db.executeQuery(stm);
        if (rs.next()) {
            return from(rs, "u");
        } else {
            throw new Exception("Usuario NO EXISTE");
        }
    }

    public void update(User e) throws Exception {
        String sql = "update " +
                "User " +
                "set id=?, clave=?, nombre=? " +
                "where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        stm.setString(2, e.getClave());
        stm.setString(3, e.getNombre());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("USUARIO NO EXISTE");
        }
    }

    public void delete(User e) throws Exception {
        String sql = "delete " +
                "from User " +
                "where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("USUARIO NO EXISTE");
        }
    }

    public List<User> findByNombre(String nombre) throws Exception {
        List<User> resultado = new ArrayList<User>();
        String sql = "select * " +
                "from " +
                "user e " +
                "where e.nombre like ?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + nombre + "%");
        ResultSet rs = db.executeQuery(stm);
        User usuario;
        while (rs.next()) {
            usuario = from(rs,"e");
            resultado.add(usuario);
        }
        return resultado;
    }

    public User findById(String id) throws Exception {
        String sql = "select * " +
                "from " +
                "user e " +
                "where e.id like ?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + id + "%");
        ResultSet rs = db.executeQuery(stm);
        User e;
        rs.next();
        e = from(rs, "e");
        return e;
    }

    public User from(ResultSet rs, String alias) throws Exception {
        User e = new User();
        e.setId(rs.getString(alias + ".id"));
        e.setClave(rs.getString(alias + ".clave"));
        e.setNombre(rs.getString(alias + ".nombre"));
        return e;
    }
}
