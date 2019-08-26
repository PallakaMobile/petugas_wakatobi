package ps.petugas.salam.wakatobi.ui.mainscreen

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/20/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PMainScreen : BasePresenter<IMainScreen.View>(), IMainScreen.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IMainScreen.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getLoginData(items: HashMap<String, String>) {
        composite.add(api.API_CLIENT.setLogin(items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ loginData ->
                    if (loginData.code() == 200) {
                        if (loginData.body().diagnostic.status == 200) {
                            mView()?.onGetLoginData(loginData.body().response)
                        } else if (loginData.body().diagnostic.status == 401) {
                            mView()?.onError("Sesi kadaluarsa", 401)
                        } else mView()?.onError(loginData.body().diagnostic.description!!, 0)
                    } else {
                        mView()?.onError(loginData.errorBody().string(), 0)
                    }
                }))
    }

}