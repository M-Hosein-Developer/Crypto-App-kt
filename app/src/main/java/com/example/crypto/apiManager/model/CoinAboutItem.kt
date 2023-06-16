package com.example.crypto.apiManager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem(

    var coinWebsite: String? = "No Data",
    var coinGitHub: String? = "No Data",
    var coinTwitter: String? = "No Data",
    var coinDesc: String? = "No Data",
    var CoinReddit: String? = "No Data"

) : Parcelable
