package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

class DataFetcher extends AbstractTableModel {

    private final String[] columnNames;
    ArrayList<Object[]> userData;

    public DataFetcher() {
        columnNames = new String[]{"Czynność", "Czynność bezpośrednio poprzedzająca", "Czas trwania"};
        userData = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return userData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}

public class MainUI {
    private JPanel rootPanel;
    private JComboBox<String> displayBox;
    private JButton displayButton;
    private JTable showTable;
    private JButton addButton;
    private JButton editButton;
    private JButton removeButton;
    private DataFetcher dataFetcher;
    public MainUI() {
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
        dataFetcher = new DataFetcher();
        showEmpty();
    }
    public JPanel getRootPanel(){
        return rootPanel;
    }
    public void showEmpty() {
        showTable.setModel(dataFetcher);
    }

}
