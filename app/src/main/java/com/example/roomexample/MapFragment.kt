package com.example.roomexample

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.roomexample.database.Scene
import com.example.roomexample.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var args: MapFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        // retrieve the passed argument (scene'name and scene's address)
        args = MapFragmentArgs.fromBundle(requireArguments())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var  addressList :List<Address>

        //get the Google map object
        mMap = googleMap
        //use the Geocoder the translate the address into the location
        val geoCoder = Geocoder(context)
        addressList = geoCoder.getFromLocationName(args.address, 1)
        // Add a marker and move the camera
        if (!addressList.isNullOrEmpty()) {
            val location = LatLng(addressList[0].latitude, addressList[0].longitude)
            mMap.addMarker(MarkerOptions().position(location).title(args.name))
            //zoom = 15f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

}