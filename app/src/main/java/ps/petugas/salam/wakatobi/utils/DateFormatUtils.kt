package ps.petugas.salam.wakatobi.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *----------------------------------------------
 * Created by ukieTux on 3/9/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
object DateFormatUtils {
    private val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
    private val outputDate = SimpleDateFormat("dd/MM/yyyy | HH:mm 'WITA'", Locale("id", "ID"))
    private val outputDate1 = SimpleDateFormat("HH:mm 'WITA' , dd/MM/yyyy", Locale("id", "ID"))
    fun format(input: String, type: Int): String {
        when (type) {
            0 -> {
                val date = inputDate.parse(input)
                return outputDate.format(date)
            }
            1 -> {
                val date = inputDate.parse(input)
                return outputDate1.format(date)
            }
        }
        return ""
    }
}