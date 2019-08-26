package ps.petugas.salam.wakatobi.ui.ranking

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 4/8/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

class PRanking : BasePresenter<IRanking.View>(), IRanking.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IRanking.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getRanking(user: String) {
        composite.add(api.API_CLIENT.getRank(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowDialog(true) }
                .doOnComplete { mView()?.onShowDialog(false) }
                .subscribe { response ->
                    if (response.code() == 200) {
                        if (response.body().diagnostic.status == 200) {
                            mView()?.onGetRanking(response.body().stat, response.body().response)
                        } else
                            mView()?.onMessage(response.body().diagnostic.description!!)
                    } else
                        mView()?.onMessage(response.errorBody().toString())
                })
    }
}