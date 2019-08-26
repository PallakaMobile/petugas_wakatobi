package ps.petugas.salam.wakatobi.ui.fragment.laporan

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataDetailReport
import ps.petugas.salam.wakatobi.model.DataReport

/**
 **********************************************
 * Created by ukie on 3/21/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IReport {
    interface View : BaseMVP {
        fun setupView()
        fun onShowLoading(isShow: Boolean)
        fun onError(errorMessage: String)
        fun onGetReport(response: List<DataDetailReport.Response>, pagination: DataReport.Pagination, type: Int)
    }

    interface Presenter {
        fun getReportNewest(page: Int, instansi: String)
        fun getReportNearby(page: Int, instansi: String, lat: Double, long: Double)
    }
}