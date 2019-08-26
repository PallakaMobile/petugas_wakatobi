package ps.petugas.salam.wakatobi.utils

import android.app.Application
import android.content.Context
import ps.petugas.salam.wakatobi.services.ConnectionReceiver

/**
 *----------------------------------------------
 * Created by ukieTux on 2/24/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //        MultiDex.install(this);
    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectivityReceiverListener) {
        ConnectionReceiver.connectivityReceiverListener = listener
    }

    companion object {
        var instance: AppController? = null
            private set
        var context: Context? = null
            private set
    }
}
