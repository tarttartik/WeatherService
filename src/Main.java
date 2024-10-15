//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        var novosibirsk = new Location("Новосибирск", 55.0415000, 82.9346000); //the center of Novosibirsk
        var weatherService = new WeatherService();

        weatherService.ShowWeatherInLocation(novosibirsk);
        weatherService.ShowAverageTemperatureForecast(novosibirsk, 5);

        System.console().readLine();
    }
}