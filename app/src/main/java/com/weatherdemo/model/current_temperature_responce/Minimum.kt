package com.weatherdemo.model.current_temperature_responce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Minimum {
    @SerializedName("Metric")
    @Expose
    var metric: Metric1? = null

    @SerializedName("Imperial")
    @Expose
    var imperial: Imperial? = null
}