package ps.petugas.salam.wakatobi.model

/**
 **********************************************
 * Created by ukie on 3/18/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class DataMapsApi {

    lateinit var routes: List<RoutesItem>
    lateinit var geocoded_waypoints: List<GeocodedWaypointsItem>
    lateinit var status: String

    class GeocodedWaypointsItem {
        lateinit var types: List<String>
        lateinit var geocoder_status: String
        lateinit var place_id: String
    }

    class RoutesItem {
        lateinit var summary: String
        lateinit var copyrights: String
        lateinit var legs: List<LegsItem>
        lateinit var warnings: List<String>
        lateinit var bounds: Bounds
        lateinit var overview_polyline: OverviewPolyline
        lateinit var waypoint_order: List<String>
    }

    class Bounds {
        lateinit var southwest: Southwest
        lateinit var northeast: Northeast
    }

    class Southwest {
        val lng: Double = 0.0
        val lat: Double = 0.0
    }

    class Northeast {
        val lng: Double = 0.0
        val lat: Double = 0.0
    }

    class OverviewPolyline {
        val points: String? = null
    }

    class LegsItem {
        lateinit var duration: Duration
        lateinit var start_location: StartLocation
        lateinit var distance: Distance
        lateinit var start_address: String
        lateinit var end_location: EndLocation
        lateinit var end_address: String
        lateinit var via_waypoint: List<String>
        lateinit var steps: List<StepsItem>
        lateinit var traffic_speed_entry: List<String>
    }

    class StepsItem {
        lateinit var duration: Duration
        lateinit var start_location: StartLocation
        lateinit var distance: Distance
        lateinit var travel_mode: String
        lateinit var html_instructions: String
        lateinit var end_location: EndLocation
        lateinit var polyline: Polyline
    }

    class Duration {
        val text: String? = null
        val value: Int = 0
    }

    class StartLocation {
        val lng: Double = 0.0
        val lat: Double = 0.0
    }

    class EndLocation {
        val lng: Double = 0.0
        val lat: Double = 0.0
    }

    class Distance {
        val text: String? = null
        val value: Int = 0
    }

    class Polyline {
        val points: String? = null
    }
}