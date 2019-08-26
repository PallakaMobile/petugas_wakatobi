package ps.petugas.salam.wakatobi.ui.profile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ps.petugas.petugas.salam.wakatobi.ui.profile.IProfile
import ps.petugas.salam.wakatobi.base.BasePresenter
import ps.petugas.salam.wakatobi.network.api
import java.io.File

/**
 **********************************************
 * Created by ukie on 3/15/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class PProfile : BasePresenter<IProfile.View>(), IProfile.Presenter {
    private val composite = CompositeDisposable()

    override fun attachView(mvpView: IProfile.View) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        composite.clear()
    }

    override fun updateProfilePicture(item: HashMap<String, RequestBody>, imageFile: File) {
        val requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val body = MultipartBody.Part.createFormData("gambar", imageFile.name, requestFile)
        composite.add(api.API_CLIENT.updateProfilePicture(item, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true) }
                .doOnComplete { mView()?.onDialogShow(false) }
                .subscribe({ response ->
                    if (response.code() == 200) {
                        if (response.body().diagnostic.status == 200) {
                            mView()?.onUpdateProfilePicture(response.body().response.gambar!!)
                        }
                        mView()?.onMessage(response.body().diagnostic.description!!)
                    } else
                        mView()?.onMessage(response.errorBody().string())
                }))
    }

    //
    override fun getProfile(item: HashMap<String, String>) {
        composite.add(api.API_CLIENT.setLogin(item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true) }
                .doOnComplete { mView()?.onDialogShow(false) }
                .subscribe({ profile ->
                    if (profile.code() == 200) {
                        if (profile.body().diagnostic.status == 200) {
                            mView()?.onGetProfile(profile.body().response)
                        } else {
                            mView()?.onMessage(profile.body().diagnostic.description!!)
                        }
                    } else {
                        mView()?.onMessage(profile.errorBody().string())
                    }
                }))
    }


    override fun updatePassword(item: HashMap<String, String>) {
        composite.add(api.API_CLIENT.updatePassword(item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true) }
                .doOnComplete { mView()?.onDialogShow(false) }
                .subscribe({ response ->
                    if (response.code() == 200) {
                        if (response.body().diagnostic.status == 200)
                            mView()?.onUpdatePassword()
                        mView()?.onMessage(response.body().diagnostic.description!!)
                    } else {
                        mView()?.onMessage(response.errorBody().string())
                    }
                }))
    }


    override fun updateEmail(item: HashMap<String, String>) {
        composite.add(api.API_CLIENT.updateEmail(item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true) }
                .doOnComplete { mView()?.onDialogShow(false) }
                .subscribe({ response ->
                    if (response.code() == 200) {
                        if (response.body().diagnostic.status == 200)
                            mView()?.onUpdateEmail()
                        mView()?.onMessage(response.body().diagnostic.description!!)
                    } else {
                        mView()?.onMessage(response.errorBody().string())
                    }
                }))
    }

    override fun getActionHistory(page: Int, user: String) {
        composite.add(api.API_CLIENT.getActionHistory(page, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView()?.onDialogShow(true) }
                .doOnComplete { mView()?.onDialogShow(false) }
                .subscribe({ action ->
                    if (action.code() == 200) {
                        if (action.body().diagnostic.status == 200)
                            mView()?.onGetActionHistory(action.body())
                        else
                            mView()?.onMessage(action.body().diagnostic.description!!)
                    } else {
                        mView()?.onMessage(action.errorBody().string())
                    }
                }))
    }
}