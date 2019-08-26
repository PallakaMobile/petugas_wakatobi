package ps.petugas.salam.wakatobi.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import ps.petugas.salam.wakatobi.OkhttpClient
import ps.petugas.salam.wakatobi.network.APIClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 *----------------------------------------------
 * Created by ukieTux on 2/28/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */


object api {
    val BASE_URL: String = "http://api.salamwakatobi.com/"

    val okHttpClient: OkHttpClient? = OkhttpClient.instance.okHttpClient

    val swClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()!!


    val googleClient = Retrofit.Builder()
            .baseUrl("http://maps.googleapis.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()!!


    val API_CLIENT: APIClient = swClient.create(APIClient::class.java)
    val GOOGLE_API: GoogleApis = googleClient.create(GoogleApis::class.java)


}