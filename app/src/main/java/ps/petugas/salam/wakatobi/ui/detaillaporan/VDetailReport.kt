package ps.petugas.salam.wakatobi.ui.detaillaporan

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.detail_laporan_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataDetailReport
import ps.petugas.salam.wakatobi.ui.finishreport.VFinishReport
import ps.petugas.salam.wakatobi.ui.fragment.laporan.OnRemoveItem
import ps.petugas.salam.wakatobi.ui.location.VLocation
import ps.petugas.salam.wakatobi.ui.mainscreen.OnSwitchTab
import ps.petugas.salam.wakatobi.utils.DateFormatUtils
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import ps.salam.wakatobi.ui.DialogImageZoom
import java.util.*

class VDetailReport : BaseActivity(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        IDetailReport.View {

    private val mPresenter = PDetailReport()
    private lateinit var dialog: ProgressDialog

    private lateinit var rxPermissions: RxPermissions

    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLastLocation: Location? = null
    private var mMap: GoogleMap? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    companion object {
        lateinit var switchTab: OnSwitchTab
        lateinit var removeItem: OnRemoveItem
        fun setOnRemoveItem(onRemoveItem: OnRemoveItem) {
            removeItem = onRemoveItem
        }

        fun setOnSwitchTab(onSwitchTab: OnSwitchTab) {
            switchTab = onSwitchTab
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.detail_laporan_screen
    }

    override fun myCodeHere() {
        when (intent.extras.getString("type")) {
            "report" -> {
                tv_message_action.text = getString(R.string.accept_report_n)
                title = getString(R.string.detail_report)
            }
            "action" -> {
                tv_message_action.text = getString(R.string.finish_report_n)
                title = getString(R.string.detail_action)
            }
            "history" -> {
                title = getString(R.string.detail_history)
                ll_3_button.visibility = View.GONE
                ll_accept_report.visibility = View.GONE
                ll_poin_history.visibility = View.VISIBLE
            }
        }

        Logger.d("type : " + intent.extras.getString("type"))
        setupView()
    }


    override fun setupView() {
        //connect to map
        buildGoogleApiClient()
        mGoogleApiClient.connect()

        mPresenter.attachView(this)
        rxPermissions = RxPermissions(this)
        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)
    }


    @Synchronized private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onGetDetailReport(response: DataDetailReport.Response) {
        Glide.with(this)
                .load(response.foto)
                .error(R.drawable.ic_default_profile)
                .crossFade()
                .into(iv_profile_newest)

        tv_username_newest.text = response.nama
        tv_title_report_newest.text = response.judul
        tv_date_report_newest.text = DateFormatUtils.format(response.waktu_submit!!, 0)

        Glide.with(this)
                .load(response.gambar)
                .crossFade()
                .thumbnail(0.5F)
                .into(object : SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>) {
                        iv_image_report_newest.setImageDrawable(resource)
                        progressBar.visibility = View.GONE
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        progressBar.visibility = View.GONE
                        Logger.d(e.toString())

                    }
                })

        iv_image_report_newest.setOnClickListener {
            val args = Bundle()
            args.putString("image_url", response.gambar)
            args.putString("image_title", response.judul)
            val dialogZoom = DialogImageZoom()
            dialogZoom.arguments = args
            dialogZoom.show(supportFragmentManager, "dialog image")
        }

        tv_area.text = response.area
        tv_desc_report.text = response.pesan
        tv_kategori_value.text = String.format(getString(R.string.titik_dua_format), response.kategori)

        when (intent.extras.getString("type")) {
            "report" -> {
                tv_verification_date.text = String.format(getString(R.string.was_verified_by_admin), DateFormatUtils.format(response.waktu_belum!!, 1))
                if (response.status_laporan != 1) {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setCancelable(false)
                    alertDialog.setMessage(getString(R.string.report_already_accept))
                    alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        finish()
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
            }
            "action" -> {
                tv_verification_date.text = String.format(getString(R.string.was_response), DateFormatUtils.format(response.waktu_proses!!, 1))
            }
            "history" -> {
                tv_verification_date.text = String.format(getString(R.string.was_finish), DateFormatUtils.format(response.waktu_selesai!!, 1))
                @Suppress("DEPRECATION")
                tv_total_point.text = Html.fromHtml(getString(R.string.point_get_dot) + "<b> " + response.poin + "</b>")
            }
        }


        var duration = ""
        Logger.d("durasi", response.durasi)
        try {
            duration = response.durasi!!.replace("jam", "J")
            duration = duration.replace("menit", "M")
        } catch (e: Exception) {
        }
        tv_duration.text = duration

        val distance: String?
        if (response.jarak == 0.0)
            distance = "< 1"
        else
            distance = response.jarak.toString()

        tv_distance.text = String.format(getString(R.string.format_km), distance)


        //click listener
        tv_call_reporter.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + response.telp)
            startActivity(callIntent)
        }
        tv_duration.setOnClickListener {
            showLocation(response)
        }
        tv_distance.setOnClickListener {
            showLocation(response)
        }
        tv_see_location.setOnClickListener {
            showLocation(response)
        }

        ll_accept_report.setOnClickListener {
            if (intent.extras.getString("type") == "report") {
                val items = HashMap<String, String>()
                items.put("action", "accept-report")
                items.put("petugas", SharedPref.getString(SharedKey.id_user))
                items.put("laporan", intent.getStringExtra("id_report"))
                mPresenter.acceptReport(items)
            } else if (intent.extras.getString("type") == "action") {
                val finishReport = Intent(applicationContext, VFinishReport::class.java)
                finishReport.putExtra("id_report", intent.getStringExtra("id_report"))
                startActivity(finishReport)
            }
        }
    }

    private fun showLocation(response: DataDetailReport.Response) {
        val choose = arrayOf<CharSequence>(getString(R.string.app_name), getString(R.string.google_maps))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.open_with))
        builder.setItems(choose) { _, i ->
            when (i) {
                0 -> {
                    val location = Intent(applicationContext, VLocation::class.java)
                    location.putExtra("latitude", response.latitude.toString())
                    location.putExtra("longitude", response.longitude.toString())
                    location.putExtra("name_report", response.judul)
                    location.putExtra("address", response.area)
                    startActivity(location)
                }
                1 -> openGoogleMaps(response.latitude.toString(), response.longitude.toString())
            }
        }
        builder.show()
    }

    override fun onDialogShow(isShow: Boolean, status: Int) {
        if (isShow)
            dialog.show()
        else {
            if (status == 1) {
                removeItem.position(intent.extras.getInt("position"))
                finish()
//                if (intent.extras.getBoolean("notif")) {
//                    val mainActivity = Intent(applicationContext, VMainScreen::class.java)
//                    mainActivity.putExtra("notif", true)
//                    startActivity(Intent(mainActivity))
//                }
                switchTab.switchToTab(1)
            }
            dialog.dismiss()
        }
    }

    override fun onMessage(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_LONG)
    }

    private fun openGoogleMaps(lat: String, lng: String) {
        // Check whether Google App is installed in user device
        if (isGoogleMapsInstalled()) {
            // If installed, send latitude and longitude value to the app
            val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        } else {
            // If not installed, display dialog to ask user to install Google Maps app
            AlertDialog.Builder(this)
                    .setMessage(getString(R.string.google_maps_installation))
                    .setPositiveButton(getString(R.string.install), { _, _ ->
                        try {
                            // Open Google Maps app page in Google Play app
                            startActivity(Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.google.android.apps.maps")
                            ))
                        } catch (ANF: android.content.ActivityNotFoundException) {
                            startActivity(Intent(
                                    // Open Google Maps app page in Google Play web
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                            ))
                        }
                    })
                    .show()
        }
    }

    // Method to check whether Google Maps app is installed in user device
    private fun isGoogleMapsInstalled(): Boolean {
        try {
            packageManager.getApplicationInfo("com.google.android.apps.maps", 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (dialog.isShowing)
            dialog.dismiss()
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe({ permission ->
                    if (permission.granted) {
                        try {
                            mMap?.isMyLocationEnabled = true
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                            updateLocation()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (permission.shouldShowRequestPermissionRationale) {
                            val builder = android.support.v7.app.AlertDialog.Builder(this)
                            builder.setMessage(getString(R.string.access_location_not_allowed))
                            builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                            builder.setCancelable(false)
                            builder.show()
                        } else {
                            promptSettings()
                        }
                    }
                })
    }

    private fun updateLocation() {
        if (mLastLocation != null) {
            mLatitude = mLastLocation?.latitude!!
            mLongitude = mLastLocation?.longitude!!
            Logger.d(mLatitude.toString() + " : " + mLongitude.toString())

            mPresenter.getDetailReport(intent.getStringExtra("id_report"), SharedPref.getString(SharedKey.id_user), mLatitude, mLongitude)
        } else {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsPermission()
            }
            CustomToastView.makeText(this, getString(R.string.cant_find_location), Toast.LENGTH_SHORT)
        }
    }

    private fun promptSettings() {
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        builder.setMessage(R.string.access_location_not_allowed)
        builder.setPositiveButton(getString(R.string.settings)) { dialog, _ ->
            dialog.dismiss()
            goToSettings()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun gpsPermission() {
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.gps_disable)
        builder.setMessage(R.string.ask_enable_gps)
        builder.setPositiveButton(getString(R.string.settings)) { dialog, _ ->
            val intent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setCancelable(false)
        builder.show()
    }

    private fun goToSettings() {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.packageName))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(myAppSettings)
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        updateLocation()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Logger.d(p0.errorMessage)
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
    }
}
