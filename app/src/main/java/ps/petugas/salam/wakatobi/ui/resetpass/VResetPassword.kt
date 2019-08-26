package ps.petugas.salam.wakatobi.ui.resetpass

import android.app.ProgressDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.reset_password_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DiagnosticOnly
import ps.petugas.salam.wakatobi.widget.CustomToastView
import ps.salam.wakatobi.ui.resetpass.IResetPassword

class VResetPassword : BaseActivity(), IResetPassword.View {
    private val mPresenter = PResetPassword()
    private lateinit var dialog: ProgressDialog
    override fun getLayoutResource(): Int {
        return R.layout.reset_password_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.reset_password)
        setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        btn_send_reset.setOnClickListener {
            if (tet_email_reset.text.isEmpty()) {
                CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
            } else {
                val items=HashMap<String,String>()
                items.put("action","forget-password")
                items.put("email",tet_email_reset.text.toString())
                mPresenter.onResetPassword(items)
            }
        }
    }

    override fun onLoadingShow(isShow: Boolean) {
        if (isShow)
            dialog.show()
        else
            dialog.dismiss()
    }

    override fun onSuccess(diagnostic: DiagnosticOnly.Diagnostic) {
        CustomToastView.makeText(this, diagnostic.description, Toast.LENGTH_SHORT)
    }

    override fun onError(errorMessage: String) {
        CustomToastView.makeText(this, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
    }

}
