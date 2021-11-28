package com.example.newsapp

import android.util.Log
import com.google.android.gms.internal.ads.zzaf.d
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.logging.Logger

class RemoteConfiFirebase {


    object RemoteConfigUtils {

        private const val TAG = "RemoteConfigUtils"

        private const val adActivity = "adActivity"

        private val DEFAULTS: HashMap<String, Boolean> =
            hashMapOf(
                adActivity to false,
            )

        private lateinit var remoteConfig: FirebaseRemoteConfig

        fun init() {
            remoteConfig = getFirebaseRemoteConfig()
        }

        private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {

            val remoteConfig = Firebase.remoteConfig

            val configSettings = remoteConfigSettings {
                if (BuildConfig.DEBUG) {
                    minimumFetchIntervalInSeconds = 0 // Kept 0 for quick debug
                } else {
                    minimumFetchIntervalInSeconds = 60 * 60 // Change this based on your requirement
                }
            }

            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(DEFAULTS as Map<String, Any>)

            remoteConfig.fetchAndActivate().addOnCompleteListener {
                Log.d(TAG, "addOnCompleteListener")

            }

            return remoteConfig
        }

        fun getAdStatus(): Boolean = remoteConfig.getBoolean(adActivity)


    }
}