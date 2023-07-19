package com.test.myapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
var temperature_tv: TextView? = null
var day_tv: TextView? = null
var time_tv: TextView? = null
var location_tv: TextView? = null
var humidity_tv: TextView? = null

var day_1_tv: TextView? = null
var max_1_tv: TextView? = null
var min_1_tv: TextView? = null

var day_2_tv: TextView? = null
var max_2_tv: TextView? = null
var min_2_tv: TextView? = null

var day_3_tv: TextView? = null
var max_3_tv: TextView? = null
var min_3_tv: TextView? = null

var day_4_tv: TextView? = null
var max_4_tv: TextView? = null
var min_4_tv: TextView? = null


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperature_tv = findViewById(R.id.temperature_tv)
        day_tv = findViewById(R.id.day_tv)
        time_tv = findViewById(R.id.time_tv)
        location_tv=findViewById(R.id.location_tv)
        humidity_tv=findViewById(R.id.precipitation_tv)

        day_1_tv = findViewById(R.id.day_1)
        max_1_tv = findViewById(R.id.max_day_1)
        min_1_tv = findViewById(R.id.min_day_1)

        day_2_tv = findViewById(R.id.day_2)
        max_2_tv = findViewById(R.id.max_day_2)
        min_2_tv = findViewById(R.id.min_day_2)

        day_3_tv = findViewById(R.id.day_3)
        max_3_tv = findViewById(R.id.max_day_3)
        min_3_tv = findViewById(R.id.min_day_3)

        day_4_tv = findViewById(R.id.day_4)
        max_4_tv = findViewById(R.id.max_day_4)
        min_4_tv = findViewById(R.id.min_day_4)

        fetchWeatherData()

        window.statusBarColor = resources.getColor(R.color.main)

        BottomSheetBehavior.from(findViewById<FrameLayout>(R.id.bottom_sheet_fl)).apply {
            peekHeight = 200
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun fetchWeatherData() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData("Delhi", "336cd2fa430623841e2c5e0cfa009687", "metric")

        fun getFormattedDay(day: String, offset: Int): String {
            val daysOfWeek = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val index = (daysOfWeek.indexOf(day) + offset) % 7
            return daysOfWeek[index]
        }

        response.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                val responseBody = response.body()

                responseBody?.list?.get(0)?.let { forecastItem ->
                    val temperature = forecastItem.main.temp.toInt().toString() + "°"
                    val dateTime = forecastItem.dt

                    val city = "Delhi"
                    val humidity = forecastItem.main.humidity.toString()+" %"

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = (dateTime * 1000).toLong()

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val time = dateFormat.format(calendar.time)

                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    val day = dayFormat.format(calendar.time).toString()+" , "

                    temperature_tv?.text = temperature
                    time_tv?.text = time
                    day_tv?.text = day
                    location_tv?.text = city
                    humidity_tv?.text=humidity
                }
                responseBody?.list?.let { forecastList ->
                    val calendar = Calendar.getInstance()
                    val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

                    for (index in 0 until minOf(forecastList.size, 4)) {
                        val forecastItem = forecastList[index]

                        val maxTemperature = forecastItem.main.temp_max.toInt().toString() + "°"
                        val minTemperature = forecastItem.main.temp_min.toInt().toString() + "°"
                        val dateTime = forecastItem.dt

                        calendar.timeInMillis = (dateTime * 1000).toLong()
                        val forecastDay = calendar.get(Calendar.DAY_OF_WEEK)

                        val dayOffset = (forecastDay - currentDay + index-2) % 7

                        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                        val day = dayFormat.format(calendar.time).toString()

                        when (index) {
                            0 -> {
                                day_1_tv?.text = "Today"
                                max_1_tv?.text = maxTemperature
                                min_1_tv?.text = minTemperature
                            }
                            1 -> {
                                day_2_tv?.text = getFormattedDay(day, dayOffset)
                                max_2_tv?.text = maxTemperature
                                min_2_tv?.text = minTemperature
                            }
                            2 -> {
                                day_3_tv?.text = getFormattedDay(day, dayOffset)
                                max_3_tv?.text = maxTemperature
                                min_3_tv?.text = minTemperature
                            }
                            3 -> {
                                day_4_tv?.text = getFormattedDay(day, dayOffset)
                                max_4_tv?.text = maxTemperature
                                min_4_tv?.text = minTemperature
                            }
                        }
                    }
                }

            }



            override fun onFailure(call: Call<Example>, t: Throwable) {
                Log.d("DATA", t.toString())
            }
        })
    }
}
