package ps.petugas.salam.wakatobi.base

/**
 *----------------------------------------------
 * Created by ukieTux on 2/27/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
open class BasePresenter<T : BaseMVP> : Presenter<T> {
    private var mvpView: T? = null

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        this.mvpView = null
    }

    fun mView(): T? {
        return this.mvpView
    }
}
