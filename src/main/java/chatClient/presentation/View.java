package chatClient.presentation;

import chatClient.Application;
import chatProtocol.Message;
import chatProtocol.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.Vector;

public class View implements Observer {
    private JPanel panel;
    private JPanel loginPanel;
    private JPanel bodyPanel;
    private JTextField id;
    private JPasswordField clave;
    private JButton login;
    private JButton finish;
    private JTextPane messages;
    private JTextField mensaje;
    private JButton post;
    private JButton logout;
    private JButton register;
    private JScrollPane contactsPanel;
    private JTable tbl_users;
    private JLabel lb_clave;
    private JLabel lb_usuario;
    private JButton btn_AgregarCont;
    private JTextField fld_contID;
    private JTextField fld_contNombre;
    private JLabel lb_contId;
    private JLabel lb_contNombre;
    private JButton btn_buscar;
    private JTextField fld_buscar;
    private int lastRowSelected;

    int changeStatus;

    Model model;
    Controller controller;

    public View() {
        lastRowSelected = 0;
        changeStatus = 0;
        loginPanel.setVisible(true);
        Application.window.getRootPane().setDefaultButton(login);
        bodyPanel.setVisible(false);

        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(id.getText().matches("^[a-zA-Z0-9]*$")) {
                        User u = new User(id.getText(), new String(clave.getPassword()), "");
                        id.setBackground(Color.white);
                        clave.setBackground(Color.white);
                        controller.login(u);
                        id.setText("");
                        clave.setText("");
                    }
                    else{
                        JOptionPane.showMessageDialog(panel,"No se permiten caracteres especiales","ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    id.setBackground(Color.orange);
                    clave.setBackground(Color.orange);
                    showMessageDialog(ex.getMessage());
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.store();
                controller.logout();
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row;
                String text = mensaje.getText();
                try {
                    row = tbl_users.getSelectedRow();
                }catch (Exception ex){row = 0;}
                if(row != -1)
                    lastRowSelected = row;
                controller.post(text, lastRowSelected);
                    //tbl_users.setRowSelectionInterval(row, row);
            }
        });
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nombre = new JTextField("");
                Object[] fields = {"Nombre:",nombre};
                int option = JOptionPane.showConfirmDialog(panel,fields,id.getText(), JOptionPane.CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
                if(option == JOptionPane.OK_OPTION){
                    try{
                        controller.register(new User(id.getText(),new String(clave.getPassword()), nombre.getText()));
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(panel, ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        btn_AgregarCont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(validateContact()){
                    try{
                        User contact = takeContact();
                        controller.processContact(contact);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(panel, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        tbl_users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    int row;
                    try {
                        row = tbl_users.getSelectedRow();
                    } catch (Exception ex) {
                        row = 0;
                    }
                    int lastRow = lastRowSelected;
                    if(row != -1)
                        lastRowSelected = row;
                    controller.setCurrentChat(lastRowSelected,lastRow);
                    tbl_users.setRowSelectionInterval(row, row);

            }
        });
        btn_buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.buscar(fld_buscar.getText());
            }
        });
    }

    public void showMessageDialog(String message){
        JOptionPane.showMessageDialog(panel,message,"ERROR",JOptionPane.ERROR_MESSAGE);
    }
    public void setModel(Model model) {
        this.model = model;
        model.addObserver(this);
    }

    public void setStatus(int status,String id){
        int index = model.getUserIndex(id);
        if(index != -1) {
            if (status == Model.OFFLINE) {
                tbl_users.setValueAt("Offline", index,0);
            }
            else{
                tbl_users.setValueAt("Online",index,0);
            }
            tbl_users.repaint();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public JPanel getPanel() {
        return panel;
    }

    String backStyle = "margin:0px; background-color:#e6e6e6;";
    String senderStyle = "background-color:#c2f0c2;margin-left:30px; margin-right:5px;margin-top:3px; padding:2px; border-radius: 25px;";
    String receiverStyle = "background-color:white; margin-left:5px; margin-right:30px; margin-top:3px; padding:2px;";

    public void update(java.util.Observable updatedModel, Object properties) {

        int[] cols = {TableModel.IMAGE,TableModel.NAME,TableModel.ID};
        tbl_users.setModel(new TableModel(cols, model.getContacts()));
        tbl_users.setRowHeight(30);


        int prop = (int) properties;
        controller.setStatus();

        this.panel.repaint();
        if (model.getCurrentUser() == null) {
            Application.window.setTitle("CHAT");
            loginPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(login);
            bodyPanel.setVisible(false);
        } else {
            Application.window.setTitle(model.getCurrentUser().getNombre().toUpperCase());
            loginPanel.setVisible(false);
            bodyPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(post);
            if ((prop & Model.CHAT) == Model.CHAT) {
                this.messages.setText("");
                String text = "";
                int row;
                try {
                    //row = tbl_users.getSelectedRow();
                    row = lastRowSelected;
                }catch (Exception e){row = 0;}
                for (Message m : model.getMessages()) {
                    if(row != -1 && (m.getSender().getId().equals(tbl_users.getValueAt(row,TableModel.ID)) ||
                            m.getReceiver().getId().equals(tbl_users.getValueAt(row,TableModel.ID)))) {
                        if (m.getSender().equals(model.getCurrentUser())) {
                            text += ("Me:" + m.getMessage() + "\n");
                        } else {
                            text += (m.getSender().getNombre() + ": " + m.getMessage() + "\n");
                        }
                    }
                }
                this.messages.setText(text);
            }
            this.mensaje.setText("");
        }
        panel.validate();
    }



    private boolean validateContact(){
        boolean valid = true;

        if(fld_contID.getText().isEmpty()){
            valid = false;
            lb_contId.setBorder(Application.BORDER_ERROR);
            lb_contId.setToolTipText("Id requerido");
        }
        else{
            lb_contId.setBorder(null);
            lb_contId.setToolTipText(null);
        }
        if(fld_contNombre.getText().isEmpty()){
            valid = false;
            lb_contNombre.setBorder(Application.BORDER_ERROR);
            lb_contNombre.setToolTipText("Nombre requerido");
        }
        else{
            lb_contNombre.setBorder(null);
            lb_contNombre.setToolTipText(null);
        }

        return valid;
    }

    private User takeContact(){
        User contact = new User();
        contact.setNombre(fld_contNombre.getText());
        contact.setId(fld_contID.getText());
        return contact;
    }
}
