package com.example.tvshowerappkotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.tvshowerappkotlin.data.TVShowerModel
import com.example.tvshowerappkotlin.network.TVShowerClient
import com.example.tvshowerappkotlin.network.TVShowerWebServices
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var webServices: TVShowerWebServices
    lateinit var webClient: TVShowerClient
    private lateinit var sharedPref: SharedPreferences
    private var data : TVShowerModel? = null
    private var showName : String? = null
    private val MyPreference = "myPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(MyPreference, Context.MODE_PRIVATE)
        webClient = TVShowerClient()
        webServices = webClient.retrofitInstance.create(TVShowerWebServices::class.java)

        btSearch.setOnClickListener {
            it.hideKeyboard()
            showName = edName.text.toString()

            if (!showName.isNullOrEmpty() && !showName.isNullOrBlank()){

                if (sharedPref.contains(showName)) {
                    useCachedData(showName)
                }else {
                    callWebservice(showName)
                }
            }else {
                removeViews()
                tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun callWebservice(user: String?) {

        val call: Call<TVShowerModel> = webServices.getTVShow(user)
        call.enqueue(object : Callback<TVShowerModel> {

            override fun onFailure(call: Call<TVShowerModel>, t: Throwable) {
                Log.v("retrofit", "call failed")
                removeViews()
                tvNetworkError.visibility = View.VISIBLE
                tvError.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<TVShowerModel>, response: Response<TVShowerModel>
            ) {
                data = response.body()

                data?.let { write(showName, it) }

                data?.let { displayData(it) }
            }
        })
    }

    //Display data
    private fun displayData(data: TVShowerModel){

        removeErrorMsgs()

        tvDays.text = getString(R.string.num_of_days)

        tvName.text = data.name

        val date = calculateNumOfDays(data.premiered)
        tvNumber.text = date

        Picasso.get()
            .load(data.image.original)
            .into(imgView)

        drawViews()
    }

    //Remove error messages
    private fun removeErrorMsgs(){
        tvError.visibility = View.GONE
        tvNetworkError.visibility = View.GONE
    }

    private fun removeViews(){
        tvName.visibility = View.GONE
        tvNumber.visibility = View.GONE
        tvDays.visibility = View.GONE
        imgView.visibility = View.GONE
    }

    //Draw views
    private fun drawViews(){
        tvName.visibility = View.VISIBLE
        tvNumber.visibility = View.VISIBLE
        tvDays.visibility = View.VISIBLE
        imgView.visibility = View.VISIBLE
    }

    //Hide the keyboard
    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    //Calculate number of days
    private fun calculateNumOfDays(date: String) : String{
        val pattern = "yyyy-MM-dd"
        val sdf: DateFormat = SimpleDateFormat(pattern, Locale.UK)
        val startDateValue: Date? = sdf.parse(date)

        val z: ZoneId = ZoneId.of("Europe/London")
        val today: LocalDate = LocalDate.now(z)
        val currentDateValue: Date? = sdf.parse("$today")
        val diff: Long = currentDateValue!!.time - (startDateValue!!.time)
        val numDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

        return numDays.toString()
    }

    private fun write(showName: String?, data: TVShowerModel){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val sData: String = gson.toJson(data)
        editor.putString(showName, sData)
        editor.apply()
    }

    private fun read(showName: String?): TVShowerModel{
        var json: String? = null
        if (sharedPref.contains(showName))
            json = sharedPref.getString(showName, "")

        return Gson().fromJson(json, TVShowerModel::class.java)
    }

    //Use cached version of data
    private fun useCachedData(showName: String?){
        val data: TVShowerModel = read(showName)

        displayData(data)
    }
}