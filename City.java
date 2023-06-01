
import java.util.Date;

public class City {
    private String city_name;
    private double temp;
    private int humidity;
    private double windSpeed;
    private Date date;

    public City() {
    }

    public City(String city_name, double temp, int humidity, double windSpeed, Date date) {
        this.city_name = city_name;
        this.temp = temp -273.15;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.date = date;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
