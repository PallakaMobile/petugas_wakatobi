package ps.petugas.salam.wakatobi.model

/**
 *----------------------------------------------
 * Created by ukieTux on 3/8/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
class DataReport {
    lateinit var diagnostic: Diagnostic
    lateinit var pagination: Pagination
    lateinit var response: List<DataDetailReport.Response>

    class Diagnostic {
        var status: Int = 0
        var description: String? = null
    }

    class Pagination {
        var total_data: Int = 0
        var limit_page: Int = 0
        var active_page: Int = 0
        var total_page: Int = 0
    }
}