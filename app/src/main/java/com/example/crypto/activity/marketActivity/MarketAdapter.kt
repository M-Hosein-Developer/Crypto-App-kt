package com.example.crypto.activity.marketActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crypto.R
import com.example.crypto.apiManager.BASE_URL_IMAGE
import com.example.crypto.apiManager.model.CoinsData
import com.example.crypto.databinding.ItemresyclerMarketBinding

class MarketAdapter(
    private val data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    lateinit var binding: ItemresyclerMarketBinding

    inner class MarketViewHolder(itemViewHolder: View) : RecyclerView.ViewHolder(itemViewHolder) {


        fun bindViews(data: CoinsData.Data) {

            binding.txtCoinName.text = data.coinInfo.fullName
            binding.txtPrice.text = data.dISPLAY.uSD.pRICE.toString()

            val marketCap = data.rAW.uSD.mKTCAP / 1000000000
            val indexDot = marketCap.toString().indexOf('.')
            binding.txtMarketCap.text = "$" + marketCap.toString().substring(0 , indexDot + 2) + " B"

            val change = data.rAW.uSD.cHANGEPCT24HOUR
            if (change > 0){
                binding.txtChange.setTextColor(ContextCompat.getColor(binding.root.context , R.color.colorGain))
                binding.txtChange.text = data.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0 , 5) + "%"
            }else if(change < 0) {
                binding.txtChange.setTextColor(ContextCompat.getColor(binding.root.context , R.color.colorLoss))
                binding.txtChange.text = data.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0 ,6) + "%"
            }else{
                binding.txtChange.setTextColor(ContextCompat.getColor(binding.root.context , R.color.secondaryTextColor))
                binding.txtChange.text = data.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0 , 1) + "%"
            }




            Glide.with(itemView)
                .load(BASE_URL_IMAGE + data.coinInfo.imageUrl)
                .into(binding.imageItem)

            itemView.setOnClickListener {

                recyclerCallback.onCoinItemCLicked(data)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemresyclerMarketBinding.inflate(inflater, parent, false)
        return MarketViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    interface RecyclerCallback {

        fun onCoinItemCLicked(data: CoinsData.Data)

    }

}