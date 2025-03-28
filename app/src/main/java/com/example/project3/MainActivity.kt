package com.example.project3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.project3.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        binding.coinInfoButton.setOnClickListener {
            printCoinInfo();
        }
    }

    private fun printCoinInfo(){
        val coinURL = "https://api.coincap.io/v2/assets"
        val queue = Volley.newRequestQueue(this)

        // this syntax was recommended by the ide
        val stringRequest = StringRequest (
            Request.Method.GET, coinURL,
            { response ->
                // create json object then get data array from object
                val jsonResponse = JSONObject(response)
                val coinArray : JSONArray = jsonResponse.getJSONArray("data")

                for (i in 0 until coinArray.length()) {
                    val theCoin : JSONObject = coinArray.getJSONObject(i)

                    Log.i("MainActivity",
                        "Coin Name: ${theCoin.getString("name")}")
                    Log.i("MainActivity",
                        "Coin Symbol: ${theCoin.getString("symbol")}")
                }
            },
            {
                Log.i("MainActivity", "That didn't work!")
            })

        queue.add(stringRequest)
    }
}