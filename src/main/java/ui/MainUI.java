package ui;

import logic.Activity;
import logic.ActivityInput;
import logic.MainLogic;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

class DataFetcher extends AbstractTableModel {
    private final String[] columnNames;
    ArrayList<Object[]> userData;

    public DataFetcher() {
        columnNames = new String[]{ "Czynność", "Czynność bezpośrednio poprzedzająca", "Czas trwania" };
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
    public void editData(int row, Object[] dane) {
        userData.get(row)[0] = dane[0];
        userData.get(row)[1] = dane[1];
        userData.get(row)[2] = dane[2];
    }
    public void removeData(int row) {
        userData.remove(userData.get(row));
    }
    public boolean checkIfExists(Object[] data, int row) {
        for (int i = 0; i < userData.size(); ++i)
            if (userData.get(i)[0].equals(data[0]) && row != i) {
                return true;
            }
        return false;
    }
    public boolean checkIfPrevExists(Object[] data) {
        String dane = (String)data[1];
        List<String> poprzednicy = asList(dane.split("\\s*,\\s*"));
        for (String value : poprzednicy) {
            value = value.replaceAll("\\s", "");
        }
        int counter = 0;
        for (String s : poprzednicy) {
            for (Object[] userDatum : userData) {
                if (userDatum[0].equals(s)) {
                    counter++;
                }
            }
        }
        return counter == poprzednicy.size();
    }
    public boolean checkIfPrevUnique(Object[] data) {
        String dane = (String)data[1];
        List<String> poprzednicy = asList(dane.split("\\s*,\\s*"));
        Set<String> zbiorPom = new HashSet<>(poprzednicy);
        return zbiorPom.size() == poprzednicy.size();
    }
    public boolean checkIfPrevAndNextDifferent(Object[] data) {
        String dane = (String)data[1];
        String[] poprzednicy = dane.split("\\s*,\\s*");
        for (String s : poprzednicy) {
            if (data[0].equals(s)) {
                return false;
            }
        }
        return true;
    }
    public void changer(String prev, String next) {//jesli uzytkownik zmieni nazwę czynnosci, powinna byc ona poprawiona w innych rekordach
        for (Object[] userDatum : userData) {
            userDatum[1] = ((String)userDatum[1]).replace(prev, next);
        }
    }
    public void remover(String czyn) {
        for (Object[] userDatum : userData) {
            String data = (String)userDatum[1];
            data = data.replace("," + czyn + ",", ",");
            data = data.replace(czyn + ",", "");
            data = data.replace("," + czyn, "");
            data = data.replace(czyn, "");
            if (data.length() == 0) {
                data = "-";
            }
            userDatum[1] = data;
        }
    }
}

class ResultFetcher extends AbstractTableModel {
    ArrayList<Object[]> userData; //TU SA WYNIKI DO TABELKI
    private final String[] columnNames;
    MainLogic mainLogic; //TU SA DANE

    public ResultFetcher() {
        columnNames = new String[]{ "Czynność", "Czas", "ES", "EF", "LS", "LF", "Rezerwa", "Czynność krytyczna" };
        userData = new ArrayList<>();
        mainLogic = new MainLogic();
    }

    public void setUserData(DataFetcher dataFetcher) {
        ArrayList<ActivityInput> testingData = new ArrayList<>();
        for (int i = 0; i < dataFetcher.getRowCount(); ++i) {
            String poprzednicy = (String)dataFetcher.getValueAt(i, 1);
            String nazwa = (String)dataFetcher.getValueAt(i, 0);
            Double czas = Double.parseDouble((String)dataFetcher.getValueAt(i, 2));
            testingData.add(new ActivityInput(nazwa, poprzednicy, czas));
        }
        mainLogic.addActivityInput(testingData);
        mainLogic.calc();
        ArrayList<Activity> wynik = mainLogic.getAllActivities();
        for (Activity activity : wynik) {
            Object[] dane = new Object[]{ activity.name, activity.time, activity.ES, activity.EF, activity.LS, activity.LF, activity.reserve, activity.isCritical };
            userData.add(dane);
        }
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
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
    @Override
    public Class< ? > getColumnClass(int columnIndex)
    {
        return userData.get(0)[columnIndex].getClass();
    }
    public String getColumnName(int col) {
        return columnNames[col];
    }
}

class GanttChartMaker extends JFrame{
    ChartPanel panel;
    public GanttChartMaker (String title, ResultFetcher resultFetcher) {
        super(title);
        GanttCategoryDataset dataset = getCategoryDataset(resultFetcher);
        JFreeChart chart = ChartFactory.createGanttChart(
                title, // Chart title
                "Czynnosci", // Y-Axis Label
                "Czas", // X-Axis Label
                dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        DateAxis axis = (DateAxis) plot.getRangeAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("S"));
        panel = new ChartPanel(chart);
        setContentPane(panel);
    }
    private GanttCategoryDataset getCategoryDataset(ResultFetcher results) {
        TaskSeries najwczesniejsze=new TaskSeries("Czas najwcześniejszy");
        TaskSeries najpozniejsze=new TaskSeries("Czas najpóźniejszy");
        for (int i = 0; i < results.getRowCount(); ++i) {
            najwczesniejsze.add(new Task((String) results.getValueAt(i, 0), new SimpleTimePeriod(0, 1)));
            //tu trzeba zrobić żeby dobrze liczyło daty, bo nie chce dobrze - zamiast 0 i 1 trzeba wstawić ES i EF
        }
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(najwczesniejsze);dataset.add(najpozniejsze);
        return dataset;
    }

    public ChartPanel getPanel() {
        return panel;
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
    private ResultFetcher resultFetcher;

    public MainUI() {
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
        createAddButton();
        createEditButton();
        createRemoveButton();
        createDisplayButton();
        createDisplayBox();
        createSelectableRecord();
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
        showTable.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        showTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        showTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        showTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    }
    public void addRecord() {
        try {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            Object[] dane = new Object[]{ "","","" };
            int n = recordWindow(panel, dane, "Dodawanie rekordu");
            if (n == 0) {
                textDataParser(dane);
                dataChecker(dane, -1);
                dataFetcher.addData(dane);
            }
            showData();
        }
        catch (Exception e) {
            errorWindow(e);
        }
    }
    public void editRecord() {
        try {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            String czyn = (String)showTable.getValueAt(showTable.getSelectedRow(), 0);
            String pop = (String)showTable.getValueAt(showTable.getSelectedRow(), 1);
            String czas = ((String)showTable.getValueAt(showTable.getSelectedRow(), 2));
            Object[] dane = new Object[]{ czyn,pop,czas };
            int n = recordWindow(panel, dane, "Edytowanie rekordu");
            if (n == 0) {
                //textDataParser(dane);
                dataChecker(dane, showTable.getSelectedRow());
                dataFetcher.editData(showTable.getSelectedRow(), dane);
                dataFetcher.changer(czyn, (String)showTable.getValueAt(showTable.getSelectedRow(), 0));
            }
            showData();
        }
        catch (Exception e) {
            errorWindow(e);
        }
    }
    public void removeRecord() {
        try {
            dataFetcher.remover((String)showTable.getValueAt(showTable.getSelectedRow(), 0));
            dataFetcher.removeData(showTable.getSelectedRow());
            showData();
        }
        catch (Exception e) {
            errorWindow(e);
        }
    }
    public void displayResult(String resultType) {
        try {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            if ("Tabela".equals(resultType)) {
                JTable resultTable = new JTable();
                resultTable.setAutoCreateRowSorter(true);
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                resultTable.setModel(resultFetcher);
                for (int i = 0; i < resultFetcher.getColumnCount() - 1; ++i) {
                    resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }
                JScrollPane scroll = new JScrollPane(resultTable);
                scroll.setSize(new Dimension(1000, 10));
                scroll.setPreferredSize(new Dimension(1000, scroll.getPreferredSize().height));
                panel.add(scroll);
            }
            if ("Diagram Gantta".equals(resultType)) {
                GanttChartMaker ganttChartMaker = new GanttChartMaker("Harmonogram Gantta", resultFetcher);
                panel.add(ganttChartMaker.getPanel());
            }
            if ("Graf CPM".equals(resultType)) {
                //TUTAJ GRAF CPM
            }
            //to wyswietla okienko z wynikiem
            //jak chcesz swoje okienko do grafu/harmonogramu to przeloz te linijke do ifa z tabela
            JOptionPane.showConfirmDialog(null, panel, resultType, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
        catch (Exception e) {
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
        displayButton.addActionListener(e->{
            resultFetcher = new ResultFetcher();
            resultFetcher.setUserData(dataFetcher);
            displayResult((String) displayBox.getSelectedItem());
        });
    }
    public void createDisplayBox(){
        displayBox.setModel(new DefaultComboBoxModel<>(new String[]{"Tabela", "Diagram Gantta", "Graf CPM"}));
    }
    public void createSelectableRecord(){
        showTable.setRowSelectionAllowed(true);
        showTable.getSelectionModel().addListSelectionListener(e -> {
            editButton.setEnabled(showTable.getSelectedRow()!=-1);
            removeButton.setEnabled(showTable.getSelectedRow()!=-1);
        });

    }
    public void errorWindow(Exception e) {
        String message = "Coś poszło nie tak!\n";
        if (e.getMessage() != null) {
            message += e.getMessage();
            message += "\n";
        }
        message += "Spróbuj ponownie!\n";
        JOptionPane.showMessageDialog(new JFrame(), message);
    }
    public int recordWindow(JPanel panel, Object[] record, String title) {
        int n = 2;
        try {
            JTextField czynnosc = new JTextField((String)record[0]);
            JTextField poprzednik = new JTextField((String)record[1]);
            JTextField czas = new JTextField((String)record[2]);
            JLabel info1 = new JLabel("Przy braku poprzednika, pozpstaw puste pole, lub wpisz \"-\"");
            JLabel info2 = new JLabel("Przy wprowadzaniu wielu poprzedników, oddziel ich przecinkami");
            Font newLabelFont = new Font(info1.getFont().getName(), Font.ITALIC, info1.getFont().getSize());
            info1.setFont(newLabelFont);
            info2.setFont(newLabelFont);
            panel.add(new JLabel("Czynność:"));
            panel.add(czynnosc);
            panel.add(new JLabel("Poprzednik:\n"));
            panel.add(info1);
            panel.add(info2);
            panel.add(poprzednik);
            panel.add(new JLabel("Czas trwania:"));
            panel.add(czas);
            n = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (n == JOptionPane.OK_OPTION) {
                record[0] = czynnosc.getText();
                //record[0] = ((String) record[0]).replaceAll("\\s", "");
                record[1] = poprzednik.getText();
                //record[1] = ((String) record[1]).replaceAll("\\s", "");
                if (((String)record[1]).length() == 0) {
                    record[1] = "-";
                }
                record[2] = czas.getText();
                record[2] = ((String)record[2]).replaceAll("\\s", "");
            }
        }
        catch (Exception e) {
            errorWindow(e);
        }
        return n;
    }
    public void dataChecker(Object[] record, int row) throws Exception {
        String message = "\n";
        String pop = (String)record[1];
        double num = 1.;
        if (record[0].equals("") || record[2].equals("")) { //jesli zostaly puste pola
            message += "Nie podano wszystkich wymaganych danych!\n";
        }
        if (!dataFetcher.checkIfPrevAndNextDifferent(record)) { //jesli czynnosc jest swoim wlasnym poprzednikiem
            message += "Podana czynność jest swoim własnym poprzednikiem!\n";
        }
        if (dataFetcher.checkIfExists(record, row) && (row > -1)) { //sprawdź czy czynność istnieje
            message += "Podana czynność już znajduje się w tablicy!\n";
        }
        if (pop.contains("-") && pop.length() > 1) {
            message += "Czynność jednocześnie posiada i nie posiada poprzednika!\n";
        }
        if (!((String)record[1]).contains("-")) {
            if (!dataFetcher.checkIfPrevExists(record)) { //sprawdź czy poprzednik istnieje
                message += "Poprzednik nie istnieje!\n";
            }
            if (!dataFetcher.checkIfPrevUnique(record)) { //sprawdź czy poprzednicy są unikatowi
                message += "Poprzednicy nie są unikatowi!\n";
            }
        }
        try {
            num = Double.parseDouble((String)record[2]);
        }
        catch (NumberFormatException nfe) {
            message += "Podany czas nie jest liczbą!\n";
        }
        if (num < 0) {
            message += "Czas wykonania zadania jest mniejszy od 0!\n";
        }
        if (!message.equals("\n")) {
            throw new Exception(message);
        }
    }
    void textDataParser(Object[] dane) {
        String nazwa = (String)dane[0];
        dane[0] = nazwa.trim().replaceAll(" +", " ").replaceAll("[-+^]*", "");
        String poprz = (String)dane[1];
        dane[1] = poprz.trim().replaceAll("[ +^]*", "").replaceAll(",+", ",");
    }
}
