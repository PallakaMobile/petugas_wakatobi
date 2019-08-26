package ps.petugas.salam.wakatobi.ui.location

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataMapsApi

/**
 **********************************************
 * Created by ukie on 3/18/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
interface ILocation {
    interface View : BaseMVP {
        fun setupView()
        fun onGetDirection(dataMapsApi: DataMapsApi)
        fun onSuccess(isSuccess: Boolean, message: String)
    }

    interface Presenter {
        fun getDirection(url: String)
    }
}