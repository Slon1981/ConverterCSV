import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        System.out.println(list);


        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> employees = parseXML("data.xml");
        String json2 = listToJson(employees);
        writeString(json2, "data2.json");
    }

    private static void writeString(String json, String outputFile) {
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static List<Employee> parseCSV(String[] mapping, String fileName) {

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(fileReader);

            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBeanBuilder csvToBeanBuilder = new CsvToBeanBuilder(csvReader);
            CsvToBean csvToBean = csvToBeanBuilder.withMappingStrategy(strategy).build();


           return csvToBean.parse();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static List<Employee> parseXML(String file) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(file);

            Node node = document.getDocumentElement();
            NodeList list = node.getChildNodes();


            List<Employee> employees = new ArrayList<>();

            for (int i = 0; i < list.getLength(); i++) {
                Node node2 = list.item(i);

                if (node2.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node2;

                    String id = e.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = e.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = e.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = e.getElementsByTagName("country").item(0).getTextContent();
                    String age = e.getElementsByTagName("age").item(0).getTextContent();

                    Employee emp = new Employee(Long.valueOf(id), firstName,lastName,country, Integer.valueOf(age));
                    employees.add(emp);
                }

            }

            return employees;

        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
