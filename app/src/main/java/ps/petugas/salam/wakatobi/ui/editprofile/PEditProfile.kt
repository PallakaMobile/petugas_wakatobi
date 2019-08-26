package ps.petugas.salam.wakatobi.ui.editprofile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/30/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

class PEditProfile : BasePresenter<IEditProfile.View>(), IEditProfile.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IEditProfile.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }


    override fun updateProfile(items: HashMap<String, String>) {
        composite.add(api.API_CLIENT.updateProfileInfo(items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowDialog(true) }
                .doOnComplete { mView()?.onShowDialog(false) }
                .subscribe({ updateProfileInfo ->
                    if (updateProfileInfo.code() == 200) {
                        if (updateProfileInfo.body().diagnostic.status == 200) {
                            mView()?.onSuccess()
                        }
                        mView()?.onShowMessage(updateProfileInfo.body().diagnostic.description!!)
                    } else {
                        mView()?.onShowMessage(updateProfileInfo.errorBody().string())
                    }
                }))
    }
}