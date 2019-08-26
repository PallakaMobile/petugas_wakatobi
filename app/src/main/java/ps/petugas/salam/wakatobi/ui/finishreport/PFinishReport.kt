package ps.petugas.salam.wakatobi.ui.finishreport

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api
import ps.salam.wakatobi.utils.ProgressRequestBody
import java.io.File

/**
 **********************************************
 * Created by ukie on 3/27/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */

class PFinishReport : BasePresenter<IFinishReport.View>(), IFinishReport.Presenter {
    private val composite = CompositeDisposable()
    override fun attachView(mvpView: IFinishReport.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun sendFinishReport(items: HashMap<String, RequestBody>, imageFile: File, uploadCallbacks: ProgressRequestBody.UploadCallbacks) {
        val progressRequestBody: ProgressRequestBody = ProgressRequestBody(imageFile, uploadCallbacks)
        val body = MultipartBody.Part.createFormData("gambar", imageFile.name, progressRequestBody)
        composite.add(api.API_CLIENT.sendResponse(items, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onShowDialog(true, 0) }
                .doOnComplete { mView()?.onShowDialog(false, 0) }
                .subscribe({ response ->
                    if (response.code() == 200) {
                        if (response.body().diagnostic.status == 200) {
                            mView()?.onShowDialog(false, 200)
                            mView()?.onShowMessage("Poin Anda telah bertambah")
                        } else
                            mView()?.onShowMessage(response.errorBody().toString())
                    } else {
                        mView()?.onShowMessage(response.errorBody().toString())
                    }
                }))
    }
}