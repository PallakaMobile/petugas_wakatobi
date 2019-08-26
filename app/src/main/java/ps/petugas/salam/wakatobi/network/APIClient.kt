package ps.petugas.salam.wakatobi.network

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ps.petugas.salam.wakatobi.model.*
import ps.salam.wakatobi.model.DataProfilePicture
import retrofit2.Response
import retrofit2.http.*


/**
 * ----------------------------------------------
 * Created by ukieTux on 2/28/17 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2017 | All Rights Reserved
 */

interface APIClient {
    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun setLogin(@FieldMap items: HashMap<String, String>): Observable<Response<DataLogin>>

    //forgot password
    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun forgotPassword(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>

    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=laporan")
    fun getReportNewest(@Query("page") page: Int, @Query("instansi") instansi: String): Observable<Response<DataReport>>

    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=laporan_terdekat")
    fun getReportNearby(@Query("page") page: Int, @Query("instansi") instansi: String, @Query("lat") lat: Double, @Query("long") long: Double): Observable<Response<DataReport>>

    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun sendFirebaseID(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>


    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=detail-laporan")
    fun getDetailReport(@Query("id") id_report: String, @Query("user") user: String, @Query("lat") lat: Double, @Query("long") long: Double): Observable<Response<DataDetailReport>>

    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun acceptReport(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>

    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=aksi")
    fun getActionReport(@Query("page") page: Int, @Query("petugas") user: String): Observable<Response<DataAction>>


    @Multipart
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun sendResponse(@PartMap items: HashMap<String, RequestBody>, @Part image: MultipartBody.Part): Observable<Response<DiagnosticOnly>>

    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=peringkat-list")
    fun getRank(@Query("user") user: String): Observable<Response<DataRankList>>

    @Multipart
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun updateProfilePicture(@PartMap items: HashMap<String, RequestBody>, @Part image: MultipartBody.Part): Observable<Response<DataProfilePicture>>

    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun updateProfileInfo(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>

    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun updatePassword(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>

    @FormUrlEncoded
    @POST("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f")
    fun updateEmail(@FieldMap items: HashMap<String, String>): Observable<Response<DiagnosticOnly>>


    @GET("api_skpd.php?token=de047cf89fb0c244f3379316f67ffc7241805e7f&get=history")
    fun getActionHistory(@Query("page") page: Int, @Query("petugas") user: String): Observable<Response<DataAction>>
}
