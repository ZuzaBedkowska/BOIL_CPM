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
        return userData.get(rowIndex)[columnIndex];
    }
    public String getColumnName(int col) {
        return columnNames[col];
    }
    public void addData(Object[] dane) {
        userData.add(dane);
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
        createEditButton();
        createRemoveButton();
        createDisplayButton();
        createDisplayBox();
        dataFetcher = new DataFetcher();
        showData();
    }
    public JPanel getRootPanel(){
        return rootPanel;
    }
    public void showData() {
        DataFetcher df = new DataFetcher();
        showTable.setModel(df);
        showTable.setModel(dataFetcher);
    }
    public void addRecord(){
        try{
            JPanel panel = new JPanel(new GridLayout(0, 1));
            Object[] dane = new Object[]{"","",""};
            recordWindow(panel, dane, "Dodawanie rekordu");
            dataFetcher.addData(dane);
            showData();
        } catch (Exception e) {
            errorWindow(e);
        }
    }
    public void editRecord(){
        try{
            JPanel panel = new JPanel(new GridLayout(0, 1));
            recordWindow(panel, new Object[]{"","",""}, "Edytowanie rekordu");
        } catch (Exception e) {
            errorWindow(e);
        }
    }
    public void displayResult(String resultType) {
        try {
            JOptionPane.showConfirmDialog(null, new JPanel(), resultType, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            errorWindow(e);
        }
    }
    public void removeRecord() {
        try {

        } catch (Exception e) {
            errorWindow(e);
        }
    }
    public void createAddButton() {
        addButton.addActionListener(e -> addRecord());
    }
    public void createEditButton() {
        editButton.addActionListener(e -> editRecord());
    }
    public void createRemoveButton(){
        removeButton.addActionListener(e-> removeRecord());
    }
    public void createDisplayButton(){
        displayButton.addActionListener(e->displayResult((String) displayBox.getSelectedItem()));
    }
    public void createDisplayBox(){
        displayBox.setModel(new DefaultComboBoxModel<>(new String[]{"Tabela", "Diagram Gantta", "Graf CPM"}));

    };
    public void errorWindow(Exception e) {
        String message = "Coś poszło nie tak!\n";
        if (e.getMessage() != null) {
            message += e.getMessage();
            message += "\n";
        }
        message += "Spróbuj ponownie!\n";
        JOptionPane.showMessageDialog(new JFrame(), message);
    }
    public void recordWindow(JPanel panel, Object[] record, String title) {
        try {
            JTextField czynnosc = new JTextField((String) record[0]);
            JTextField poprzednik = new JTextField((String) record[1]);
            JTextField czas = new JTextField((String) record[2]);
            panel.add(new JLabel("Czynność:"));
            panel.add(czynnosc);
            panel.add(new JLabel("Poprzednik:"));
            panel.add(poprzednik);
            panel.add(new JLabel("Czas trwania:"));
            panel.add(czas);
            JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            record[0] = czynnosc.getText();
            record[1] = poprzednik.getText();
            record[2] = czas.getText();

        }catch (Exception e) {
            errorWindow(e);
        }
    }
}
