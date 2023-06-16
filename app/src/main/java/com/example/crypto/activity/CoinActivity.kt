package com.example.crypto.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.crypto.R
import com.example.crypto.activity.coinActivity.ChartAdapter
import com.example.crypto.apiManager.ALL
import com.example.crypto.apiManager.ApiManager
import com.example.crypto.apiManager.BASE_URL_TWITTER
import com.example.crypto.apiManager.HOUR
import com.example.crypto.apiManager.HOURS24
import com.example.crypto.apiManager.MONTH
import com.example.crypto.apiManager.MONTH3
import com.example.crypto.apiManager.WEEK
import com.example.crypto.apiManager.YEAR
import com.example.crypto.apiManager.model.ChartData
import com.example.crypto.apiManager.model.CoinAboutData
import com.example.crypto.apiManager.model.CoinAboutItem
import com.example.crypto.apiManager.model.CoinsData
import com.example.crypto.databinding.ActivityCoinBinding


class CoinActivity : AppCompatActivity() {

    lateinit var binding: ActivityCoinBinding
    private lateinit var dataThisCoin : CoinsData.Data
    private lateinit var dataThisCoinAbout : CoinAboutItem
    val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //-------------------------------------------------------

        val fromIntent = intent.getBundleExtra("dataToSend")!!
        dataThisCoin = fromIntent.getParcelable<CoinsData.Data>("dataToSend1")!!

        if (fromIntent.getParcelable<CoinAboutItem>("dataToSend2") != null){

            dataThisCoinAbout = fromIntent.getParcelable<CoinAboutItem>("dataToSend2")!!

        }else{
            dataThisCoinAbout = CoinAboutItem()
        }


        toolbar()
        initUi()

    }

    private fun initUi() {


        initChartUi()
        initStatisticsUi()
        initAboutUi()

    }

    private fun initAboutUi() {

        binding.layoutAbout.txtxWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtTwitter.text = "@" + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGitHub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.CoinReddit
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc

        binding.layoutAbout.txtxWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!)
        }

        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinTwitter!!)
        }

        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinGitHub!!)
        }

        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.CoinReddit!!)
        }


    }

    private fun openWebsiteDataCoin(url : String){

        val intent = Intent(Intent.ACTION_VIEW , Uri.parse(url))
        startActivity(intent)

    }

    private fun initStatisticsUi() {

        binding.layoutStatistics.tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvTodaysHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvTodayLowAmount.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvAlgorithmAmount.text = dataThisCoin.coinInfo.algorithm

        val change2 = dataThisCoin.rAW.uSD.tOTALVOLUME24H
        val indexDot = change2.toString().indexOf('.')
        binding.layoutStatistics.tvTotalVolumeAmount.text = "$ " + dataThisCoin.rAW.uSD.tOTALVOLUME24H.toString().substring(0 , indexDot + 2)

        binding.layoutStatistics.tvMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.tvSupplyAmount.text = dataThisCoin.dISPLAY.uSD.sUPPLY

    }

    private fun initChartUi() {

        var period : String = HOUR
        requestAndShowChart(period)


        binding.layoutChart.radioGroup.setOnCheckedChangeListener { _ , checkedId ->

            when(checkedId){

                R.id.radio_12h -> {period = HOUR}

                R.id.radio_1d -> {period = HOURS24}

                R.id.radio_1w -> {period = WEEK}

                R.id.radio_1m -> {period = MONTH}

                R.id.radio_3m -> {period = MONTH3}

                R.id.radio_1y -> {period = YEAR}

                R.id.radio_all -> {period = ALL}

            }

            requestAndShowChart(period)
        }

        //Text View Chart - price , ....
        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE

        val change1 = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        val indexDot = change1.toString().indexOf('.')

        binding.layoutChart.txtChartChange2.text = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0 , indexDot + 2) + "%"
        binding.layoutChart.txtCartChange1.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        val change = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        if (change > 0){
            binding.layoutChart.txtChartPrice.setTextColor(ContextCompat.getColor(this , R.color.colorGain))
            binding.layoutChart.txtChartUpdown.setTextColor(ContextCompat.getColor(this , R.color.colorGain))
            binding.layoutChart.txtChartChange2.setTextColor(ContextCompat.getColor(this , R.color.colorGain))

            binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(this , R.color.colorGain)

            binding.layoutChart.txtChartUpdown.text = "▲"

        }else if(change < 0) {
            binding.layoutChart.txtChartPrice.setTextColor(ContextCompat.getColor(this , R.color.colorLoss))
            binding.layoutChart.txtChartUpdown.setTextColor(ContextCompat.getColor(this , R.color.colorLoss))
            binding.layoutChart.txtChartChange2.setTextColor(ContextCompat.getColor(this , R.color.colorLoss))

            binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(this , R.color.colorLoss)

            binding.layoutChart.txtChartUpdown.text = "▼"

        }else{
            binding.layoutChart.txtChartPrice.setTextColor(ContextCompat.getColor(this , R.color.secondaryTextColor))
            binding.layoutChart.txtChartUpdown.setTextColor(ContextCompat.getColor(this , R.color.secondaryTextColor))
            binding.layoutChart.txtChartChange2.setTextColor(ContextCompat.getColor(this , R.color.secondaryTextColor))

            binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(this , R.color.secondaryTextColor)

            binding.layoutChart.txtChartUpdown.text = "-"

        }

        binding.layoutChart.sparkViewMain.setScrubListener {


            if (it == null){
                //show full price
                binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE

            }else{
                //show point price
                binding.layoutChart.txtChartPrice.text = "$" + (it as ChartData.Data).close.toString()

            }

        }

    }

    private fun requestAndShowChart(period : String){

        apiManager.getChartData(dataThisCoin.coinInfo.name , period , object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
            override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {

                val chartAdapter = ChartAdapter(data.first , data.second?.open.toString())
                binding.layoutChart.sparkViewMain.adapter = chartAdapter

            }

            override fun onError(errorMessage: String) {

                Toast.makeText(this@CoinActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun toolbar() {
        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName
    }
}