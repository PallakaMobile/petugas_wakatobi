package ps.petugas.salam.wakatobi.ui.finishreport

import okhttp3.RequestBody
import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.salam.wakatobi.utils.ProgressRequestBody
import java.io.File

/**
 **********************************************
 * Created by ukie on 3/27/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IFinishReport {
    interface View : BaseMVP {
        fun setupView()
        fun onShowDialog(isShow: Boolean, type: Int)
        fun onShowMessage(message: String)
    }

    interface Presenter {
        fun sendFinishReport(items: HashMap<String, RequestBody>, imageFile: File, uploadCallbacks: ProgressRequestBody.UploadCallbacks)
    }
}