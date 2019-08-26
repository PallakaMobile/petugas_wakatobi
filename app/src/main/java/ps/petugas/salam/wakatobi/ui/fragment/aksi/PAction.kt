package ps.petugas.salam.wakatobi.ui.fragment.aksi

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/26/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PAction : BasePresenter<IAction.View>(), IAction.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IAction.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getActionReport(page: Int, user: String) {
        composite.add(api.API_CLIENT.getActionReport(page, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowDialog(true) }
                .doOnComplete { mView()?.onShowDialog(false) }
                .subscribe({ action ->
                    if (action.code() == 200) {
                        if (action.body().diagnostic.status == 200)
                            mView()?.onGetActionReport(action.body())
                        else
                            mView()?.onShowError(action.body().diagnostic.description!!)
                    } else {
                        mView()?.onShowError(action.errorBody().string())
                    }
                }))
    }


}