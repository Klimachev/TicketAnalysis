import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        //.json-файл
        File file = new File(args[0]);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //Десериализация .json-файла
            Flight flight = objectMapper.readValue(file, Flight.class);

            System.out.println(flight.getMinFlightTime("Владивосток", "Тель-Авив"));
            System.out.println(flight.getAvgMedianPriceDifference("Владивосток", "Тель-Авив"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}