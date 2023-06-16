package com.example.crypto.apiManager

import com.example.crypto.apiManager.model.CoinsData
import com.example.crypto.apiManager.model.NewsData
import com.example.crypto.apiManager.model.ChartData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


class ApiManager {

    private val apiService : ApiService

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String , String>>>){

        apiService.getTopNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {

                val data = response.body()!!

//                val title = data.data[0].title
//                val url = data.data[0].url
//                val pair = Pair<String , String>(title , url)

                val dataToSend: ArrayList<Pair<String , String>> = arrayListOf()
                data.data.forEach {
                    dataToSend.add(
                        Pair(it.title , it.url)
                    )
                }
                apiCallback.onSuccess(dataToSend)
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })

    }

    fun getCoinsList(apiCallback: ApiCallback<List<CoinsData.Data>>){

        apiService.getTopCoins().enqueue(object : Callback<CoinsData>{
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val data = response.body()!!
                apiCallback.onSuccess(data.data)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }


        })

    }

    fun getChartData(symbol : String , period : String , apiCallback: ApiCallback<Pair<List<ChartData.Data> , ChartData.Data?>>){

        var histoPeriod = ""
        var limit = 30
        var aggregate = 1

        when (period){

            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60 // تایم
                aggregate = 12 //تعداد نقطه
            }

            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }

            WEEK -> {
                histoPeriod = HISTO_DAY
                aggregate = 6
            }

            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }

            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }

            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }

            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
        }

        apiService.getChartData(histoPeriod , symbol , limit , aggregate).enqueue(object : Callback<ChartData> {

            override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {

                val dataFull = response.body()!!
                val data1 = dataFull.data
                val data2 = dataFull.data.maxByOrNull { it.close.toFloat() }
                val returningData = Pair(data1, data2)
                apiCallback.onSuccess(returningData)
            }

            override fun onFailure(call: Call<ChartData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })

    }

    interface ApiCallback<T>{
        fun onSuccess(data : T)
        fun onError(errorMessage: String)
    }

}