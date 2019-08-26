package ps.petugas.salam.wakatobi.ui.detaillaporan

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/23/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PDetailReport : BasePresenter<IDetailReport.View>(), IDetailReport.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IDetailReport.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getDetailReport(idReport: String, idUser: String, lat: Double, long: Double) {
        composite.add(api.API_CLIENT.getDetailReport(idReport, idUser, lat, long)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true, 0) }
                .doOnComplete { mView()?.onDialogShow(false, 0) }
                .subscribe({ detailReport ->
                    if (detailReport.code() == 200) {
                        if (detailReport.body().diagnostic.status == 200) {
                            mView()?.onGetDetailReport(detailReport.body().response)
                        } else
                            mView()?.onMessage(detailReport.body().diagnostic.description!!)
                    } else {
                        mView()?.onMessage(detailReport.errorBody().toString())
                    }
                }))
    }

    override fun acceptReport(items: HashMap<String, String>) {
        composite.add(api.API_CLIENT.acceptReport(items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true, 0) }
                .doOnComplete { mView()?.onDialogShow(false, 0) }
                .subscribe({ accept ->
                    if (accept.code() == 200) {
                        if (accept.body().diagnostic.status == 200) {
                            mView()?.onDialogShow(false, 1)
                            mView()?.onMessage(accept.body().diagnostic.description!!)
                        } else
                            mView()?.onMessage(accept.body().diagnostic.description!!)
                    } else
                        mView()?.onMessage(accept.errorBody().string())
                }))
    }
}