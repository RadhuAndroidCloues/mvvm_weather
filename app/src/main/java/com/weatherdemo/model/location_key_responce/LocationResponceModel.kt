package com.weatherdemo.model.location_key_responce

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LocationResponceModel {
    @SerializedName("Version")
    @Expose
    var version: Int? = null

    @SerializedName("Key")
    @Expose
    var key: String? = null

    @SerializedName("Type")
    @Expose
    var type: String? = null

    @SerializedName("Rank")
    @Expose
    var rank: Int? = null
}