package ps.petugas.salam.wakatobi.model

/**
 **********************************************
 * Created by ukie on 3/16/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class DataDetailReport {
    lateinit var diagnostic: Diagnostic
    lateinit var response: Response

    class Diagnostic {
        val status: Int = 0
        val description: String? = null
    }

    class Response {
        val id: String? = null
        val nama: String? = null
        val foto: String? = null
        val gambar: String? = null
        val gambar_thumbnail: String? = null
        val gambar_selesai: String? = null
        val judul: String? = null
        val kategori: String? = null
        val pesan: String? = null
        val pesan_selesai: String? = null
        val status_laporan: Int = 0
        val waktu_submit: String? = null
        val waktu_proses: String? = null
        val waktu_belum: String? = null
        val waktu_selesai: String? = null
        val waktu_verifikasi: String? = null
        val area: String? = null
        val latitude: Double = 0.0
        val longitude: Double = 0.0
        val like: String? = null
        val komentar: String? = null
        val disukai: Int = 0
        val dibagikan: String? = null
        val dilihat: String? = null
        val tanggapan: Int = 0
        val instansi: String? = null
        val petugas: String? = null
        val foto_petugas: String? = null


        //for nearby report
        val lat_lapor: Double = 0.0
        val long_lapor: Double = 0.0
        val jarak: Double = 0.0
        val durasi: String? = null
        val telp: String? = null

        val poin: String? = null
    }
}