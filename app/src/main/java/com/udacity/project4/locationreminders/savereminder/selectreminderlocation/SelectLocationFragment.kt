package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback{

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var gMap: GoogleMap
    private lateinit var pointOfInterest: PointOfInterest
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location


        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        val latitude =30.22610729440105
        val longtitude = 31.448663856184606
        val egypt = LatLng(latitude,longtitude)
        val zoomLevel = 20f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(egypt,zoomLevel))
        googleMap.addMarker(MarkerOptions()
            .position(egypt)
            .title("Marker in Egypt"))


        enableMyLocation();
        // set On Poi Click listener
        setPoiClick(gMap)
        onLocationSelected()

    }


    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        binding.savebtn.setOnClickListener {
            if(this::pointOfInterest.isInitialized){
                _viewModel.latitude.value = pointOfInterest.latLng.latitude
                _viewModel.longitude.value = pointOfInterest.latLng.longitude
                _viewModel.reminderSelectedLocationStr.value = pointOfInterest.name
                _viewModel.selectedPOI.value = pointOfInterest
                _viewModel.navigationCommand.value = NavigationCommand.To(SelectLocationFragmentDirections.actionSelectLocationFragmentToSaveReminderFragment())

            }else{
                Toast.makeText(context,"Select Location",Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
         REQUEST_LOCATION_PERMISSION->{
             if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                 enableMyLocation()
             }else{
                 Toast.makeText(context,"Location not Granted", Toast.LENGTH_LONG).show()
             }
         }
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            gMap.setMyLocationEnabled(true)
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }


    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener { poi ->
            map.clear()
            pointOfInterest = poi
            var poiMarker = map.addMarker(
                MarkerOptions()
                .position(poi.latLng)
                .title(poi.name)
            )
            map.addCircle(CircleOptions()
                .center(poi.latLng)
                .radius(200.0)
                .strokeColor(Color.argb(255,255,0,0))
                .fillColor(Color.argb(60,250,0,0))
                .strokeWidth(4F))
            if(poiMarker !=null){
                poiMarker.showInfoWindow()
            }
        }
    }
}
