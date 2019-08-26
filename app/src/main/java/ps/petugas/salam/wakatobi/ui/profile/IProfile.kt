package ps.petugas.petugas.salam.wakatobi.ui.profile

import okhttp3.RequestBody
import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataAction
import ps.petugas.salam.wakatobi.model.DataLogin
import java.io.File

/**
 **********************************************
 * Created by ukie on 3/15/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface IProfile {
    interface View : BaseMVP {
        fun setupView()
        fun onDialogShow(isShow: Boolean)
        fun onMessage(message: String)

        fun onGetProfile(response: DataLogin.Response)
        fun onUpdateProfilePicture(imageUrl: String)
        fun onUpdatePassword()
        fun onUpdateEmail()
        fun onGetActionHistory(dataAction: DataAction)

    }

    interface Presenter {
        fun getProfile(item: HashMap<String, String>)
        fun getActionHistory(page: Int, user: String)
        fun updatePassword(item: HashMap<String, String>)
        fun updateEmail(item: HashMap<String, String>)
        fun updateProfilePicture(item: HashMap<String, RequestBody>, imageFile: File)
    }
}