package com.example.adminfoodapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.databinding.ActivityRevenueChartBinding
import com.example.adminfoodapp.utils.Constants
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RevenueChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRevenueChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRevenueChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }


        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        binding.tvCurrentTime.text = "Thời gian hiện tại: $currentTime"


        loadRevenueChart()
    }

    private fun loadRevenueChart() {
        val url = "${Constants.BASE_URL}get_food_revenue_chart.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(url, { response ->
            val jsonObject = JSONObject(response)
            if (jsonObject.getString("status") == "success") {
                val dataArray = jsonObject.getJSONArray("data")
                val entries = ArrayList<BarEntry>()
                val labels = ArrayList<String>()

                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    val name = item.getString("date")
                    val total = item.getDouble("revenue").toFloat()


                    entries.add(BarEntry(i.toFloat(), total))
                    labels.add(name)
                }

                val barDataSet = BarDataSet(entries, "Doanh thu (VNĐ)")
                barDataSet.color = Color.parseColor("#4CAF50")
                barDataSet.valueTextSize = 12f

                val barData = BarData(barDataSet)

                binding.barChart.data = barData
                binding.barChart.description.isEnabled = false
                binding.barChart.setFitBars(true)

                val xAxis = binding.barChart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f

                binding.barChart.axisRight.isEnabled = false
                binding.barChart.animateY(1000)
                binding.barChart.invalidate()
            }
        }, {
            it.printStackTrace()
        })

        requestQueue.add(stringRequest)
    }
}
