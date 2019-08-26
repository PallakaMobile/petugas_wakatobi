package ps.petugas.salam.wakatobi.model

/**
 *----------------------------------------------
 * Created by ukieTux on 3/2/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
class DiagnosticOnly {
    lateinit var diagnostic: Diagnostic

    class Diagnostic {
        var description: String? = null
        var status: Int? = 0
    }
}