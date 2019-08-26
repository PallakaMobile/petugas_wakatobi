package ps.petugas.salam.wakatobi.ui.ranking

import ps.petugas.salam.wakatobi.base.BaseMVP
import ps.petugas.salam.wakatobi.model.DataRankList

/**
 **********************************************
 * Created by ukie on 4/8/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

interface IRanking {
    interface View : BaseMVP {
        fun setupView()
        fun onGetRanking(stat: DataRankList.Stat, response: List<DataRankList.Response>)
        fun onShowDialog(showDialog:Boolean)
        fun onMessage(message:String)
    }

    interface Presenter{
        fun getRanking(user:String)
    }
}