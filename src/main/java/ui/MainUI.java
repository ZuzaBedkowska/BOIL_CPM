package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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
    private final DataFetcher dataFetcher;
    public MainUI() {
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
        createAddButton();
        dataFetcher = new DataFetcher();
        showEmpty();
    }
    public JPanel getRootPanel(){
        return rootPanel;
    }
    public void showEmpty() {
        showTable.setModel(dataFetcher);
    }
    public void addRecord(){
        try{
            JTextField czynnosc = new JTextField("");
            JTextField poprzednik = new JTextField("");
            JTextField czas = new JTextField("");
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Czynność:"));
            panel.add(czynnosc);
            panel.add(new JLabel("Poprzednik:"));
            panel.add(poprzednik);
            panel.add(new JLabel("Czas trwania:"));
            panel.add(czas);
            JOptionPane.showConfirmDialog(null, panel, "Dodawanie rekordu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            String message = "Coś poszło nie tak!\n";
            if (e.getMessage() != null) {
                message += e.getMessage();
                message += "\n";
            }
            message += "Spróbuj ponownie!\n";
            JOptionPane.showMessageDialog(new JFrame(), message);
        }
    }
    public void createAddButton() {
        addButton.addActionListener(e -> addRecord());
    }
}
