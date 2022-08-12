import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    JTextArea taskReportArea;
    JScrollPane scrollPane;
    ArrayList<Task> taskList = new ArrayList<>();
    ChartPanel chartPanel;

    public static void main(String[] args) {

        Main main = new Main();
    }

    public Main() {

        /*******************************************************
         adding, sizing and positioning graphic elements
         *******************************************************/
        JPanel mainPanel = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.setLayout(new GridBagLayout());

        JTextArea addingTaskTextArea = new JTextArea(15, 20);
        addingTaskTextArea.setBackground(Color.WHITE);
        addingTaskTextArea.setVisible(true);
        addingTaskTextArea.setLineWrap(true);
        addingTaskTextArea.setWrapStyleWord(true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(addingTaskTextArea, gbc);

        JButton addTaskButton = new JButton("ADD TASK");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(addTaskButton, gbc);

        JLabel taskReportLabel = new JLabel("Task Report");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(taskReportLabel, gbc);

        taskReportArea = new JTextArea(15, 20);
        taskReportArea.setBackground(Color.lightGray);
        taskReportArea.setVisible(true);
        taskReportArea.setEditable(false);
        taskReportArea.setLineWrap(true);
        taskReportArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(taskReportArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(scrollPane, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        ChartPanel chartPanel = createChart();
        mainPanel.add(chartPanel,gbc);

        JFrame mainFrame = new JFrame("Tasks chart");
        mainFrame.add(mainPanel);
        mainFrame.setSize(1000, 800);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
        /*************************************************************/


        //button click event
        addTaskButton.addActionListener(x -> {
            ArrayList list = new ArrayList(taskList);
            taskReportArea.setText("");
            Task task = new Task(addingTaskTextArea.getText(), false, LocalDate.now());
            list.add(task);
            taskList = new ArrayList<>(list);
            addingTaskTextArea.setText("");
            chartPanel.getChart().getXYPlot().setDataset(createDataset());
        });

    }

    private ChartPanel createChart() {

        DateAxis timeAxis = new DateAxis("Date");
        timeAxis.setDateFormatOverride(new SimpleDateFormat("YYYY-MM-dd"));
        NumberAxis numberAxis = new NumberAxis("Tasks");
        numberAxis.setAutoRangeIncludesZero(false);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        XYPlot plot = new XYPlot(createDataset(), timeAxis, numberAxis, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        JFreeChart lineChart = new JFreeChart("Tasks chart", plot);
        lineChart.setBackgroundPaint(Color.white);
        chartPanel = new ChartPanel(lineChart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 380);
            }
        };
        return chartPanel;
    }

    HashMap<LocalDate, Integer> assignedTasksMap = new HashMap<>();
    HashMap<LocalDate, Integer> completedTasksMap = new HashMap<>();

    private TimeSeriesCollection createDataset() {

        assignedTasksMap=new HashMap<>();
        completedTasksMap=new HashMap<>();

        readSomeDummyData()
                .stream()
                .forEach(x -> {
                    //only for completed tasks
                    if (x.isCompleted()) {
                        if (!completedTasksMap.containsKey(x.getDate())) {
                            completedTasksMap.put(x.getDate(), 1);
                        } else {
                            completedTasksMap.merge(x.getDate(), 1, Integer::sum);
                        }
                    }

                    //for all tasks, because completed tasks are also assigned tasks
                    if (!assignedTasksMap.containsKey(x.getDate())) {
                        assignedTasksMap.put(x.getDate(), 1);
                    } else {
                        assignedTasksMap.merge(x.getDate(), 1, Integer::sum);
                    }

                    //adding task to report area
                    fillReportArea(x);
                });

        return setTimeSeries();
    }

    private TimeSeriesCollection setTimeSeries() {
        TimeSeries assigned = new TimeSeries("ASSIGNED");
        TimeSeries completed = new TimeSeries("COMPLETED");
        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(assigned);
        collection.addSeries(completed);
        assigned = collection.getSeries("ASSIGNED");
        completed = collection.getSeries("COMPLETED");

        TimeSeries finalAssigned = assigned;

        assignedTasksMap.forEach((x, y) -> {
            finalAssigned.add(Day.parseDay(x.toString()), y);

        });

        TimeSeries finalCompleted = completed;
        completedTasksMap.forEach((x, y) -> {
            finalCompleted.add(Day.parseDay(x.toString()), y);
        });

        return collection;
    }

    private void fillReportArea(Task task) {
        taskReportArea.append("\n" + task.getDescription() + " \nCompleted: " + task.isCompleted() + " \nDate: " + task.getDate() + "\n");
    }


    private ArrayList<Task> readSomeDummyData() {
        if (taskList.isEmpty()) {

            //only assigned tasks, not completed
            taskList.add(new Task("@Tom - do stuff number 10", false, LocalDate.of(2022, 07, 22)));
            taskList.add(new Task("@Bob - do stuff number 11", false, LocalDate.of(2022, 07, 23)));
            taskList.add(new Task("@Tom - do stuff number 12", false, LocalDate.of(2022, 07, 23)));
            taskList.add(new Task("@John - do stuff number 13", false, LocalDate.of(2022, 07, 23)));
            taskList.add(new Task("@Susan - do stuff number 14", false, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Tom - do stuff number 15", false, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Susan - do stuff number 16", false, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Bob - do stuff number 17", false, LocalDate.of(2022, 07, 25)));
            taskList.add(new Task("@Susan - do stuff number 165", false, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Bob - do stuff number 175", false, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Susan - do stuff number 143", false, LocalDate.of(2022, 8, 8)));
            taskList.add(new Task("@Tom - do stuff number 154", false, LocalDate.of(2022, 8, 8)));
            taskList.add(new Task("@Susan - do stuff number 165", false, LocalDate.of(2022, 8, 8)));
            taskList.add(new Task("@Bob - do stuff number 176", false, LocalDate.of(2022, 8, 11)));
            taskList.add(new Task("@Bob - do stuff number 176", false, LocalDate.of(2022, 8, 10)));
            taskList.add(new Task("@Bob - do stuff number 176", false, LocalDate.of(2022, 8, 7)));
            taskList.add(new Task("@Bob - do stuff number 176", false, LocalDate.of(2022, 8, 1)));
            taskList.add(new Task("@Bob - do stuff number 177", false, LocalDate.of(2022, 8, 1)));
            taskList.add(new Task("@Bob - do stuff number 171", false, LocalDate.of(2022, 8, 1)));
            taskList.add(new Task("@Bob - do stuff number 123", false, LocalDate.of(2022, 8, 1)));

            //completed tasks
            taskList.add(new Task("@Tom - do stuff number 1", true, LocalDate.of(2022, 07, 22)));
            taskList.add(new Task("@Bob - do stuff number 2", true, LocalDate.of(2022, 07, 22)));
            taskList.add(new Task("@John - do stuff number 3", true, LocalDate.of(2022, 07, 22)));
            taskList.add(new Task("@John - do stuff number 8", true, LocalDate.of(2022, 07, 23)));
            taskList.add(new Task("@John - do stuff number 4", true, LocalDate.of(2022, 07, 23)));
            taskList.add(new Task("@Tom - do stuff number 5", true, LocalDate.of(2022, 07, 24)));
            taskList.add(new Task("@Susan - do stuff number 6", true, LocalDate.of(2022, 07, 25)));
            taskList.add(new Task("@Bob - do stuff number 7", true, LocalDate.of(2022, 07, 25)));
            taskList.add(new Task("@John - do stuff number 44", true, LocalDate.of(2022, 8, 07)));
            taskList.add(new Task("@Tom - do stuff number 55", true, LocalDate.of(2022, 8, 07)));
            taskList.add(new Task("@Susan - do stuff number 66", true, LocalDate.of(2022, 8, 10)));
            taskList.add(new Task("@Bob - do stuff number 77", true, LocalDate.of(2022, 8, 11)));

        }

        return taskList;
    }

}
