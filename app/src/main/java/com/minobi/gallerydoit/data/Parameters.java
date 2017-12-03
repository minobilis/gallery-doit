
package com.minobi.gallerydoit.data;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Parameters {

    @SerializedName("latitude")
    private Long Latitude;
    @SerializedName("longitude")
    private Long Longitude;
    @SerializedName("weather")
    private String Weather;

    public Long getLatitude() {
        return Latitude;
    }

    public void setLatitude(Long latitude) {
        Latitude = latitude;
    }

    public Long getLongitude() {
        return Longitude;
    }

    public void setLongitude(Long longitude) {
        Longitude = longitude;
    }

    public String getWeather() {
        return Weather;
    }

    public void setWeather(String weather) {
        Weather = weather;
    }

}
