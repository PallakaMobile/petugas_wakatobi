package ps.petugas.salam.wakatobi.ui.mainscreen

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.main_activity_screen.*
import kotlinx.android.synthetic.main.main_screen_layer_app_bar.*
import kotlinx.android.synthetic.main.main_screen_layer_nav_header.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.model.DataLogin
import ps.petugas.salam.wakatobi.ui.detaillaporan.VDetailReport
import ps.petugas.salam.wakatobi.ui.fragment.aksi.VAction
import ps.petugas.salam.wakatobi.ui.fragment.laporan.VReport
import ps.petugas.salam.wakatobi.ui.infoapp.InfoApp
import ps.petugas.salam.wakatobi.ui.login.VLogin
import ps.petugas.salam.wakatobi.ui.profile.VProfile
import ps.petugas.salam.wakatobi.ui.ranking.VRanking
import ps.petugas.salam.wakatobi.ui.settings.Settings
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import java.util.ArrayList

class VMainScreen : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        IMainScreen.View,
        OnSwitchTab {


    private val mPresenter = PMainScreen()
    private lateinit var profile_pic: String
    private lateinit var username: String
    private lateinit var instance: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_screen)

        //add listener on VDetailReport
        VDetailReport.setOnSwitchTab(this)

        //check login status
        if (!SharedPref.getBol(SharedKey.is_login)) {
            finish()
            startActivity(Intent(applicationContext, VLogin::class.java))
            Logger.d("belum login")
        } else
            setupView()


    }

    override fun setupView() {
        //setup nav drawer menu
        mPresenter.attachView(this)

        Logger.d("token Firebase : " + SharedPref.getString(SharedKey.firebase_ID))
        nav_view.inflateMenu(R.menu.menu_navigation)

        val toggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                onTransparent(true)
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View?) {
                onTransparent(false)
                super.onDrawerClosed(drawerView)
            }
        }

        iv_nav_drawer.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.END))
                drawer_layout.closeDrawer(GravityCompat.END)
            else
                drawer_layout.openDrawer(GravityCompat.END)
        }

        @Suppress("DEPRECATION")
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        nav_view.setNavigationItemSelectedListener(this)

        //setup tab
        setupTab()

        Logger.d("firebaseID : ${SharedPref.getString(SharedKey.firebase_ID)}")

        //if accept report from notif
        try {
            if (intent.extras.getBoolean("notif")) {
                val detailReport = Intent(applicationContext, VDetailReport::class.java)
                detailReport.putExtra("id_report", intent.extras.getString("id_report"))
//                detailReport.putExtra("notif", true)
                detailReport.putExtra("type", "report")
                startActivity(detailReport)
            }
        } catch (ignore: Exception) {
        }
    }

    override fun onError(errorMessage: String, errorCode: Int) {
        if (errorCode == 401) {
            SharedPref.clearAll()
            SharedPref.saveString(SharedKey.firebase_ID, FirebaseInstanceId.getInstance().token.toString())
            val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
        CustomToastView.makeText(this, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun onGetLoginData(response: DataLogin.Response) {
        Glide.with(this)
                .load(response.foto)
                .error(R.drawable.ic_default_profile)
                .into(iv_profile_newest)

        Logger.d("onResume")

        tv_nav_username.text = response.nama
        tv_nav_user_status.text = response.instansi
        tv_point.text = response.poin

        username = response.nama!!
        instance = response.instansi!!
        profile_pic = response.foto!!
    }

    private fun onTransparent(isTransparent: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            if (isTransparent) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this@VMainScreen, R.color.transparent)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this@VMainScreen, R.color.colorPrimary)
            }
        }
    }


    private fun setupTab() {

        //add to pager
        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(VReport(), getString(R.string.report))
        adapter.addFragment(VAction(), getString(R.string.action))
        vp_container.adapter = adapter
        tabs.setupWithViewPager(vp_container)
    }

    override fun switchToTab(position: Int) {
        tabs.getTabAt(position)?.select()
    }


    inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        val fragmentList: ArrayList<Fragment> = ArrayList()
        val titleList: ArrayList<String> = ArrayList()

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return titleList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }
    }

    override fun onResume() {
        super.onResume()
        val items = HashMap<String, String>()
        items.put("action", "check-login")
        items.put("username", SharedPref.getString(SharedKey.username))
        items.put("password", SharedPref.getString(SharedKey.password))
        mPresenter.getLoginData(items)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> startActivity(Intent(applicationContext, VProfile::class.java))
            R.id.nav_ranking -> {
                val rankIntent = Intent(applicationContext, VRanking::class.java)
                rankIntent.putExtra("username", username)
                rankIntent.putExtra("instance", instance)
                rankIntent.putExtra("profile_pic", profile_pic)
                startActivity(rankIntent)
            }
            R.id.nav_settings -> startActivity(Intent(applicationContext, Settings::class.java))
            R.id.nav_about -> startActivity(Intent(applicationContext, InfoApp::class.java))
        }
        drawer_layout.closeDrawer(GravityCompat.END)
        return true
    }
}
