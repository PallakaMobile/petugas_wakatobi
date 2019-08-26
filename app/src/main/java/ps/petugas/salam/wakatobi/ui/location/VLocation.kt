package ps.petugas.salam.wakatobi.ui.location

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
import android.support.v4.content.ContextCompat
import android.util.Log
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
import kotlinx.android.synthetic.main.location_screen.*
import kotlinx.android.synthetic.main.marker_info_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataMapsApi
import ps.petugas.salam.wakatobi.utils.RouteDecode
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import java.io.UnsupportedEncodingException
import java.util.ArrayList

class VLocation : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ILocation.View {
    private val mPresenter = PLocation()
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLastLocation: Location? = null
    private lateinit var progressDialog: ProgressDialog

    private lateinit var bounds: LatLngBounds
    private lateinit var mMap: GoogleMap
    private lateinit var myLatLng: LatLng
    private lateinit var latLngAsset: LatLng

    private lateinit var cu: CameraUpdate
    private var line: Polyline? = null

    private var myLat: Double = 0.0
    private var myLng: Double = 0.0
    private lateinit var values: HashMap<String, String>

    private lateinit var rxPermission: RxPermissions

    override fun getLayoutResource(): Int {
        return R.layout.location_screen
    }

    override fun myCodeHere() {
        title = intent.extras.getString("name_report")
        setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)
        rxPermission = RxPermissions(this)
        values = HashMap()
        Logger.d("VLOCATION" + intent.extras.getString("latitude") + " : " + intent.extras.getString("longitude"))
        values.put("latitude", intent.extras.getString("latitude"))
        values.put("longitude", intent.extras.getString("longitude"))
        values.put("name_report", intent.extras.getString("name_report"))
        values.put("address", intent.extras.getString("address"))

        buildGoogleApiClient()
        mGoogleApiClient.connect()

        fabDirection.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage(getString(R.string.please_wait))
            progressDialog.show()
            val url = "maps/api/directions/json?" + "origin=" + myLatLng.latitude + "," + myLatLng.longitude + "&destination=" + latLngAsset.latitude + "," + latLngAsset.longitude + "&sensor=false&units=metric"
            mPresenter.getDirection(url)
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    @Synchronized private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }


    override fun onGetDirection(dataMapsApi: DataMapsApi) {
        line?.remove()
        mMap.clear()

        val rectLine = PolylineOptions()

        val routeList = ArrayList<LatLng>()
        if (dataMapsApi.routes.isNotEmpty()) {
            var decodeList: ArrayList<LatLng>
            val routeA = dataMapsApi.routes[0]
            Log.i("tuxkids", "Legs length : " + routeA.legs.size)
            if (routeA.legs.isNotEmpty()) {
                val steps = routeA.legs[0].steps
                Log.i("tuxkids", "Steps size :" + steps.size)
                var step: DataMapsApi.StepsItem
                var polyline: String
                for (i in steps.indices) {
                    step = steps[i]
                    routeList.add(LatLng(step.start_location.lat, step.start_location.lng))
                    polyline = step.polyline.points!!
                    decodeList = RouteDecode.decodePoly(polyline)
                    routeList.addAll(decodeList)
                    routeList.add(LatLng(step.end_location.lat, step.end_location.lng))
                    Log.i("tuxkids", "End Locations :" + step.end_location.lat + ", " + step.end_location.lng)
                }
            }
        }

        if (routeList.size > 0) {
            for (i in routeList.indices) {
                rectLine.add(routeList[i]).width(15f).color(
                        ContextCompat.getColor(this, R.color.colorPrimary))
            }
            val bld = LatLngBounds.Builder()
            bld.include(myLatLng)
            bld.include(latLngAsset)
            val bounds = bld.build()

            Logger.d("uhuy " + values["latitude"]!!.toDouble() + values["longitude"]!!.toDouble())
            latLngAsset = LatLng(values["latitude"]!!.toDouble(), values["longitude"]!!.toDouble())

            bld.include(latLngAsset)
            mMap.addMarker(MarkerOptions()
                    .position(latLngAsset)
                    .snippet(values["name_report"] + "%"
                            + values["address"])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
//                    .icon(BitmapDescriptorFactory.fromBitmap(resource)))

            bld.include(myLatLng)
            mMap.setOnMapLoadedCallback {
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 90)
                mMap.moveCamera(cu)
                mMap.animateCamera(cu)
            }
            mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.marker_info_screen, null)

                    val snippet = marker.snippet
                    val splitSnippet = snippet.split("%".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

                    tv_title.text = splitSnippet[0]
                    tv_address.text = splitSnippet[1]

                    mMap.setOnInfoWindowClickListener { _ -> Toast.makeText(applicationContext, splitSnippet[0], Toast.LENGTH_SHORT).show() }

                    return view
                }
            })
        }
        mMap.isTrafficEnabled = true

        // Adding route on the map
        line = mMap.addPolyline(rectLine)

        progressDialog.dismiss()

    }

    override fun onSuccess(isSuccess: Boolean, message: String) {
        if (isSuccess) progressDialog.dismiss()
        else {
            CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun setupMap(googleMap: GoogleMap, values: HashMap<String, String>) {
        Logger.d("setupMap")
        mMap = googleMap
        setMapType(SharedPref.getInt(SharedKey.mode_map))

        rxPermission.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe({ permission ->
                    if (permission) {
                        Logger.d("permission granted")
                        mMap.isMyLocationEnabled = true
                        if (values.size == 0) {
                            Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_SHORT).show()
                        } else {
                            val bld = LatLngBounds.Builder()


                            Logger.d("uhuy " + values["latitude"]!!.toDouble() + values["longitude"]!!.toDouble())
                            latLngAsset = LatLng(values["latitude"]!!.toDouble(), values["longitude"]!!.toDouble())

                            bld.include(latLngAsset)
                            mMap.addMarker(MarkerOptions()
                                    .position(latLngAsset)
                                    .snippet(values["name_report"] + "%" + values["address"])
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
//                                    .icon(BitmapDescriptorFactory.fromBitmap(resource)))
                            bounds = bld.build()
                            bld.include(myLatLng)
                            bounds = bld.build()
                            mMap.setOnMapLoadedCallback {
                                cu = CameraUpdateFactory.newLatLngBounds(bounds, 90)
                                mMap.moveCamera(cu)
                                mMap.animateCamera(cu)
                            }
                            mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                                override fun getInfoWindow(marker: Marker): View? {
                                    return null
                                }

                                override fun getInfoContents(marker: Marker): View {
                                    @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.marker_info_screen, null)

                                    val snippet = marker.snippet
                                    val splitSnippet = snippet.split("%".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                                    val title = view.findViewById(R.id.tv_title) as TextView
                                    val address = view.findViewById(R.id.tv_address) as TextView
                                    title.text = splitSnippet[0]
                                    address.text = splitSnippet[1]

                                    mMap.setOnInfoWindowClickListener { _ -> Toast.makeText(applicationContext, splitSnippet[0], Toast.LENGTH_SHORT).show() }

                                    return view
                                }
                            })
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

                            /**
                             * Display map onConnected
                             */

                            val mapView = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                            mapView.getMapAsync { googleMap ->
                                try {
                                    setupMap(googleMap, values)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            // Draw direction route between user position and place position


                        } else {
                            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                gpsPermission()
                            }
                            Toast.makeText(this, getString(R.string.cant_find_location), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.access_location_not_allowed))
                        builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                        builder.setCancelable(false)
                        builder.show()
                    }
                })
    }

    private fun gpsPermission() {
        val builder = AlertDialog.Builder(this)
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


    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }


    override fun onDetachScreen() {
        mPresenter.detachView()
    }

}
