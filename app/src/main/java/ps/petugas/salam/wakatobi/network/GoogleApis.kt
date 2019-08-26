package ps.petugas.salam.wakatobi.network

import io.reactivex.Observable
import ps.petugas.salam.wakatobi.model.DataMapsApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 **********************************************
 * Created by ukie on 3/18/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface GoogleApis {

    //Google Maps
    @GET
    fun getDirection(@Url url: String): Observable<Response<DataMapsApi>>
}