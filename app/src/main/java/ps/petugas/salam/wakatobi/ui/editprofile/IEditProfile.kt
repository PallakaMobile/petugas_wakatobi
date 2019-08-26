package ps.petugas.salam.wakatobi.ui.editprofile

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataLogin

/**
 **********************************************
 * Created by ukie on 3/30/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IEditProfile {
    interface View : BaseMVP {
        fun setupView(response: DataLogin.Response)
        fun onShowDialog(isShow: Boolean)
        fun onSuccess()
        fun onShowMessage(message: String)

    }

    interface Presenter {
        fun updateProfile(items: HashMap<String, String>)
    }
}