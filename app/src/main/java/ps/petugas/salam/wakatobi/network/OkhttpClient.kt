package ps.petugas.salam.wakatobi

import okhttp3.Cache
import okhttp3.Interceptor
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ps.petugas.salam.wakatobi.services.ConnectionReceiver
import ps.petugas.salam.wakatobi.utils.AppController
import java.io.File

/**
 * ----------------------------------------------
 * Created by ukieTux on 5/24/16 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2017 | All Rights Reserved
 */
internal class OkhttpClient
private constructor() {
    var okHttpClient: OkHttpClient? = null
        private set

    init {

        System.setProperty("http.keepAlive", "false")
        val builder = OkHttpClient.Builder()

        //Logging -> hapus saat release
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        builder.connectTimeout(60, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(logging)
        builder.retryOnConnectionFailure(true)

        okHttpClient = builder.build()
        okHttpClient = okHttpClient
    }


    companion object {
        private var ourInstance: OkhttpClient? = null


        val instance: OkhttpClient
            get() {

                if (ourInstance == null) {
                    ourInstance = OkhttpClient()
                }

                return ourInstance!!
            }

    }
}
