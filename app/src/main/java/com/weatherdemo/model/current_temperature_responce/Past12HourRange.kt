package com.weatherdemo.model.current_temperature_responce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Past12HourRange {
    @SerializedName("Minimum")
    @Expose
    var minimum: Minimum? = null

    @SerializedName("Maximum")
    @Expose
    var maximum: Maximum? = null
}