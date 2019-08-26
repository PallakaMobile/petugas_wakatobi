package ps.petugas.salam.wakatobi.ui.mainscreen

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataLogin

/**
 **********************************************
 * Created by ukie on 3/20/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IMainScreen {
    interface View : BaseMVP {
        fun setupView()
        fun onError(errorMessage: String, errorCode: Int)
        fun onGetLoginData(response: DataLogin.Response)
    }

    interface Presenter {
        fun getLoginData(items: HashMap<String, String>)
    }
}