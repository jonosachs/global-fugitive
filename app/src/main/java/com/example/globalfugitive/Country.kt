package com.example.globalfugitive

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_table")
data class Country(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val name: String,
    val iso2: String,
    val iso3: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val emoji: String,
//    val translations: String
)


//"id": 1,
//        "name": "Afghanistan",
//        "iso3": "AFG",
//        "iso2": "AF",
//        "numeric_code": "004",
//        "phone_code": "93",
//        "capital": "Kabul",
//        "currency": "AFN",
//        "currency_name": "Afghan afghani",
//        "currency_symbol": "؋",
//        "tld": ".af",
//        "native": "افغانستان",
//        "region": "Asia",
//        "region_id": "3",
//        "subregion": "Southern Asia",
//        "subregion_id": "14",
//        "nationality": "Afghan",
//        "timezones": [
//            {
//                "zoneName": "Asia\/Kabul",
//                "gmtOffset": 16200,
//                "gmtOffsetName": "UTC+04:30",
//                "abbreviation": "AFT",
//                "tzName": "Afghanistan Time"
//            }
//        ],
//        "translations": {
//            "kr": "아프가니스탄",
//            "pt-BR": "Afeganistão",
//            "pt": "Afeganistão",
//            "nl": "Afghanistan",
//            "hr": "Afganistan",
//            "fa": "افغانستان",
//            "de": "Afghanistan",
//            "es": "Afganistán",
//            "fr": "Afghanistan",
//            "ja": "アフガニスタン",
//            "it": "Afghanistan",
//            "cn": "阿富汗",
//            "tr": "Afganistan",
//            "ru": "Афганистан",
//            "uk": "Афганістан",
//            "pl": "Afganistan"
//        },
//        "latitude": "33.00000000",
//        "longitude": "65.00000000",
//        "emoji": "🇦🇫",
//        "emojiU": "U+1F1E6 U+1F1EB"