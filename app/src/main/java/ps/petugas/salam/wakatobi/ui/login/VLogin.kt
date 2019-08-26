package ps.petugas.salam.wakatobi.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.login_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataLogin
import ps.petugas.salam.wakatobi.model.DiagnosticOnly
import ps.petugas.salam.wakatobi.ui.mainscreen.VMainScreen
import ps.petugas.salam.wakatobi.ui.resetpass.VResetPassword
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView

class VLogin : BaseActivity(), ILogin.View {


    private lateinit var dialog: ProgressDialog
    private val mPresenter = PLogin()
    override fun getLayoutResource(): Int {
        return R.layout.login_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.login)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(false)
        setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)
        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        btn_login.setOnClickListener {
            if (tet_username.text.isEmpty() || tet_password.text.isEmpty()) {
                CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
            } else {
                val items = HashMap<String, String>()
                items.put("action", "check-login")
                items.put("username", tet_username.text.toString())
                items.put("password", tet_password.text.toString())
                mPresenter.getLoginData(items)
            }
        }

        btn_reset_pass.setOnClickListener {
            startActivity(Intent(applicationContext, VResetPassword::class.java))
        }

    }

    override fun onLoadingShow(isShow: Boolean, status: Int) {
        if (isShow)
            dialog.show()
        else {
            if (status == 1) {
                finish()
                startActivity(Intent(applicationContext, VMainScreen::class.java))
            }
            dialog.dismiss()
        }
    }

    override fun onGetLoginData(response: DataLogin.Response) {
        if (response.aktif == 0) {
            CustomToastView.makeText(this, getString(R.string.account_inactive), Toast.LENGTH_SHORT)
        } else {
            SharedPref.saveBol(SharedKey.is_login, true)
            SharedPref.saveString(SharedKey.id_user, response.id_user.toString())
            SharedPref.saveString(SharedKey.id_instansi,response.id_instansi.toString())
            SharedPref.saveString(SharedKey.password, tet_password.text.toString())
            SharedPref.saveString(SharedKey.username, tet_username.text.toString())
        }
    }

    override fun onError(errorMessage: String) {
        CustomToastView.makeText(this, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
    }
}
