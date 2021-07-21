package com.weatherdemo.model.current_temperature_responce

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.weatherdemo.model.current_temperature_responce.Past12HourRange

class TemperatureSummary {
    @SerializedName("Past12HourRange")
    @Expose
    var past12HourRange: Past12HourRange? = null
}