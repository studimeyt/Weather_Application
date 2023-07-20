package com.test.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.math.roundToInt
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var temperatureChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperatureChart = findViewById(R.id.temperatureChart)
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_fl))
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Toggle the visibility of the graph based on the bottom sheet state
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> showGraph()
                    BottomSheetBehavior.STATE_EXPANDED -> hideGraph()
                    // You can add more cases for other states if needed
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // No need to implement this method, but it's required as part of the interface
            }
        })


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

        findViewById<FrameLayout>(R.id.bottom_sheet_fl).setOnTouchListener { _, event ->
            // Detect if the bottom sheet is being touched
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                // If the bottom sheet is expanded, hide the graph
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    hideGraph()
                }
            }
            // Return false to ensure other touch events are also handled
            false
        }
    }
//    fun onStateChanged(bottomSheet: View, newState: Int) {
//        // Toggle the visibility of the graph based on the bottom sheet state
//        when (newState) {
//            BottomSheetBehavior.STATE_COLLAPSED -> showGraph()
//            BottomSheetBehavior.STATE_EXPANDED -> hideGraph()
//            // You can add more cases for other states if needed
//        }
//    }
//
//    fun onSlide(bottomSheet: View, slideOffset: Float) {
//        // No need to implement this method, but it's required as part of the interface
//    }
//
//    // Rest of your code...

    // Helper methods
    private fun showGraph() {
        temperatureChart.visibility = View.VISIBLE
    }

    private fun hideGraph() {
        temperatureChart.visibility = View.GONE
    }



    private fun updateGraph(temperatureData: List<Float>, days: List<String>) {
        val entries =
            temperatureData.mapIndexed { index, temperature -> Entry(index.toFloat(), temperature) }

        val dataSet = LineDataSet(entries, "Temperature")
        dataSet.color = Color.WHITE
        dataSet.setCircleColor(Color.BLUE)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.valueTextSize = 12f
        dataSet.setDrawValues(false) // Hide values on data points

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Use smooth curves

        // Customizing the appearance
        dataSet.setDrawFilled(true) // Fill the area under the line
        dataSet.fillColor = ColorTemplate.getHoloBlue()
        dataSet.fillAlpha = 150
        dataSet.setDrawHighlightIndicators(false) // Hide indicator on data points

        val lineData = LineData(dataSet)

        val temperatureChart = findViewById<LineChart>(R.id.temperatureChart)
        temperatureChart.data = lineData
        temperatureChart.description.isEnabled = false // Disable description
        temperatureChart.legend.isEnabled = false
        temperatureChart.setTouchEnabled(true) // Disable touch interactions
        temperatureChart.setPinchZoom(true) // Disable zoom
        val customGradient: Drawable? = ContextCompat.getDrawable(this, R.drawable.graph_gradient)
        // Customize axes appearance
        customGradient?.let {
            dataSet.fillDrawable = it
        }
        val xAxis = temperatureChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // X-axis position
        xAxis.setDrawGridLines(false) // Hide vertical grid lines
        xAxis.textColor = Color.WHITE
        xAxis.granularity = 1f // Set minimum interval to 1 (to display all labels)

        val yAxisLeft = temperatureChart.axisLeft
        yAxisLeft.isEnabled = false;
        yAxisLeft.setDrawGridLines(false) // Hide horizontal grid lines

        val yAxisRight = temperatureChart.axisRight
        yAxisRight.isEnabled = false // Disable right-side axis

        // Animation
        temperatureChart.animateX(800, Easing.EasingOption.EaseInOutQuad)
        temperatureChart.animateY(800, Easing.EasingOption.EaseInOutQuad)

        // Set the custom days' names as labels on the x-axis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)

        temperatureChart.invalidate() // Refresh the chart to apply changes
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
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    val calendar = Calendar.getInstance()
                    val days = mutableListOf<String>()
                    val temperatureData = mutableListOf<Float>()


                    for (index in 0 until minOf(forecastList.size, 4)) {
                        val forecastItem = forecastList[index]

                        val maxTemperature = forecastItem.main.temp_max.toFloat()
                        val minTemperature = forecastItem.main.temp_min.toFloat()
                        val dateTime = forecastItem.dt_txt



                        calendar.timeInMillis = dateFormat.parse(dateTime)?.time ?: 0

                        when (index) {
                            0 -> {
                                day_1_tv?.text = "Today"
                                temperatureData.add(maxTemperature)
                                max_1_tv?.text = "${maxTemperature.roundToInt()}°"
                                min_1_tv?.text = "${minTemperature.roundToInt()}°"
                                days.add("Today")
                            }
                            1 -> {
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                                day_2_tv?.text = dayFormat.format(calendar.time)
                                temperatureData.add(maxTemperature)
                                max_2_tv?.text = "${maxTemperature.roundToInt()}°"
                                min_2_tv?.text = "${minTemperature.roundToInt()}°"
                                days.add(day_2_tv?.text.toString())
                            }
                            2 -> {
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                                day_3_tv?.text = dayFormat.format(calendar.time)
                                temperatureData.add(maxTemperature)
                                max_3_tv?.text = "${maxTemperature.roundToInt()}°"
                                min_3_tv?.text = "${minTemperature.roundToInt()}°"
                                days.add(day_3_tv?.text.toString())
                            }
                            3 -> {
                                calendar.add(Calendar.DAY_OF_YEAR, 2)
                                day_4_tv?.text = dayFormat.format(calendar.time)
                                temperatureData.add(maxTemperature)
                                max_4_tv?.text = "${maxTemperature.roundToInt()}°"
                                min_4_tv?.text = "${minTemperature.roundToInt()}°"
                                days.add(day_4_tv?.text.toString())
                            }
                        }
                    }

                    updateGraph(temperatureData, days)
                }


            }

            override fun onFailure(call: Call<Example>, t: Throwable) {
                Log.d("DATA", t.toString())
            }
        })
    }
}
