package ps.petugas.salam.wakatobi.model

/**
 **********************************************
 * Created by ukie on 4/8/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

class DataRankList {
    lateinit var diagnostic: DiagnosticOnly.Diagnostic
    lateinit var response: List<Response>
    lateinit var stat: Stat

    class Response {
        val nama: String? = null
        val instansi: String? = null
        val poin: String? = null
        val rank: String? = null
        val aksi: String? = null
        val flag: Int = 0
    }

    class Stat {
        val peringkat: String? = null
        val poin: String? = null
        val laporan_selesai: String? = null
        val tot_petugas: String? = null
    }
}