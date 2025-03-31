package com.example.project3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.project3.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var coinNames : String
    private lateinit var coinSupply : String
    private lateinit var coinSymbol : String
    private lateinit var coinPrice : String
    private lateinit var coinPercentChange : String
    private val coinList = ArrayList<Coin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        retrieveCoinInfo()

        binding.coinInfoButton.setOnClickListener {
            Log.i("MainActivity", "price: $coinPrice")
        }

    }

    private fun retrieveCoinInfo(){
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

                    coinNames = theCoin.getString("name")
                    coinSupply = theCoin.getDouble("supply").roundToInt().toString()
                    coinSymbol = theCoin.getString("symbol")
                    coinPrice = roundNumber(theCoin.getDouble("priceUsd"))
                    coinPercentChange = roundNumber(theCoin.getDouble("changePercent24Hr"))

                    coinList.add(Coin(coinNames, coinSymbol, coinPrice, coinPercentChange, coinSupply))

                }
            },
            {
                Log.i("MainActivity", "That didn't work!")
            })

        queue.add(stringRequest)
    }

    private fun roundNumber(double : Double): String {
        // round to 2 decimal places and return as a string
        return "%.2f".format(double)
    }

}