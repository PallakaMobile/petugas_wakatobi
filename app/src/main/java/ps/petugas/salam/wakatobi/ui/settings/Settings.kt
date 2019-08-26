package ps.petugas.salam.wakatobi.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlinx.android.synthetic.main.settings_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView

class Settings : BaseActivity() {

    override fun getLayoutResource(): Int {
        return R.layout.settings_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.settings_application)

        when (SharedPref.getInt(SharedKey.mode_map)) {
            0 -> rb_road_map.isChecked = true
            1 -> rb_satellite.isChecked = true
            2 -> rb_hybrid.isChecked = true
            3 -> rb_terrain.isChecked = true
        }

        ll_road_map.setOnClickListener {
            SharedPref.saveInt(SharedKey.mode_map, 0)
            rb_road_map.isChecked = true
            rb_satellite.isChecked = false
            rb_hybrid.isChecked = false
            rb_terrain.isChecked = false
            selectedMap(getString(R.string.mode_road_map))
        }
        ll_satellite.setOnClickListener {
            SharedPref.saveInt(SharedKey.mode_map, 1)
            rb_road_map.isChecked = false
            rb_satellite.isChecked = true
            rb_hybrid.isChecked = false
            rb_terrain.isChecked = false
            selectedMap(getString(R.string.mode_satellite))
        }
        ll_hybrid.setOnClickListener {
            SharedPref.saveInt(SharedKey.mode_map, 2)
            rb_road_map.isChecked = false
            rb_satellite.isChecked = false
            rb_hybrid.isChecked = true
            rb_terrain.isChecked = false
            selectedMap(getString(R.string.mode_hybrid))
        }
        ll_terrain.setOnClickListener {
            SharedPref.saveInt(SharedKey.mode_map, 0)
            rb_road_map.isChecked = false
            rb_satellite.isChecked = false
            rb_hybrid.isChecked = false
            rb_terrain.isChecked = true
            selectedMap(getString(R.string.mode_terrain))
        }

        btn_send_rating.setOnClickListener {
            val uri = Uri.parse("market://details?id=" + packageName)
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(myAppLinkToMarket)
            } catch (e: ActivityNotFoundException) {
                CustomToastView.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun selectedMap(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
    }

    override fun onDetachScreen() {

    }
}
