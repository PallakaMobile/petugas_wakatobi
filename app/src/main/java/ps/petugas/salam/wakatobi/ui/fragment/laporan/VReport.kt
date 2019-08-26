package ps.petugas.salam.wakatobi.ui.fragment.laporan

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.aksi_screen.*
import kotlinx.android.synthetic.main.report_nearby_screen.*
import kotlinx.android.synthetic.main.report_newest_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.adapter.AdapterReport
import ps.petugas.salam.wakatobi.base.BaseFragment
import ps.petugas.salam.wakatobi.model.DataDetailReport
import ps.petugas.salam.wakatobi.model.DataReport
import ps.petugas.salam.wakatobi.support.xrecyclerview.XRecyclerView
import ps.petugas.salam.wakatobi.ui.detaillaporan.VDetailReport
import ps.petugas.salam.wakatobi.utils.ScreenSizeGetter
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import java.io.UnsupportedEncodingException

/**
 **********************************************
 * Created by ukie on 3/19/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class VReport : BaseFragment(),
        IReport.View,
        OnRemoveItem,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val mPresenter = PReport()
    private lateinit var viewNewest: View
    private lateinit var viewNearby: View

    private var listResponse = ArrayList<DataDetailReport.Response>()
    private lateinit var dialog: ProgressDialog

    private var page = 1
    private var totalPage = 0
    private var instansi = ""

    private var isRefresh = false
    private var isLoadMore = false

    private var adapterReport: AdapterReport? = null

    private var isFirstLoad = false

    //google maps
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLastLocation: Location? = null
    private lateinit var bounds: LatLngBounds
    private lateinit var mMap: GoogleMap
    private lateinit var myLatLng: LatLng

    private lateinit var cu: CameraUpdate
    private var myLat: Double = 0.0
    private var myLng: Double = 0.0
    private lateinit var rxPermission: RxPermissions
    private lateinit var marker: Marker


    private var markerHashMap = HashMap<Int, Marker>()

    override fun getLayoutResource(): Int {
        return R.layout.aksi_screen
    }

    override fun myCodeHere() {
        VDetailReport.setOnRemoveItem(this)
        rxPermission = RxPermissions(activity)

        viewNearby = LayoutInflater.from(activity).inflate(R.layout.report_nearby_screen, null, false)
        viewNewest = LayoutInflater.from(activity).inflate(R.layout.report_newest_screen, null, false)

        //newest load first
        fl_container.removeAllViews()
        fl_container.addView(viewNewest)

        rg_report_status.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_newest_report -> {
                    listResponse.clear()
                    fl_container.removeAllViews()
                    fl_container.addView(viewNewest)
                    instansi = SharedPref.getString(SharedKey.id_instansi)
                    page = 1
                    mPresenter.getReportNewest(page, instansi)
                    reportNewest()
                }
                R.id.rb_nearly_report -> {
                    try {
                        listResponse.clear()
                        markerHashMap.clear()
                        mMap.clear()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    fl_container.removeAllViews()
                    fl_container.addView(viewNearby)
                    reportNearby()
                    if (mGoogleApiClient.isConnected)
                        mGoogleApiClient.reconnect()
                    mGoogleApiClient.connect()
                }
            }
        }

        setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)

        dialog = ProgressDialog(activity)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        //setup Map
        buildGoogleApiClient()

        //get ReportNewest Firstl
        instansi = SharedPref.getString(SharedKey.id_instansi)
        page = 1

        reportNewest()
        mPresenter.getReportNewest(page, instansi)

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    @Synchronized private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }


    private fun reportNewest() {
        //setup recycler view
        rv_report_newest.setHasFixedSize(true)
        rv_report_newest.setArrowImageView(R.drawable.ic_arrow_downward)
        val layoutManager = LinearLayoutManager(activity)
        rv_report_newest.layoutManager = layoutManager as RecyclerView.LayoutManager
        adapterReport = AdapterReport(listResponse, 0)
        rv_report_newest.adapter = adapterReport
        rv_report_newest.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {
                isRefresh = true
                page = 1
                mPresenter.getReportNewest(page, instansi)
                dialog.show()
            }

            override fun onLoadMore() {
                Logger.d("load more")
                page++
                if (page <= totalPage) {
                    isLoadMore = true
                    mPresenter.getReportNewest(page, instansi)
                } else {
                    rv_report_newest.loadMoreComplete()
                }
            }
        })
    }

    private fun reportNearby() {
        map.view!!.layoutParams.height = (ScreenSizeGetter.getHeight(activity.windowManager) * 0.35).toInt()
        //setup recycler view
        rv_report_nearby.setHasFixedSize(true)
        rv_report_nearby.setArrowImageView(R.drawable.ic_arrow_downward)
        rv_report_nearby.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager
        adapterReport = AdapterReport(listResponse, 1)
        rv_report_nearby.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        rv_report_nearby.adapter = adapterReport
        rv_report_nearby.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {
                isRefresh = true
                page = 1
                mPresenter.getReportNearby(page, instansi, myLat, myLng)
                dialog.show()
            }

            override fun onLoadMore() {
                Logger.d("load more")
                page++
                if (page <= totalPage) {
                    isLoadMore = true
                    mPresenter.getReportNearby(page, instansi, myLat, myLng)
                } else {
                    rv_report_nearby.loadMoreComplete()
                }
            }
        })
    }


    override fun onShowLoading(isShow: Boolean) {
        if (isShow)
            dialog.show()
        else
            dialog.dismiss()
    }

    override fun onError(errorMessage: String) {
        CustomToastView.makeText(activity, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun onGetReport(response: List<DataDetailReport.Response>, pagination: DataReport.Pagination, type: Int) {
        when (type) {
            0 -> {
                if (response.isEmpty() && page == 1) {
                    tv_no_data_newest.visibility = View.VISIBLE
                    rv_report_newest.visibility = View.GONE
                } else {
                    tv_no_data_newest.visibility = View.GONE
                    rv_report_newest.visibility = View.VISIBLE
                }

                if (isRefresh || page == 1) {
                    if (listResponse.isNotEmpty())
                        listResponse.clear()
                    isRefresh = false
                    rv_report_newest.refreshComplete()
                }

                if (isLoadMore) {
                    isLoadMore = false
                    rv_report_newest.loadMoreComplete()
                }
            }
            1 -> {
                if (response.isEmpty() && page == 1) {
                    tv_no_data_nearby.visibility = View.VISIBLE
                    rv_report_nearby.visibility = View.GONE
                } else {
                    tv_no_data_nearby.visibility = View.GONE
                    rv_report_nearby.visibility = View.VISIBLE
                }

                if (isRefresh || page == 1) {
                    if (listResponse.isNotEmpty())
                        listResponse.clear()
                    isRefresh = false
                    rv_report_nearby.refreshComplete()
                }

                if (isLoadMore) {
                    isLoadMore = false
                    rv_report_nearby.loadMoreComplete()
                }

                /**
                 * Display map onConnected
                 */

                val mapView = activity.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapView.getMapAsync { googleMap ->
                    try {
                        onSetupMap(googleMap, listResponse)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        page = pagination.active_page
        totalPage = pagination.total_page
        listResponse.addAll(response)
        adapterReport?.notifyDataSetChanged()
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            try {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        rxPermission.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe({ permission ->
                    if (permission) {
                        //noinspection MissingPermission
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                        if (mLastLocation != null) {
                            myLat = mLastLocation?.latitude!!
                            myLng = mLastLocation?.longitude!!
                            myLatLng = LatLng(myLat, myLng)

                            //getNearby report
                            mPresenter.getReportNearby(page, instansi, myLat, myLng)

                        } else {
                            val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                gpsPermission()
                            }
                            Toast.makeText(activity, getString(R.string.cant_find_location), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage(getString(R.string.access_location_not_allowed))
                        builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                        builder.setCancelable(false)
                        builder.show()
                    }
                })
    }

    private fun gpsPermission() {
        val builder = AlertDialog.Builder(activity)
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


    @Throws(UnsupportedEncodingException::class)
    fun onSetupMap(googleMap: GoogleMap, listResponseMap: ArrayList<DataDetailReport.Response>) {
        var tempListResponseMap = ArrayList<DataDetailReport.Response>()

        tempListResponseMap.clear()
        tempListResponseMap = listResponseMap

        mMap = googleMap
        mMap.clear()

        setMapType(SharedPref.getInt(SharedKey.mode_map))

        Logger.d("size temp : " + tempListResponseMap.size)

        rxPermission
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe({ permission ->
                    if (permission) {
                        mMap.isMyLocationEnabled = true

                        /*
                        Get GPS Permission
                         */
                        val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            gpsPermission()
                        }

                        val bld = LatLngBounds.Builder()
                        if (tempListResponseMap.isEmpty()) {
                            CustomToastView.makeText(activity, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                        } else {

                            bld.include(myLatLng)

                            for (item in tempListResponseMap.indices) {
                                myLatLng = LatLng(tempListResponseMap[item].lat_lapor, tempListResponseMap[item].long_lapor)
                                Logger.d("latlang $item :  " + tempListResponseMap[item].lat_lapor + tempListResponseMap[item].long_lapor)
                                bld.include(myLatLng)

                                marker = mMap.addMarker(MarkerOptions()
                                        .position(myLatLng)
                                        .snippet(tempListResponseMap[item].id + "%"
                                                + tempListResponseMap[item].nama + "%"
                                                + tempListResponseMap[item].judul + "%"
                                                + tempListResponseMap[item].area + "%"
                                                + item
                                        )
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

                                markerHashMap.put(item, marker)

                                bounds = bld.build()

                                mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                                    override fun getInfoWindow(marker: Marker): View? {
                                        return null
                                    }

                                    override fun getInfoContents(marker: Marker): View {
                                        @SuppressLint("InflateParams") val view = getLayoutInflater(arguments).inflate(R.layout.marker_info_screen, null)

                                        val snippet = marker.snippet
                                        val splitSnippet = snippet.split("%".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

                                        val title = view.findViewById(R.id.tv_title) as TextView
                                        val address = view.findViewById(R.id.tv_address) as TextView
                                        title.text = splitSnippet[1]
                                        address.text = splitSnippet[3]

                                        Logger.d("size temp : $tempListResponseMap.size")

                                        //do on Click move item to index 0
                                        Logger.d("item position :" + splitSnippet[4])
                                        Logger.d("item size before move :" + listResponse.size)
                                        listResponse.add(0, listResponse[splitSnippet[4].toInt()])
                                        Logger.d("item size before :" + listResponse.size)
                                        listResponse.removeAt(splitSnippet[4].toInt() + 1)
                                        Logger.d("item size after :" + listResponse.size)

                                        onSetupMap(googleMap, listResponse)

                                        adapterReport?.notifyDataSetChanged()
                                        rv_report_nearby.smoothScrollToPosition(0)

                                        mMap.setOnInfoWindowClickListener {
                                            val detailAsset = Intent(activity.applicationContext, VDetailReport::class.java)
                                            detailAsset.putExtra("id_report", splitSnippet[0])
                                            detailAsset.putExtra("position", item)
                                            startActivity(detailAsset)
                                        }

                                        return view
                                    }
                                })

                                if (!isFirstLoad)
                                    mMap.setOnMapLoadedCallback {
                                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 90)
                                        mMap.moveCamera(cu)
                                        mMap.animateCamera(cu)
                                        isFirstLoad = true
                                    }
                            }
                            mMap.uiSettings.isMapToolbarEnabled = false
                        }
                    }
                })
    }

    private fun setMapType(position: Int) {
        when (position) {
            0 -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            1 -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            2 -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            3 -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }


    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun position(position: Int) {
        if (markerHashMap.isNotEmpty()) {
            val marker = markerHashMap[position]
            marker?.remove()
            markerHashMap.remove(position)
        }
        listResponse.removeAt(position)
        adapterReport?.notifyDataSetChanged()
    }

    override fun detachScreen() {
        mPresenter.detachView()
    }
}