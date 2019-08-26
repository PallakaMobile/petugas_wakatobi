package ps.petugas.salam.wakatobi.ui.resetpass

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api
import ps.salam.wakatobi.ui.resetpass.IResetPassword

/**
 **********************************************
 * Created by ukie on 3/21/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PResetPassword : BasePresenter<IResetPassword.View>(), IResetPassword.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IResetPassword.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun onResetPassword(items: HashMap<String, String>) {
        composite.addAll(api.API_CLIENT.forgotPassword(items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onLoadingShow(true) }
                .doOnComplete { mView()?.onLoadingShow(false) }
                .subscribe({ reset ->
                    if (reset.code() == 200) {
                        if (reset.body().diagnostic.status == 200) {
                            mView()?.onSuccess(reset.body().diagnostic)
                        } else {
                            mView()?.onError(reset.body().diagnostic.description!!)
                        }
                    } else {
                        mView()?.onError(reset.errorBody().string())
                    }
                }))
    }
}