import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        //.json-файл
        String filePath = args[0];
        if(filePath == null){
            System.out.println("Путь к файлу не задан.");
            return;
        }
        File file = new File(filePath);//Предполагается, что путь к файлу корректен. В противном случае можно добавить проверку либо try{} catch{}

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