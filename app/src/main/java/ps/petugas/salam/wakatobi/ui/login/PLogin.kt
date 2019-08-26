package ps.petugas.salam.wakatobi.ui.login

import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref

/**
 **********************************************
 * Created by ukie on 3/20/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PLogin : BasePresenter<ILogin.View>(), ILogin.Presenter {


    private val composite = CompositeDisposable()
    override fun attachView(mvpView: ILogin.View) {
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
                .doOnSubscribe { mView()?.onLoadingShow(true, 0) }
                .map { loginData ->
                    if (loginData.code() == 200) {
                        if (loginData.body().diagnostic.status == 200) {
                            mView()?.onGetLoginData(loginData.body().response)
                            //continue to send firebase token
                            items.clear()
                            items.put("action", "update-token-user")
                            items.put("id", loginData.body().response.id_user.toString())
                            items.put("token-user", SharedPref.getString(SharedKey.firebase_ID))
                            return@map composite.add(api.API_CLIENT.sendFirebaseID(items)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io()).
                                    subscribe({ fireBase ->
                                        fireBase.code() != 200
                                        Logger.d("firebase" + fireBase.body().diagnostic.description)
                                    }))
                        } else {
                            mView()?.onError(loginData.body().diagnostic.description!!)
                            return@map false
                        }
                    } else {
                        mView()?.onError(loginData.errorBody().string())
                        return@map false
                    }
                }
                .subscribe({
                    var status = 0
                    if (it) {
                        status = 1
                    }
                    mView()?.onLoadingShow(false, status)
                }))
//
    }

}