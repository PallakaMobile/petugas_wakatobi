package ps.petugas.salam.wakatobi.ui.location

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/18/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PLocation : BasePresenter<ILocation.View>(), ILocation.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: ILocation.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getDirection(url: String) {
        composite.add(api.GOOGLE_API.getDirection(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ direction ->
                    if (direction.code() == 200) {
                        if (direction.body().status.toLowerCase().equals("ok"))
                            mView()?.onGetDirection(direction.body())
                        else
                            mView()?.onSuccess(false, direction.message())
                    } else {
                        mView()?.onSuccess(false, direction.code().toString())
                    }
                }))
    }
}