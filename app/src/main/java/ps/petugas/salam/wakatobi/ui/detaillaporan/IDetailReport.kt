package ps.petugas.salam.wakatobi.ui.detaillaporan

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataDetailReport
import ps.petugas.salam.wakatobi.model.DiagnosticOnly

/**
 **********************************************
 * Created by ukie on 3/23/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IDetailReport {
    interface View : BaseMVP {
        fun setupView()
        fun onDialogShow(isShow: Boolean, status: Int)
        fun onMessage(message: String)
        fun onGetDetailReport(response: DataDetailReport.Response)
    }

    interface Presenter {
        fun getDetailReport(idReport: String, idUser: String, lat: Double, long: Double)
        fun acceptReport(items: HashMap<String, String>)
    }
}