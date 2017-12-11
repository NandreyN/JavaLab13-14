
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 3. Дан файл, содержащий сведения о результатах сессии (номер зачетки, фамилия, номер семестра, название предмета, оценка).
 * Вводится строка
 * <p>
 * НомерСеместра НазваниеПредмета1 НазваниеПредмета2 …
 * <p>
 * Построить алфавитный список студентов, которые не сдали все экзамены в соответствующем семестре
 * (сортировка сразу по экзамену, затем по студенту).
 */
class Student {
    private String recordBook;
    private String surname;

    public Student(String recordBook, String surname) {
        this.recordBook = recordBook;
        this.surname = surname;
    }

    public Student(Student st) {
        this.recordBook = st.getRecordBook();
        this.surname = st.getSurname();
    }

    public String getRecordBook() {
        return recordBook;
    }

    public String getSurname() {
        return surname;
    }

    public String toString() {
        return getRecordBook() + " " + getSurname();
    }
}

class MyTableModel extends AbstractTableModel {
    private List<Record> records;
    private HashMap<Integer, HashSet<String>> subjects;
    private static final String[] HEADERS = {"Record Book", "Surname", "Term", "Subject", "Mark"};
    private List<Student> filtered;

    public MyTableModel(List<Record> records, HashMap<Integer, HashSet<String>> subjects) {
        this.records = new ArrayList<Record>(records);
        this.subjects = new HashMap<Integer, HashSet<String>>(subjects);
    }

    public List<Record> getRecords() {
        return records;
    }

    public void writeXmlFile() throws ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("Data");
        doc.appendChild(root);

        Element terms = doc.createElement("Terms");
        for (Integer key : subjects.keySet()) {
            Element term = doc.createElement("Term");
            term.setAttribute("number", String.valueOf(key));
            term.appendChild(doc.createTextNode(subjects.get(key).toString()));
            terms.appendChild(term);
        }
        root.appendChild(terms);

        Element recordsRoot = doc.createElement("Records");
        root.appendChild(recordsRoot);
        for(Record record : records)
        {
            Element recordElement = doc.createElement("Record");

            Element element = doc.createElement("RecordbookNumber");
            element.appendChild(doc.createTextNode(record.getRecordBookNumber()));
            recordElement.appendChild(element);

            element = doc.createElement("Surname");
            element.appendChild(doc.createTextNode(record.getSurname()));
            recordElement.appendChild(element);

            element = doc.createElement("TermNumber");
            element.appendChild(doc.createTextNode(record.getTermNumber()));
            recordElement.appendChild(element);

            element = doc.createElement("Subject");
            element.appendChild(doc.createTextNode(record.getSubject()));
            recordElement.appendChild(element);

            element = doc.createElement("Mark");
            element.appendChild(doc.createTextNode(record.getMark()));
            recordElement.appendChild(element);

            recordsRoot.appendChild(recordElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // additional spaces while outputting document tree

        DOMSource source = new DOMSource(doc);
        try {
            // location and name of XML file you can change as per need
            FileWriter fos = new FileWriter("./ros.xml");
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);

        } catch (IOException e) {

            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private String getTermByRecBookNumber(String recBook) {
        Optional<Record> record = records.stream().filter(x -> x.getRecordBookNumber().equals(recBook)).findFirst();
        return record.get().getTermNumber();
    }

    List<Student> getFilteredData() {
        filtered = new ArrayList<Student>();
        List<String> recBooks = new ArrayList<String>();

        HashMap<String, HashSet<String>> studentSubjectsAggregator = new HashMap<>();
        for (Record r : records) {
            if (!studentSubjectsAggregator.containsKey(r.getRecordBookNumber()))
                studentSubjectsAggregator.put(r.getRecordBookNumber(), new HashSet<>());
            studentSubjectsAggregator.get(r.getRecordBookNumber()).add(r.getSubject());
        }

        for (Map.Entry<String, HashSet<String>> pair : studentSubjectsAggregator.entrySet()) {
            // get course now by record book number
            String term = getTermByRecBookNumber(pair.getKey());
            if (pair.getValue().size() != subjects.get(Integer.parseInt(term)).size()) {
                recBooks.add(pair.getKey());
                continue;
            }

            for (String subj : subjects.get(Integer.parseInt(term))) {
                if (!pair.getValue().contains(subj)) {
                    recBooks.add(pair.getKey());
                    break;
                }
            }
        }

        List<Record> copy = new ArrayList<>(records);
        for (String rb : recBooks) {
            Record rec = copy.stream().filter(x -> x.getRecordBookNumber().equals(rb)).findFirst().get();
            filtered.add(new Student(rec.getRecordBookNumber(), rec.getSurname()));
        }
        return filtered;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void addValue(Record value) {
        records.add(value);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int index) {
        return HEADERS[index];
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= records.size())
            return "??";

        Record rec = records.get(rowIndex);
        Object ret = "??";
        switch (columnIndex) {
            case 0:
                ret = rec.getRecordBookNumber();
                break;
            case 1:
                ret = rec.getSurname();
                break;
            case 2:
                ret = rec.getTermNumber();
                break;
            case 3:
                ret = rec.getSubject();
                break;
            case 4:
                ret = rec.getMark();
                break;
        }
        return ret;
    }
}

class RecordHandler extends DefaultHandler {
    private HashMap<Integer, HashSet<String>> subjects;
    private ArrayList<Record> records;
    private String currentTag;
    private Record currentRecord;

    @Override
    public void startDocument() throws SAXException {
        subjects = new HashMap<>();
        records = new ArrayList<>();
        currentTag = "";
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public HashMap<Integer, HashSet<String>> getSubjects() {
        return subjects;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName.toLowerCase();
        if (currentTag.equals("term")) {
            subjects.put(Integer.parseInt(attributes.getValue("number")), new HashSet<String>());
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        switch (currentTag) {
            case "term":
                String input = new String(ch, start, length);
                for (int i = 1; i <= subjects.size(); i++) {
                    if (subjects.get(i).size() == 0) {
                        HashSet<String> s = new HashSet<>();
                        StringTokenizer tokenizer = new StringTokenizer(input, "[, ]");
                        while (tokenizer.hasMoreTokens())
                        {
                            s.add(tokenizer.nextToken());
                        }

                        subjects.put(i, s);
                        break;
                    }
                }
                break;

            case "record":
                currentRecord = new Record();
                break;
            case "recordbooknumber":
                currentRecord.setRecordBookNumber(new String(ch, start, length));
                break;
            case "surname":
                currentRecord.setSurname(new String(ch, start, length));
                break;
            case "termnumber":
                currentRecord.setTermNumber(new String(ch, start, length));
                break;
            case "subject":
                currentRecord.setSubject(new String(ch, start, length));
                break;
            case "mark":
                currentRecord.setMark(new String(ch, start, length));
                break;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (qName.toLowerCase().equals("record")) {
            records.add(currentRecord);
            currentRecord = null;
        }
        currentTag = "";
    }
}


public class MainFrame extends JFrame {

    private JMenuBar menuBar;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem addItem;
    private JMenu fileMenu;
    private InputDialog inputDialog;

    private JScrollPane scroll;
    private JPanel allRecPanel, filtRecPanel;

    private MyTableModel tableModel, filteredTableModel;
    private JTable table;
    private JList<String> filteredList;
    private DefaultListModel<String> listModel;

    private JSplitPane splitPane;

    public MainFrame() {

        this.setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        addItem = new JMenuItem("Add");
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(addItem);
        //filteredTableModel = new MyTableModel(new ArrayList<>(), new HashMap<>());
        //filteredTable = new JTable(filteredTableModel);
        listModel = new DefaultListModel<>();
        filteredList = new JList<>(listModel);

        filtRecPanel = new JPanel(new BorderLayout());
        filtRecPanel.setBorder(BorderFactory.createTitledBorder("Filtered"));
        filtRecPanel.add(filteredList, BorderLayout.CENTER);


        allRecPanel = new JPanel(new BorderLayout());
        tableModel = new MyTableModel(new ArrayList<Record>(), new HashMap<>());
        table = new JTable(tableModel);
        allRecPanel.setBorder(BorderFactory.createTitledBorder("All Records"));
        allRecPanel.add(table, BorderLayout.CENTER);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(allRecPanel);
        splitPane.setBottomComponent(filtRecPanel);
        splitPane.setDividerLocation(1);

        scroll = new JScrollPane(splitPane);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scroll, BorderLayout.CENTER);

        openMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().endsWith(".xml")) {
                        JOptionPane.showMessageDialog(null, "Choose xml file");
                        return;
                    }

                    try {
                        SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
                        SAXParser parser = factory.newSAXParser();
                        RecordHandler handler = new RecordHandler();
                        parser.parse(file, handler);

                        table.setModel(new MyTableModel(handler.getRecords(), handler.getSubjects()));
                        tableModel = (MyTableModel) table.getModel();
                        tableModel.fireTableDataChanged();

                        List<Student> rc = tableModel.getFilteredData();
                        DefaultListModel<String> newListModel = new DefaultListModel<>();
                        rc.stream().forEach(x -> newListModel.addElement(x.toString()));

                        filteredList.setModel(newListModel);
                        filteredList.repaint();

                    } catch (SAXException e1) {
                        e1.printStackTrace();
                    } catch (ParserConfigurationException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        saveMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().endsWith("xml"))
                        file = new File(file.getAbsolutePath() + ".xml");
                    if (!file.exists())
                        try {
                            file.createNewFile();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            return;
                        }
                    try {
                        tableModel.writeXmlFile();
                    } catch (ParserConfigurationException e1) {
                        e1.printStackTrace();
                    } catch (TransformerConfigurationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        addItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callModalInput();
            }
        });


        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
    }

    private void callModalInput() {
        inputDialog = new InputDialog(this);
        inputDialog.setBounds(0, 0, 800, 100);
        inputDialog.setVisible(true);

        Record toAdd = inputDialog.getRecord();
        tableModel.addValue(toAdd);
    }

    public static void main(String args[]) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setBounds(300, 300, 500, 500);
        frame.setVisible(true);
    }
}
