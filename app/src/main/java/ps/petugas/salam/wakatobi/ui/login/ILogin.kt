package ps.petugas.salam.wakatobi.ui.login

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
interface ILogin {
    interface View : BaseMVP {
        fun setupView()
        fun onLoadingShow(isShow: Boolean,status:Int)
        fun onGetLoginData(response: DataLogin.Response)
        fun onError(errorMessage: String)
    }

    interface Presenter {
        fun getLoginData(items: HashMap<String, String>)
    }
}