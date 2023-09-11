import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        System.out.println(list);


        String json = listToJson(list);

        writeString(json);
    }

    private static void writeString(String json) {
        try {
            FileWriter writer = new FileWriter("data.json");
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

        try {
            FileReader fileReader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(fileReader);

            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBeanBuilder csvToBeanBuilder = new CsvToBeanBuilder(csvReader);
            CsvToBean csvToBean = csvToBeanBuilder.withMappingStrategy(strategy).build();

            fileReader.close();

           return csvToBean.parse();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
