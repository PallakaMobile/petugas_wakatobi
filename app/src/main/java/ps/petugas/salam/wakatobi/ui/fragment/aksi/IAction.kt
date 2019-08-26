package ps.petugas.salam.wakatobi.ui.fragment.aksi

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataAction

/**
 **********************************************
 * Created by ukie on 3/26/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

interface IAction {
    interface View : BaseMVP {
        fun setupView()
        fun onShowDialog(isShow: Boolean)
        fun onGetActionReport(dataAction: DataAction)
        fun onShowError(errorMessage: String)
    }

    interface Presenter {
        fun getActionReport(page: Int, user: String)


    }
}
