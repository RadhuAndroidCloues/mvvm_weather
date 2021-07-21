package com.weatherdemo.model.current_temperature_responce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Imperial {
    @SerializedName("Value")
    @Expose
    var value: Int? = null

    @SerializedName("Unit")
    @Expose
    var unit: String? = null

    @SerializedName("UnitType")
    @Expose
    var unitType: Int? = null
}