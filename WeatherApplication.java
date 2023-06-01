import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.InetAddress;
import java.net.UnknownHostException;



public class WeatherApplication extends JFrame {

    private static final String API_KEY = "983a0beb78678ca18af5ee4c134a3bb7";

    private JLabel cityLabel;
    private JLabel tempLabel;
    private JLabel humidityLabel;
    private JLabel windSpeedLabel;
    private JLabel dateLabel;

    private JTextField searchField;
    private JButton searchButton;

    public WeatherApplication(String city_name, String temp,String humidity,String windSpeed, String date) {

        createTable();
        
        setTitle("Ramclief Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());


        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String city = searchField.getText();
                searchIt(city);
            }
        });

      

        cityLabel = new JLabel("City : " + city_name);
        tempLabel = new JLabel("Temperature : " + temp + " °C");
        humidityLabel = new JLabel("Humidity : " + humidity + " %");
        windSpeedLabel = new JLabel("Wind Speed : " + windSpeed + " m/s");
        dateLabel = new JLabel("Date : " + date);


        add(cityLabel);
        add(tempLabel);
        add(humidityLabel);
        add(windSpeedLabel);
        add(dateLabel);

        

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(searchField, constraints);
        constraints.insets = new Insets(5, 5, 10, 5);
        panel.add(searchButton, constraints);

        
        panel.add(cityLabel, constraints);
        panel.add(tempLabel, constraints);
        panel.add(humidityLabel, constraints);
        panel.add(windSpeedLabel, constraints);
        panel.add(dateLabel, constraints);

        
        add(panel);


        
        pack();
        setResizable(false);
        setLocationRelativeTo(null); 
        setVisible(true);
    }


    public static City getWeatherByCity(String city) {
        City weatherInfo = null;
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                String cityName = json.getString("name");
                double temperature = json.getJSONObject("main").getDouble("temp");
                int humidity = json.getJSONObject("main").getInt("humidity");
                double windSpeed = json.getJSONObject("wind").getDouble("speed");
                Date date = new Date(json.getLong("dt") * 1000);

                weatherInfo = new City(cityName, temperature, humidity, windSpeed, date);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }

    public static String getUserCity() {
        try {
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
    
                JSONObject jsonResponse = new JSONObject(response.toString());
                double latitude = jsonResponse.getDouble("lat");
                double longitude = jsonResponse.getDouble("lon");
    
                // Get city name based on user's geolocation
                URL weatherUrl = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                        "&lon=" + longitude + "&appid=" + API_KEY);
                HttpURLConnection weatherConnection = (HttpURLConnection) weatherUrl.openConnection();
                weatherConnection.setRequestMethod("GET");
    
                int weatherResponseCode = weatherConnection.getResponseCode();
                if (weatherResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader weatherReader = new BufferedReader(
                            new InputStreamReader(weatherConnection.getInputStream()));
                    StringBuilder weatherResponse = new StringBuilder();
                    String weatherLine;
                    while ((weatherLine = weatherReader.readLine()) != null) {
                        weatherResponse.append(weatherLine);
                    }
                    weatherReader.close();
    
                    JSONObject weatherJson = new JSONObject(weatherResponse.toString());
                    String cityName = weatherJson.getString("name");
                    return cityName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void searchIt(String city_name){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        
        if(isInternetConnected()){
            City weatherInfo = getWeatherByCity(city_name);
        if (weatherInfo != null) {

            
            
            City city = getWeatherByCity(city_name);

            insertCity(city);

            String td_Date = dateFormat.format(city.getDate());
        
            WeatherApplication app = new WeatherApplication(city.getCity_name(),decimalFormat.format(city.getTemp()),Integer.toString(city.getHumidity()),Double.toString(city.getWindSpeed()), td_Date);
            } else {
                JOptionPane.showMessageDialog(null, "Aucune information trouv\u00E9e pour \"" + city_name + "\"", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
        else{
            try {
                List<City> cities = getAllCities();
                City off_weatherInfo = null;
                boolean found = false;
                for (City c : cities) {
                    if (c.getCity_name().equals(city_name)) {
                        found = true;
                        off_weatherInfo = c;
                        break;
                    }
                }

                if (found){
                    String td_Date = dateFormat.format(off_weatherInfo.getDate());
                WeatherApplication app = new WeatherApplication(off_weatherInfo.getCity_name(),decimalFormat.format(off_weatherInfo.getTemp()),Integer.toString(off_weatherInfo.getHumidity()),Double.toString(off_weatherInfo.getWindSpeed()), td_Date);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Aucune information trouv\u00E9e pour \"" + city_name + "\"", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        
    }


    //BASE DE DONNEES
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:weather.db";
        return DriverManager.getConnection(url);
    }

    
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS cities (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, temperature REAL, humidity INTEGER, windSpeed REAL, date TEXT)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void insertCity(City city) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");

        String td_Date = dateFormat.format(city.getDate());

        String sql = "INSERT INTO cities (name, temperature, humidity, windSpeed, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city.getCity_name());
            stmt.setDouble(2, city.getTemp());
            stmt.setInt(3, city.getHumidity());
            stmt.setDouble(4, city.getWindSpeed());
            stmt.setString(5, td_Date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<City> getAllCities() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM cities";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                double temperature = rs.getDouble("temperature");
                int humidity = rs.getInt("humidity");
                double windSpeed = rs.getDouble("windSpeed");
                String date = rs.getString("date");
                Date parsedDate = dateFormat.parse(date);
                City city = new City(name, temperature, humidity, windSpeed, parsedDate);
                cities.add(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static boolean isInternetConnected() {
        try {
            InetAddress.getByName("www.google.com");
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    



    public static void main(String[] args) throws ParseException {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");

        if( isInternetConnected()){
            City city = getWeatherByCity(getUserCity());

            String td_Date = dateFormat.format(city.getDate());
            
            WeatherApplication app = new WeatherApplication(city.getCity_name(),decimalFormat.format(city.getTemp()),Integer.toString(city.getHumidity()),Double.toString(city.getWindSpeed()), td_Date);
        }
        else{                   
            JOptionPane.showMessageDialog(null, "<html>Une erreur critique est survenue !!!<br>Veuillez vous connecter a internet pour y remedier.</html>", "Erreur", JOptionPane.ERROR_MESSAGE);


        }
        

        
    }
}
