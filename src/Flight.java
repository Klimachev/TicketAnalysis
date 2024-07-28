import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    //Сведения о билетах, загруженные из .json-файла
    public  List<Ticket> tickets;

    /***
     * Минимальное время полета между городами originName и destinationName для авиаперевозчиков
     * @param originName город вылета
     * @param destinationName город прибытия
     * @return String - список авиаперевозчиков с указанием минимального времени перелета между городами originName и destinationName
     * @throws ParseException
     */
    public String getMinFlightTime(String originName, String destinationName) throws ParseException {
        //Для хранения перевозчиков и минимального времени перелета
        HashMap minFlightTime = new HashMap();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

        //Выбираются все билеты для маршрута originName->destinationName
        for(var t: tickets.stream().filter(p->p.origin_name.equals(originName)&& p.destination_name.equals(destinationName)).toArray()){
            Ticket ticket = (Ticket)t;

            //Предполагается, что в json хранится местное время вылета (приземления), поэтому строка преобразуется в дату/время с учетом часового пояса Владивостока (Тель-Авива).
            //Входные параметры позволяют сделать метод универсальным. Для улучшения можно добавить справочник часовых поясов и городов с автоматической подстановкой.
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+10"));//Часовой пояс Владивостока
            Date departureDateTime = simpleDateFormat.parse(ticket.departure_date+" "+ticket.departure_time);

            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));//Часовой пояс Тель-Авива
            Date arrivalDateTime = simpleDateFormat.parse(ticket.arrival_date+" "+ticket.arrival_time);

            long flightTime = arrivalDateTime.getTime() - departureDateTime.getTime();//Время полета в миллисекундах
            if(minFlightTime.get(ticket.carrier)==null || (long)minFlightTime.get(ticket.carrier)>flightTime)
                minFlightTime.put(ticket.carrier, flightTime);
        }

        StringBuilder stringBuilder = new StringBuilder(String.format("Минимальное время полета между городами %s и %s для авиаперевозчиков:", originName, destinationName));
        stringBuilder.append(System.lineSeparator());

        for(var carrier: minFlightTime.keySet()){//Для всех авиаперевозчиков
            long flightTime = (long) minFlightTime.get(carrier);
            byte flightHours = (byte) (flightTime/1000/3600);//Время полета (часов)
            byte flightMinutes = (byte) ((flightTime/1000%3600)/60);//Время полета (минут)

            stringBuilder.append(String.format("%s: %d ч. %d мин.", carrier, flightHours, flightMinutes));
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    /***
     * Разница между средней ценой и медианой для полета между городами originName и destinationName
     * @param originName город вылета
     * @param destinationName город прибытия
     * @return String - Разница между средней ценой и медианой для полета между городами
     */
    public String getAvgMedianPriceDifference(String originName, String destinationName) {
        //Средняя цена полета между городами
        double avgPrice = tickets.stream().filter(p->p.origin_name.equals(originName)&& p.destination_name.equals(destinationName)).mapToDouble(t->t.price).average().getAsDouble();

        //Нахождение медианы цены полета между городами
        Object[] sortedTickets = tickets.stream().filter(p->p.origin_name.equals(originName)&& p.destination_name.equals(destinationName)).sorted((t1, t2)->(int)(t1.price-t2.price)).toArray();
        float median = 0;
        int ticketCnt = sortedTickets.length;
        if(ticketCnt%2==1)
            median = ((Ticket)sortedTickets[ticketCnt/2]).price;
        else median = (((Ticket)sortedTickets[ticketCnt/2]).price + ((Ticket)sortedTickets[ticketCnt/2-1]).price)/2;

        return String.format("Разница между средней ценой и медианой для полета между городами %s и %s: %.2f", originName, destinationName, avgPrice - median);
    }

    /***
     * Информация о билете
     */
    private static class Ticket {
        public String origin;
        public String origin_name;
        public String destination;
        public String destination_name;
        public String departure_date;
        public String departure_time;
        public String arrival_date;
        public String arrival_time;
        public String carrier;
        public int stops;
        public float price;
    }
}
