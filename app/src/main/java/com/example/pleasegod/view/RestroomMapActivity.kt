package com.example.pleasegod.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pleasegod.R
import com.example.pleasegod.databinding.ActivityRestroomMapBinding
import com.example.pleasegod.databinding.ItemRestroomInformationBinding
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.observer.DefaultObserver
import com.example.pleasegod.view.adapter.RestroomListAdapter
import com.example.pleasegod.view.adapter.SearchedRestroomAdapter
import com.example.pleasegod.viewmodel.RestroomViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_restroom_map.*
import kotlinx.android.synthetic.main.bottom_sheet_searched_restroom.view.rv_searched_restroom_list

class RestroomMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {
    companion object {
        val TAG: String = RestroomMapActivity::class.java.simpleName
        val DEFAULT_ZOOM: Float = 15f
    }

    private val mGoogleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(this@RestroomMapActivity)
            .addConnectionCallbacks(this@RestroomMapActivity)
            .addOnConnectionFailedListener(this@RestroomMapActivity)
            .addApi(LocationServices.API)
            .build()
    }
    private lateinit var mMap: GoogleMap
    private val mFusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this@RestroomMapActivity)
    }
    private lateinit var mCurrentLatLng: LatLng
    private val mRestroomViewModel: RestroomViewModel by lazy {
        ViewModelProviders.of(this@RestroomMapActivity).get(RestroomViewModel::class.java)
    }
    private val mRestroomListLoadingFinishSubject: PublishSubject<Boolean> =
        PublishSubject.create<Boolean>()
    private val mRestroomList: ObservableArrayList<Restroom> by lazy {
        mRestroomViewModel.mRestroomList
    }
    private var mSelectedRestroomRoadNameAddress: String? = null
    private lateinit var mClickedRestroom: Restroom
    private var mPreviousClickedMarker: Marker? = null
    private var mPolylineToRestroom: Polyline? = null
    private val mMarkerMap: HashMap<Restroom, Marker> = HashMap<Restroom, Marker>()
    private val mBottomSheetDialog: BottomSheetDialog by lazy {
        BottomSheetDialog(this@RestroomMapActivity)
    }
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityRestroomMapBinding>(
            this@RestroomMapActivity,
            R.layout.activity_restroom_map
        ).apply {
            isLoadingFinish = mRestroomViewModel.mIsLoadingFinish
        }
        setSupportActionBar(map_toolbar as Toolbar)

        val mapFragment = (supportFragmentManager.findFragmentById(R.id.restroom_map) as SupportMapFragment).apply {
            getMapAsync(this@RestroomMapActivity)
        }

        init()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        mRestroomListLoadingFinishSubject.onNext(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        search_view.setMenuItem(menu?.findItem(R.id.action_search))

        return true
    }

    private fun init() {
        mCompositeDisposable.add(
            mRestroomListLoadingFinishSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DefaultObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                        Log.d(TAG, "${Thread.currentThread()}")

                        if (t) {
                            addMarkerForRestroomList()
                            showUserSelectedRestroom()
                        }
                    }
                })
        )
        mRestroomList.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableArrayList<Restroom>>() {
            override fun onItemRangeInserted(
                sender: ObservableArrayList<Restroom>?,
                positionStart: Int,
                itemCount: Int
            ) {
                mRestroomListLoadingFinishSubject.onNext(true)
            }

            override fun onChanged(sender: ObservableArrayList<Restroom>?) { }

            override fun onItemRangeRemoved(
                sender: ObservableArrayList<Restroom>?,
                positionStart: Int,
                itemCount: Int
            ) { }

            override fun onItemRangeMoved(
                sender: ObservableArrayList<Restroom>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) { }

            override fun onItemRangeChanged(
                sender: ObservableArrayList<Restroom>?,
                positionStart: Int,
                itemCount: Int
            ) { }
        })

        search_view.apply {
            setCursorDrawable(R.drawable.color_cursor_white)
            setEllipsize(true)
            setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, query)

                    query?.let {
                        searchRestroomByName(query)
                    }

                    search_view.closeSearch()

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
                override fun onSearchViewShown() {
                    Log.d(TAG, "onSearchViewShown()")
                }

                override fun onSearchViewClosed() {
                    Log.d(TAG, "onSearchViewClosed()")
                }
            })
        }
        intent.getStringExtra(RestroomListAdapter.INTENT_KEY)?.let {
            mSelectedRestroomRoadNameAddress = it
        }
    }

    private fun searchRestroomByName(query: String) {
        showSearchedRestroomList(mRestroomList.filter {
            it.pbctlt_plc_nm.contains(query) && it.refine_wgs84_lat != null && it.refine_wgs84_logt != null
        }.toMutableList())
    }

    private fun showSearchedRestroomList(restroomList: MutableList<Restroom>) {
        val bottomSheetView: View =
            LayoutInflater.from(this@RestroomMapActivity).inflate(R.layout.bottom_sheet_searched_restroom, null).apply {
                rv_searched_restroom_list.apply {
                    layoutManager = LinearLayoutManager(this@RestroomMapActivity)
                    adapter = SearchedRestroomAdapter(
                        this@RestroomMapActivity,
                        restroomList,
                        object : SearchedRestroomAdapter.RestroomClickListener {
                            override fun onRestroomClick(restroom: Restroom) {
                                restroom.refine_wgs84_lat?.let {
                                    restroom.refine_wgs84_logt?.let {
                                        changeClickedRestroom(restroom)

                                        mMap.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                    restroom.refine_wgs84_lat.toDouble(),
                                                    restroom.refine_wgs84_logt.toDouble()
                                                ),
                                                DEFAULT_ZOOM
                                            )
                                        )
                                        onMarkerClick((mMarkerMap[restroom]))
                                    }
                                }

                                mBottomSheetDialog.dismiss()
                            }
                        }
                    )
                    addItemDecoration(DividerItemDecoration(this@RestroomMapActivity, LinearLayoutManager.VERTICAL))
                }
            }

        with(mBottomSheetDialog) {
            setContentView(bottomSheetView)
            show()
        }
    }

    private fun addMarkerForRestroomList() {
        mRestroomList.filter {
            it.pbctlt_plc_nm != null && it.refine_wgs84_lat != null && it.refine_wgs84_logt != null
        }.forEach { restroom ->
            val latitude: Double = restroom.refine_wgs84_lat!!.toDouble()
            val longitude: Double = restroom.refine_wgs84_logt!!.toDouble()
            val latLng: LatLng = LatLng(latitude, longitude)
            val snippet: String? = restroom.refine_roadnm_addr
            var marker: Marker

            if (restroom.refine_roadnm_addr == mSelectedRestroomRoadNameAddress) {
                marker = addMarker(
                    restroom.pbctlt_plc_nm,
                    latLng,
                    snippet,
                    BitmapDescriptorFactory.HUE_AZURE
                )
            } else {
                marker = addMarker(
                    restroom.pbctlt_plc_nm,
                    latLng,
                    snippet
                )
            }

            mMarkerMap.put(restroom, marker)
        }
    }

    private fun setCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationResult: Task<Location> = (mFusedLocationProviderClient.lastLocation).apply {
                addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let {
                            mCurrentLatLng = LatLng(it.latitude, it.longitude)

                            addMarker(
                                    getString(R.string.current_location),
                                    mCurrentLatLng,
                                    null,
                                    BitmapDescriptorFactory.HUE_RED,
                                    "current_location"
                            )

                            getSharedPreferences("selected_location_preferences", Context.MODE_PRIVATE).apply {
                                getString(RestroomListActivity.PREFERENCES_KEY, null)?.let {
                                    requestPublishRestroomList(it)
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "can't retrieve current location!")
                        Log.d(TAG, task.exception?.message)
                    }
                }
            }
        }
    }

    private fun changeClickedRestroom(restroom: Restroom) {
        mClickedRestroom = restroom
    }

    private fun changeClickedRestroom(marker: Marker) {
        mRestroomList.find {
            it.refine_roadnm_addr == marker.snippet
        }?.let {
            mClickedRestroom = it
        }
    }

    private fun addMarker(
            locationName: String,
            latlng: LatLng,
            snippetStr: String?,
            iconValue: Float = BitmapDescriptorFactory.HUE_YELLOW,
            tagValue: Any? = null
    ): Marker {
        val marker: Marker = mMap.addMarker(
                MarkerOptions().title(locationName)
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(iconValue))
                        .snippet(snippetStr)
        ).apply {
            if (iconValue == BitmapDescriptorFactory.HUE_AZURE) {
                mPreviousClickedMarker = this

                changeClickedRestroom(this)
                showInfoWindow()
                drawLineToRestroom(mClickedRestroom)
            }

            tag = tagValue
        }

        mMap.setOnInfoWindowClickListener {
            showRestroomInformationDialog()
        }

        mMap.setOnMarkerClickListener(this@RestroomMapActivity)

        return marker
    }

    /**
     * If a marker is clicked, onMarkerClick() will be called.
     * onMarkerClick() returns a boolean that indicates whether
     * suppress default marker-clicked behaviour or not.
     * If it returns false, then the default behavior will occur in addition
     * to custom behaviour.
     * The default behaviour for a marker click event is
     * to show its info window(if available) and move the camera
     * such that the marker is centered on the map.
     */
    override fun onMarkerClick(clickedMarker: Marker?): Boolean {
        clickedMarker?.let {
            if (clickedMarker.tag != "current_location") {
                mPreviousClickedMarker?.let { previousMarKer ->
                    it.hideInfoWindow()
                    previousMarKer.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    clickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                }
                changeClickedRestroom(clickedMarker)
                drawLineToRestroom(mClickedRestroom)

                clickedMarker.showInfoWindow()

                mPreviousClickedMarker = clickedMarker
            }
        }

        return false
    }

    private fun drawLineToRestroom(restroom: Restroom) {
        Log.d(TAG, "drawRouteToRestroom()")

        if (restroom.refine_wgs84_lat != null) {
            if (restroom.refine_wgs84_logt != null) {
                val latitude: Double = restroom.refine_wgs84_lat.toDouble()
                val longitude: Double = restroom.refine_wgs84_logt.toDouble()

                mPolylineToRestroom?.let {
                    it.remove()
                }

                mPolylineToRestroom = mMap.addPolyline(
                        PolylineOptions()
                                .color(Color.RED)
                                .width(5f)
                                .add(mCurrentLatLng)
                                .add(LatLng(latitude, longitude))
                )
            }
        }
    }

    fun getRestroomDistance(): CharSequence {
        if (mClickedRestroom.refine_wgs84_lat != null && mClickedRestroom.refine_wgs84_logt != null) {
            val distanceArr: FloatArray = FloatArray(1)

            Location.distanceBetween(
                mCurrentLatLng.latitude,
                mCurrentLatLng.longitude,
                mClickedRestroom.refine_wgs84_lat!!.toDouble(),
                mClickedRestroom.refine_wgs84_logt!!.toDouble(),
                distanceArr
            )

            return "${distanceArr[0]}"
        }

        return ""
    }

    private fun showRestroomInformationDialog() {
        val restroomInformationView: View = LayoutInflater.from(this@RestroomMapActivity).inflate(
            R.layout.item_restroom_information, null
        )
        val binding: ItemRestroomInformationBinding? = DataBindingUtil.bind<ItemRestroomInformationBinding>(
            restroomInformationView
        )
        val restroomInformationDialog: BottomSheetDialog = BottomSheetDialog(this@RestroomMapActivity).apply {
            setContentView(restroomInformationView)
        }

        binding?.let {
            it.activity = this@RestroomMapActivity
            it.restroom = mClickedRestroom
        }

        restroomInformationDialog.show()
    }

    private fun requestPublishRestroomList(sigunName: String) {
        requestPublishRestroomList(1, 1000, sigunName)
    }

    private fun requestPublishRestroomList(pageIndex: Int = 1, pageSize: Int = 1000, sigunName: String = "고양시") {
        mRestroomViewModel.requestPublishRestroomList(getString(R.string.api_key), pageIndex, pageSize, sigunName)
    }

    override fun onConnected(connectionHint: Bundle?) {
        Log.d(TAG, "onConnected()")
    }

    override fun onConnectionSuspended(cause: Int) {
        Log.d(TAG, "onConnectionSuspended()")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed()")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady()")

        mMap = googleMap.apply {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isMyLocationEnabled = true
            }

            uiSettings.let {
                it.isMyLocationButtonEnabled = true
                it.isZoomControlsEnabled = true
            }

            setOnMapClickListener {
                if (search_view.isSearchOpen) {
                    search_view.closeSearch()
                }
            }
        }

        setCurrentLocation()
    }

    private fun showUserSelectedRestroom() {
        Log.d(TAG, "${mRestroomList.size}")

        mRestroomList.find {
            it.refine_roadnm_addr == mSelectedRestroomRoadNameAddress && it.refine_wgs84_lat != null && it.refine_wgs84_logt != null
        }?.let {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.refine_wgs84_lat!!.toDouble(), it.refine_wgs84_logt!!.toDouble()),
                    DEFAULT_ZOOM
                )
            )

            mPreviousClickedMarker = mMarkerMap[it]
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        } else {
            finish()
            overridePendingTransition(R.anim.animation_slide_from_left, R.anim.animation_slide_to_right)
        }
    }

    override fun onDestroy() {
        mCompositeDisposable.clear()

        super.onDestroy()
    }
}
