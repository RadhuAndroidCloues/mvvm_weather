package com.weatherdemo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weatherdemo.model.current_temperature_responce.CurrentTempResponceModel
import com.weatherdemo.model.location_key_responce.LocationResponceModel
import com.weatherdemo.network.BackEndApi
import com.weatherdemo.network.WebServiceClient
import com.weatherdemo.util.Constants
import com.weatherdemo.util.SingleLiveEvent
import com.weatherdemo.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class Name       :  **DemoViewModel.KT****
 * Purpose          :  DemoViewModel is class for logic and implementation.
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

class DemoViewModel(application: Application) : AndroidViewModel(application), Callback<List<CurrentTempResponceModel>> {


    val date = ObservableField<String>()
    val prime:MutableLiveData<Boolean> = MutableLiveData()
    val primeValue = ObservableField<String>()
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var apiErroe: SingleLiveEvent<String>? = null
    var cityClick: SingleLiveEvent<String>? = null
    var backClick: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<List<LocationResponceModel>>? = null


    val dayValue = ObservableField<String>()
    val yourCity = ObservableField<String>()
    val tempValue = ObservableField<String>()
    val minTemp = ObservableField<String>()
    val maxTemp = ObservableField<String>()

    var datePickerDialogData = MutableLiveData<Boolean>()

    init {
        progressDialog = SingleLiveEvent<Boolean>()
        apiErroe = SingleLiveEvent<String>()
        cityClick = SingleLiveEvent<String>()
        backClick = SingleLiveEvent<Boolean>()
        userLogin = MutableLiveData<List<LocationResponceModel>>()
    }

    fun dateClick() {
        datePickerDialogData.setValue(true);
    }
    fun locationClick() {
        cityClick?.postValue("")
    }

    fun getWeatherData(city_id: String) {
        WebServiceClient.client.create(BackEndApi::class.java).getWetherData(city_id, Constants.API_KEY)
                      .enqueue(this)
    }

    fun backClickListner() {
        backClick?.postValue(true)
    }

    fun onDateChange(current_city: String, str_date: String, int_date: Int) {
        date.set(str_date)
        dayValue.set("Day : "+Util.getDay(str_date))
        if(Util.isPrimeNumber(int_date)) {
            prime?.postValue(true)
            progressDialog?.value = true
            WebServiceClient.client.create(BackEndApi::class.java).getLocation(current_city, Constants.API_KEY)
                    .enqueue(object : Callback<List<LocationResponceModel>> {
                        override fun onFailure(call: Call<List<LocationResponceModel>>?, t: Throwable?) {
                            //callback.onError(t.toString())
                            progressDialog?.value = false
                        }

                        override fun onResponse(call: Call<List<LocationResponceModel>>?, response: Response<List<LocationResponceModel>>?) {
                            if (response?.isSuccessful!!) {
                                getWeatherData(response.body().get(0).key!!)
                            } else {
                                //setError(callback, response)
                                progressDialog?.value = false
                                if (response.code() == 503)
                                    apiErroe?.value = "The allowed number of requests has been exceeded."
                                else
                                    apiErroe?.value = "Server error."
                            }
                        }
                    })
        }else
           prime?.postValue(false)
        //primeValue.set("prime")
    }

    fun getDatePickerDialogData(): LiveData<Boolean?>? {
        return datePickerDialogData
    }

    override fun onResponse(call: Call<List<CurrentTempResponceModel>>?, response: Response<List<CurrentTempResponceModel>>?) {
        // dayValue.set("Day : " + response?.body()?.get(0)?.weatherText)
         tempValue.set(response?.body()?.get(0)?.temperature?.metric?.value.toString() + " C")
         minTemp.set(response?.body()?.get(0)?.temperatureSummary?.past12HourRange?.minimum?.metric?.value.toString() + " C")
         maxTemp.set(response?.body()?.get(0)?.temperatureSummary?.past12HourRange?.maximum?.metric?.value.toString() + " C")
         progressDialog?.value = false
    }

    override fun onFailure(call: Call<List<CurrentTempResponceModel>>?, t: Throwable?) {
        progressDialog?.value = false
    }




}