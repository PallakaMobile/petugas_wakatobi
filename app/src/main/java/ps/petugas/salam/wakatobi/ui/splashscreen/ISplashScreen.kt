package ps.petugas.salam.wakatobi.ui.splashscreen

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataLogin
import java.util.HashMap

/**
 **********************************************
 * Created by ukie on 3/15/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

interface ISplashScreen {
    interface View : BaseMVP {
        fun onSuccess(response: DataLogin.Response)
        fun onError(errorMessage: String, errorStatus: Int)
    }

    interface Presenter {
        fun getLogin(items: HashMap<String, String>)
    }
}