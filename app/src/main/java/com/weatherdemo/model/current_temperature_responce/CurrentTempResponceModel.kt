package com.weatherdemo.model.current_temperature_responce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.weatherdemo.model.current_temperature_responce.TemperatureSummary

class CurrentTempResponceModel {
    @SerializedName("LocalObservationDateTime")
    @Expose
    var localObservationDateTime: String? = null

    @SerializedName("WeatherText")
    @Expose
    var weatherText: String? = null

    @SerializedName("Temperature")
    @Expose
    var temperature: Temperature? = null

    @SerializedName("TemperatureSummary")
    @Expose
    var temperatureSummary: TemperatureSummary? = null
}