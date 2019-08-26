package ps.petugas.salam.wakatobi.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import ps.petugas.salam.wakatobi.R


/**
 *----------------------------------------------
 * Created by ukieTux on 2/23/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
abstract class BaseActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
//        try {
        toolbar = findViewById(R.id.toolbar) as Toolbar?
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                supportActionBar?.elevation = 0F
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        }

        myCodeHere()
//        } catch (exception: Exception) {
//            Logger.d(exception.toString())
//        }
    }

    protected abstract fun getLayoutResource(): Int

    protected abstract fun myCodeHere()

    protected abstract fun onDetachScreen()

    override fun onDestroy() {
        super.onDestroy()
        onDetachScreen()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

