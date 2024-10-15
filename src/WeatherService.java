import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;


public class WeatherService {

    private final String URIString = "https://api.weather.yandex.ru/v2/forecast?" +
            "lat=%s&lon=%s";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String keyHeader = "X-Yandex-Weather-Key";
    private final String key = "94b3a755-f027-44fd-8aa6-dc31956fd683";
    private final JSONParser parser = new JSONParser();

    public void ShowWeatherInLocation(Location location){

        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(String.format(URIString, location.getX(), location.getY())))
                .setHeader(keyHeader, key)
                .GET()
                .build();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jObject = (JSONObject) parser.parse(response.body());

            System.out.printf("Погода в локации %s: %s%n", location.GetName(), response.body());
            System.out.println("Температура сегодня: " + ((JSONObject)jObject.get("fact")).get("temp"));
        }
        catch (Exception ex) {
            System.err.println("Error occurred while sending POST request: " + ex.getMessage());
        }
    }

    public void ShowAverageTemperatureForecast(Location location, int daysPeriod) {

        try{
            if(daysPeriod > 11 || daysPeriod < 1)
                throw new Exception("Average temperature can only be determined for a period from 1 to 11 days!");

            HttpRequest request = HttpRequest.newBuilder().
                    uri(URI.create(String.format(URIString + "&limit=%s", location.getX(), location.getY(), daysPeriod)))
                    .setHeader(keyHeader, key)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jObject = (JSONObject) parser.parse(response.body());
            var daysForecasts = (JSONArray)jObject.get("forecasts");

            long tempSum = 0;
            for (Object dayForecast : daysForecasts) {
                var day = jsonHelper((JSONObject) dayForecast, "parts");
                var tempNight = jsonHelper(day, "night").get("temp_avg");
                var tempEvening = jsonHelper(day, "evening").get("temp_avg");
                var tempDay = jsonHelper(day, "day").get("temp_avg");
                var tempMorning = jsonHelper(day, "morning").get("temp_avg");

                tempSum += ((long) tempNight + (long) tempDay + (long) tempMorning + (long) tempEvening) / 4;
            }

            System.out.printf("Ожидаемая средняя температура в локации %s в ближайшие %s дней: %s",
                    location.GetName(), daysPeriod, tempSum/daysPeriod);

        }
        catch (Exception ex) {
            System.err.println("Error occurred while sending POST request: " + ex.getMessage());
        }
    }

    private JSONObject jsonHelper(JSONObject obj, String key) {
        return (JSONObject) obj.get(key);
    }
}
