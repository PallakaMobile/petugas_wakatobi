package ps.petugas.salam.wakatobi.model

/**
 **********************************************
 * Created by ukie on 3/26/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class DataAction {
    lateinit var diagnostic: Diagnostic
    lateinit var pagination: Pagination
    lateinit var response: List<Response>

    class Diagnostic {
        val status: Int = 0
        val description: String? = null
    }

    class Pagination {
        val total_data: Int = 0
        val limit_page: Int = 0
        val active_page: Int = 0
        val total_page: Int = 0
    }

    class Response {
        val id: String? = null
        val nama: String? = null
        val foto: String? = null
        val gambar: String? = null
        val judul: String? = null
        val pesan: String? = null
        val waktu_submit: String? = null
        val waktu_verifikasi: String? = null
        val waktu_proses: String? = null
        val waktu_selesai: String? = null
        val area: String? = null
    }
}