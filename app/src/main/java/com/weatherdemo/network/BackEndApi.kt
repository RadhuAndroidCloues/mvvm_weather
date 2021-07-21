package com.weatherdemo.network

import com.weatherdemo.model.current_temperature_responce.CurrentTempResponceModel
import com.weatherdemo.model.location_key_responce.LocationResponceModel
import com.weatherdemo.util.Constants
import retrofit2.Call
import retrofit2.http.*

/**
 * Class Name       :  **BackEndApi.KT****
 * Purpose          :  BackEndApi is interface for retrofit api.
 * Developed By     :  **@radhu_android**
 * Created Date     :  **21-07-2021**
 *
 *
 * Modified Reason  :  ****
 * Modified By      :  **@radhu_android**
 * Modified Date    :  ****
 *
 *
 ** */
interface BackEndApi {
    @GET("locations/v1/search")
    fun getLocation(@Query("q") search: String = "Pune", @Query("apikey") apikey: String = Constants.API_KEY): Call<List<LocationResponceModel>>

    @GET("currentconditions/v1/{city_id}")
    fun getWetherData(@Path("city_id") city_id: String, @Query("apikey") apikey: String = Constants.API_KEY, @Query("details") details: String ="true"): Call<List<CurrentTempResponceModel>>
}

