package ps.petugas.salam.wakatobi.ui.splashscreen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.Logger
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.model.DataLogin
import ps.petugas.salam.wakatobi.ui.mainscreen.VMainScreen
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView


class VSplashScreen : AppCompatActivity(), ISplashScreen.View {
    private val mPresenter = PSplashScreen()
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        if (SharedPref.pref.contains(SharedKey.id_user) && SharedPref.pref.contains(SharedKey.password)) {
            mPresenter.attachView(this)
            val items = HashMap<String, String>()
            items.put("action", "check-login")
            items.put("username", SharedPref.getString(SharedKey.username))
            items.put("password", SharedPref.getString(SharedKey.password))
            Logger.d(SharedPref.getString(SharedKey.username) + ":" + SharedPref.getString(SharedKey.password))
            mPresenter.getLogin(items)

        } else {
            Handler().postDelayed({
                val i = Intent(applicationContext, VMainScreen::class.java)
                startActivity(i)
                // close this activity
                finish()
            }, 1000)
        }
    }

    override fun onSuccess(response: DataLogin.Response) {
        SharedPref.saveBol(SharedKey.is_login, true)
        SharedPref.saveString(SharedKey.id_user, response.id_user.toString())
        SharedPref.saveString(SharedKey.id_instansi, response.id_instansi.toString())
        finish()
        startActivity(Intent(applicationContext, VMainScreen::class.java))
    }

    override fun onError(errorMessage: String, errorStatus: Int) {
        if (errorStatus == 401) {
            SharedPref.clearAll()
            SharedPref.saveString(SharedKey.firebase_ID, FirebaseInstanceId.getInstance().token.toString())
            val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
        CustomToastView.makeText(this, errorMessage, Toast.LENGTH_LONG)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
