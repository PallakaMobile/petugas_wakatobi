package ps.petugas.salam.wakatobi.ui.splashscreen

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api
import retrofit2.Response
import java.util.HashMap

/**
 **********************************************
 * Created by ukie on 3/15/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PSplashScreen : BasePresenter<ISplashScreen.View>(), ISplashScreen.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: ISplashScreen.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getLogin(items: HashMap<String, String>) {
        composite.add(api.API_CLIENT.setLogin(items)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataLogin ->
                    if (dataLogin.code() == 200) {
                        if (dataLogin.body().diagnostic.status == 200) {
                            mView()?.onSuccess(dataLogin.body().response)
                        } else if (dataLogin.body().diagnostic.status == 401) {
                            mView()?.onError("Sesi kadaluarsa", 401)
                        } else mView()?.onError(dataLogin.body().diagnostic.description!!, 0)
                    } else
                        mView()?.onError(dataLogin.errorBody().toString(), 0)
                }))
    }

}