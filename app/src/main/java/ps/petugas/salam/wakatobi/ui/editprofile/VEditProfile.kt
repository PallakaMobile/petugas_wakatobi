package ps.petugas.salam.wakatobi.ui.editprofile

import android.app.ProgressDialog
import android.text.TextUtils
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.edit_profile_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataLogin
import ps.petugas.salam.wakatobi.utils.RxHelperEditText
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class VEditProfile : BaseActivity(), IEditProfile.View {
    private val mPresenter = PEditProfile()
    private val composite = CompositeDisposable()

    private lateinit var dialog: ProgressDialog


    private var isValid = false

    override fun getLayoutResource(): Int {
        return R.layout.edit_profile_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.edit_profile)
        val response: DataLogin.Response = intent.extras.getParcelable("response")

        //form validation
        formValidation()

        setupView(response)
    }

    override fun setupView(response: DataLogin.Response) {
        mPresenter.attachView(this)

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        tet_full_name.setText(response.nama)
        tet_phone.setText(response.telp)
        tet_address.setText(response.alamat)
        tet_position_name.setText(response.jabatan)


        btn_update_profile.setOnClickListener {
            if (isValid) {
                val items: HashMap<String, String> = HashMap()
                items.put("action", "update-profile-info")
                items.put("id", SharedPref.getString(SharedKey.id_user))
                items.put("nama", tet_full_name.text.toString())
                items.put("telp", tet_phone.text.toString())
                items.put("jabatan", tet_position_name.text.toString())
                items.put("alamat", tet_address.text.toString())
                mPresenter.updateProfile(items)
            } else {
                CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onShowDialog(isShow: Boolean) {
        if (isShow)
            dialog.show()
        else dialog.dismiss()
    }

    override fun onSuccess() {
        finish()
    }

    override fun onShowMessage(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
    }

    private fun formValidation() {
        composite.add(checkName().subscribe({ isValid ->
            if (!isValid) {
                tet_full_name.error = getString(R.string.name_not_valid)
                tet_full_name.requestFocus()
            } else
                tet_full_name.error = null

        }))

        composite.add(checkPhone().subscribe({ isValid ->
            if (!isValid) {
                tet_phone.error = getString(R.string.phone_not_valid)
                tet_phone.requestFocus()
            } else tet_phone.error = null
        }))

        composite.add(checkPosition().subscribe({ isValid ->
            if (!isValid) {
                tet_position_name.error = getString(R.string.position_not_valid)
                tet_position_name.requestFocus()
            } else tet_position_name.error = null
        }))

        composite.add(checkAddress().subscribe({ isValid ->
            if (!isValid) {
                tet_address.error = getString(R.string.address_not_valid)
                tet_address.requestFocus()
            } else tet_address.error = null
        }))

        composite.add(checkAddress().subscribe({ isValid ->
            if (!isValid) {
                tet_address.error = getString(R.string.address_not_valid)
                tet_address.requestFocus()
            } else tet_address.error = null
        }))

        composite.add(checkForm().subscribe({ isValid -> this.isValid = isValid }))
    }

    private fun checkName(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_full_name)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ name: String -> !TextUtils.isEmpty(name) && authName(name) })
    }

    private fun checkPhone(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_phone)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ phone: String -> !TextUtils.isEmpty(phone) && tet_phone.length() > 10 })
    }

    private fun checkPosition(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_position_name)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ job: String -> !TextUtils.isEmpty(job) && tet_position_name.length() > 1 })
    }

    private fun checkAddress(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_address)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ address: String -> !TextUtils.isEmpty(address) && tet_address.length() > 5 })
    }

    private fun checkForm(): Observable<Boolean> {
        return Observable.combineLatest<Boolean, Boolean, Boolean, Boolean, Boolean>((checkName()),
                checkAddress(), checkPosition(), checkPhone(),
                (Function4 { t1, t2, t3, t4 -> t1 && t2 && t3 && t4 }))

    }

    private fun authName(nama: String): Boolean {
        val p = Pattern.compile("^[A-Za-z\\s]+[.]?[A-Za-z\\s]+$")
        val m = p.matcher(nama)
        return m.matches()
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
        composite.clear()
    }

}
