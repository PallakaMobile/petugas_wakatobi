package ps.petugas.salam.wakatobi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

import ps.petugas.salam.wakatobi.utils.AppController


/**
 * ----------------------------------------------
 * Created by ukieTux on 11/11/16 with ♥ .

 * @email : ukie.tux@gmail.com
 * *
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2016 | All Rights Reserved
 */

class ConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnected)
        }
    }


    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {

        var connectivityReceiverListener: ConnectivityReceiverListener? = null

        val isConnected: Boolean
            get() {
                val cm: ConnectivityManager = AppController.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo = cm.activeNetworkInfo
                return activeNetwork.isConnectedOrConnecting
            }
    }
}
