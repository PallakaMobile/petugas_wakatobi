package ps.salam.wakatobi.model

import ps.petugas.salam.wakatobi.model.DiagnosticOnly

/**
 **********************************************
 * Created by ukie on 3/31/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class DataProfilePicture {
    lateinit var diagnostic: DiagnosticOnly.Diagnostic
    lateinit var response: Response

    class Response {
        val gambar: String? = null
    }
}