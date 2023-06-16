package com.example.crypto.activity.marketActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crypto.activity.CoinActivity
import com.example.crypto.apiManager.ApiManager
import com.example.crypto.apiManager.model.CoinAboutData
import com.example.crypto.apiManager.model.CoinAboutItem
import com.example.crypto.apiManager.model.CoinsData
import com.example.crypto.databinding.ActivityMarketBinding
import com.google.gson.Gson
import java.util.ArrayList

class MarketActivity : AppCompatActivity() , MarketAdapter.RecyclerCallback{

    lateinit var binding: ActivityMarketBinding
    val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap: MutableMap<String , CoinAboutItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar()
        initUi()
        buttonMore()
        getAboutDataFromAssets()

        binding.swipeRefreshMain.setOnRefreshListener {

            initUi()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            } , 1500)

        }

    }

    private fun buttonMore() {
        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }
    }

    private fun initUi() {

        getNewsFromApi()
        getTopCoinsFromApi()

    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets.open("currencyinfo.json")
            .bufferedReader().use { it.readText() }

        aboutDataMap = mutableMapOf<String , CoinAboutItem>()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString , CoinAboutData::class.java)
        dataAboutAll.forEach {

            aboutDataMap[it.currencyName] = CoinAboutItem(

                it.info.web ,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit

            )

        }



    }

    private fun getNewsFromApi() {

        val test = apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
                override fun onSuccess(data: ArrayList<Pair<String, String>>) {

                    dataNews = data

                    refreshNews()
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@MarketActivity, "$errorMessage", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun refreshNews() {

        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = dataNews[randomAccess].first

        binding.layoutNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }

        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews()
        }

    }

    private fun getTopCoinsFromApi() {

        apiManager.getCoinsList(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }


        })

    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {

        val marketAdapter = MarketAdapter(ArrayList(data) , this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)

    }

    private fun toolbar() {
        binding.layoutToolbar.toolbar.title = "Market"
    }

    override fun onCoinItemCLicked(data: CoinsData.Data) {
        val intent = Intent(this , CoinActivity::class.java)

        val bundle = Bundle()
        bundle.putParcelable("dataToSend1" , data)
        bundle.putParcelable("dataToSend2" , aboutDataMap[data.coinInfo.name])

        intent.putExtra("dataToSend" , bundle)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }
}