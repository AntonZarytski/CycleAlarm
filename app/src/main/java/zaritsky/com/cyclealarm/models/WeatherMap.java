package zaritsky.com.cyclealarm.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON file - converter
 */
public class WeatherMap {
    @SerializedName("cod")
    String code;
    @SerializedName("message")
    String message;
    @SerializedName("cnt")
    Integer count;
    @SerializedName("list")
    List<Item> list;
    @SerializedName("city")
    City city;

    public static WeatherMap map() {
        WeatherMap map = new WeatherMap();
        map.code = "code";
        map.message = "message";
        map.count = 1;
        map.list = new ArrayList<>();
        map.city = getStaticCity(map);
        return map;
    }

    private static City getStaticCity(WeatherMap weatherMap) {
        City city = weatherMap.getCity();
        city.country = "Belarus";
        city.id = 74;
        city.name = "Minsk";
        return city;
    }

    private City getCity() {
        return new City();
    }

    public Integer getHumidity() {
        return list.get(0).mainElement.humidity;
    }

    public Float getPressure() {
        return list.get(0).mainElement.pressure * 0.75006375541921f;
    }

    public String getDescription() {
        return list.get(0).weatherElements.get(0).description;
    }

    public String getName() {
        return city.name;
    }

    public String getCountry() {
        return city.country;
    }

    public Float getTemp() {
        return list.get(0).mainElement.temp - 273.15f;
    }

    public Float getWindSpeed() {
        return list.get(0).windElement.speed;
    }


    private class Item {

        @SerializedName("dt")
        Integer dt;

        @SerializedName("main")
        MainElement mainElement;

        @SerializedName("weather")
        List<WeatherElement> weatherElements;

        @SerializedName("clouds")
        CloudsElement cloudsElement;

        @SerializedName("wind")
        WindElement windElement;

        @SerializedName("snow")
        SnowElement snowElement;

        @SerializedName("sys")
        SysElement sysElement;

        @SerializedName("dt_txt")
        String dt_txt;

    }

    private class MainElement {

        @SerializedName("temp")
        Float temp;
        @SerializedName("temp_min")
        Float temp_min;
        @SerializedName("temp_max")
        Float temp_max;
        @SerializedName("pressure")
        Float pressure;
        @SerializedName("sea_level")
        Float sea_level;
        @SerializedName("grnd_level")
        Float grnd_level;
        @SerializedName("humidity")
        Integer humidity;
        @SerializedName("temp_kf")
        Float temp_kf;

    }

    private class WeatherElement {
        @SerializedName("id")
        Integer id;
        @SerializedName("main")
        String main;
        @SerializedName("description")
        String description;
        @SerializedName("icon")
        String icon;
    }

    private class CloudsElement {
        @SerializedName("all")
        Integer all;
    }

    private class WindElement {
        @SerializedName("speed")
        Float speed;
        @SerializedName("deg")
        Float deg;
    }

    private class SnowElement {
        @SerializedName("3h")
        Float h;
    }

    private class SysElement {
        @SerializedName("pod")
        String pod;
    }

    private class City {

        @SerializedName("id")
        Integer id;

        @SerializedName("name")
        String name;

        @SerializedName("country")
        String country;

        @SerializedName("coord")
        Coordinate coordinate;
    }

    private class Coordinate {
        @SerializedName("lat")
        Float lat;
        @SerializedName("lon")
        Float lon;
    }

}
