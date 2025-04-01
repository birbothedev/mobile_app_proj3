package com.example.project3

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private val coinList = ArrayList<Coin>()
    private val namesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        retrieveCoinInfo()
    }

    private fun retrieveCoinInfo(){
        val coinURL = "https://api.coincap.io/v2/assets"
        val queue = Volley.newRequestQueue(this)

        // this shortened syntax for request/response was recommended by the ide
        val stringRequest = StringRequest (
            Request.Method.GET, coinURL,
            { response ->
                // create json object then get data array from object
                val jsonResponse = JSONObject(response)
                val coinArray : JSONArray = jsonResponse.getJSONArray("data")

                for (i in 0 until coinArray.length()) {
                    val theCoin : JSONObject = coinArray.getJSONObject(i)

                    val coinNames = theCoin.getString("name")
                    val coinSupply = theCoin.getDouble("supply").roundToInt().toString()
                    val coinSymbol = theCoin.getString("symbol")
                    val coinPrice = roundNumber(theCoin.getDouble("priceUsd"))

                    // check to avoid null error
                    val coinPercentChange: String = if (!theCoin.isNull("changePercent24Hr")){
                        roundNumber(theCoin.getDouble("changePercent24Hr"))
                    } else {
                        "0.00"
                    }
                    coinList.add(Coin(coinNames, coinSymbol, coinPrice, coinPercentChange, coinSupply))
                    namesList.add(coinNames)
                }

                // update spinner inside retrieveCoinInfo to ensure dropdown is populated after list is created
                runOnUiThread {
                    val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, namesList)
                    binding.coinSpinner.adapter = adapter
                }

                binding.coinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                        val selectedItem = parent.getItemAtPosition(position) as String
                        displayCoinInfo(selectedItem)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
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

    private fun displayCoinInfo(coinName : String){
        for (coin in coinList){
            if (coin.name == coinName){
                val coinPrice = "$${coin.price}"
                val coinPercent = "${coin.percentChange}%"
                binding.symbolView.text = coin.symbol
                binding.priceView.text = coinPrice
                binding.percentView.text = coinPercent
                binding.supplyView.text = coin.supply
                binding.coinNameTextView.text = coinName
            }
        }
    }
}