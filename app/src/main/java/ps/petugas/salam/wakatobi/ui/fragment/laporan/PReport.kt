package ps.petugas.salam.wakatobi.ui.fragment.laporan

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api

/**
 **********************************************
 * Created by ukie on 3/21/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PReport : BasePresenter<IReport.View>(), IReport.Presenter {

    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IReport.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun getReportNewest(page: Int, instansi: String) {
        composite.add(api.API_CLIENT.getReportNewest(page, instansi)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowLoading(true) }
                .doOnComplete { mView()?.onShowLoading(false) }
                .subscribe({ report ->
                    if (report.code() == 200) {
                        if (report.body().diagnostic.status == 200) {
                            mView()?.onGetReport(report.body().response, report.body().pagination, 0)
                        } else {
                            mView()?.onError(report.body().diagnostic.description!!)
                        }
                    } else {
                        mView()?.onError(report.errorBody().string())
                    }
                }))
    }

    override fun getReportNearby(page: Int, instansi: String, lat: Double, long: Double) {
        composite.add(api.API_CLIENT.getReportNearby(page, instansi, lat, long)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowLoading(true) }
                .doOnComplete { mView()?.onShowLoading(false) }
                .subscribe({ report ->
                    if (report.code() == 200) {
                        if (report.body().diagnostic.status == 200) {
                            mView()?.onGetReport(report.body().response, report.body().pagination, 1)
                        } else {
                            mView()?.onError(report.body().diagnostic.description!!)
                        }
                    } else {
                        mView()?.onError(report.errorBody().string())
                    }
                }))
    }

}