package chatClient.presentation;

import chatClient.logic.ServiceProxy;
import chatProtocol.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Vector;

public class TableModel extends AbstractTableModel implements javax.swing.table.TableModel {
    List<User> rows;
    int[] cols;
    Vector<String> status;

    Vector<Icon> icons;
    public TableModel(int[] cols, List<User> rows){
        initColNames();
        this.cols=cols;
        this.rows = rows;
        status = new Vector<>(rows.size());
        for(int i = 0; i<rows.size(); i++){
            status.add("");
        }
    }

    public int getColumnCount(){return cols.length;}

    public String getColumnName(int col){return colNames[cols[col]];}

    public Class<?> getColumnClass(int col){
        switch(cols[col]){
            case IMAGE: return Icon.class;
            default: return super.getColumnClass(col);
        }
    }

    public int getRowCount(){
        if(rows != null)
            return rows.size();
        return 0;
    }

    private Icon getStatusIcon(String stat){
        String file;
        if(stat.equals("Online")){
            file = "onlineRender.png";
        }
        else file = "offlineRender.png";
        return new ImageIcon(getClass().getResource("/"+file));
    }

    public Object getValueAt(int row, int col){
        User user = rows.get(row);

        switch(cols[col]){
            case NAME: return user.getNombre();
            case ID: return user.getId();
            case IMAGE: return getStatusIcon(status.get(row));
            default: return "";
        }
    }

    public void setValueAt(Object aValue, int row, int col){
        User user = rows.get(row);
        switch (cols[col]){
            case NAME: user.setNombre((String) aValue); break;
            case IMAGE: this.status.set(row,(String) aValue);

        }
    }

    public static final int IMAGE = 0;
    public static final int NAME = 1;
    public static final int ID = 2;
    String[] colNames = new String[3];
    private void initColNames(){
        colNames[IMAGE]="Status";
        colNames[NAME]="Name";
        colNames[ID]="User";
    }
}
