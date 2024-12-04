package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class NaviActivity : AppCompatActivity() {

    private lateinit var tabLayout_navi: TabLayout
    lateinit var viewPager_navi: ViewPager2

    private lateinit var mapViewContainer: FrameLayout
    private lateinit var mapView: MapView
    private lateinit var mapView_null: TextView

    private lateinit var btnNaverMap: ImageButton
    private lateinit var btnGoogleMap: ImageButton
    private lateinit var btnKakaoMap: ImageButton

    private lateinit var spinner: Spinner

    private var selectedPlace: Place? = null
    private var areImagesVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navi)

        tabLayout_navi = findViewById(R.id.tabLayout_navi)
        viewPager_navi = findViewById(R.id.layoutContainer_navi)

        mapViewContainer = findViewById(R.id.mapViewContainer)
        mapView = findViewById(R.id.mapView_navi)
        mapView_null = findViewById(R.id.mapView_null)

        btnNaverMap = findViewById(R.id.imageNaver)
        btnGoogleMap = findViewById(R.id.imageGoogle)
        btnKakaoMap = findViewById(R.id.imageKakao)

        spinner = findViewById(R.id.spinnerDistanceFilter)

        val btnNavigate = findViewById<FloatingActionButton>(R.id.btnNavigate)

        observePlaceSelection()
        initializeMap()
        updateMapView()
        setupTabLayoutAndViewPager()

        btnNavigate.setOnClickListener {
            if (selectedPlace == null) {
                // 장소가 선택되지 않았을 경우 Toast 메시지 표시
                Toast.makeText(this, "장소를 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 클릭 이벤트 중단
            }

            areImagesVisible = !areImagesVisible
            val visibility = if (areImagesVisible) View.VISIBLE else View.GONE

            btnNaverMap.visibility = visibility
            btnGoogleMap.visibility = visibility
            btnKakaoMap.visibility = visibility
        }

        val backButton: AppCompatButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // 뒤로가기 액션 수행
        }


    }
    private fun setupTabLayoutAndViewPager() {
        val adapter_navi = NaviPagerAdapter(this)
        viewPager_navi.adapter = adapter_navi

        TabLayoutMediator(tabLayout_navi, viewPager_navi) { tab, position ->
            tab.text = when (position) {
                0 -> "        추천순        "
                1 -> "        거리순        "
                else -> null
            }
        }.attach()

        // 페이지 변경 콜백 추가
        viewPager_navi.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                //observePlaceSelection()
                // 버튼 숨기기
                //btnNaverMap.visibility = View.GONE
                //btnGoogleMap.visibility = View.GONE
                //btnKakaoMap.visibility = View.GONE
                //areImagesVisible = false

                //updateMapView()

                // 현재 탭에서 선택된 항목 가져오기
                selectedPlace = getSelectedPlaceForTab(position)

                // 선택된 항목이 있을 경우 지도 업데이트
                if (selectedPlace != null) {
                    updateMapView()
                } else {
                    // 선택된 항목이 없을 경우 지도 초기화
                    mapView_null.visibility = View.VISIBLE
                    mapView.visibility = View.INVISIBLE
                }
            }
        })
    }

    // 네이버 지도 버튼 클릭 리스너

    // 구글 지도 버튼 클릭 리스너

    // 카카오 지도 버튼 클릭 리스너

    // 각 탭에 따라 선택된 항목을 가져오는 함수
    private fun getSelectedPlaceForTab(position: Int): Place? {
        return when (position) {
            0 -> {
                // 추천순 탭에서 선택된 장소 가져오기
                // 이 부분은 실제 데이터 소스에 따라 변경 필요
                (viewPager_navi.adapter as NaviPagerAdapter).getSelectedPlaceForTab0()
            }
            1 -> {
                // 거리순 탭에서 선택된 장소 가져오기
                // 이 부분도 실제 데이터 소스에 따라 변경 필요
                (viewPager_navi.adapter as NaviPagerAdapter).getSelectedPlaceForTab1()
            }
            else -> null
        }
    }


    private fun observePlaceSelection() {
        val fragmentManager = supportFragmentManager
        fragmentManager.setFragmentResultListener("placeSelected", this) { _, bundle ->
            selectedPlace = bundle.getParcelable("selectedPlace")
            updateMapView()
            btnNaverMap.visibility = View.GONE
            btnGoogleMap.visibility = View.GONE
            btnKakaoMap.visibility = View.GONE
            areImagesVisible = false
        }
    }

    private fun updateMapView() {
        Log.d(TAG, "heyheyhey?"+ selectedPlace.toString())
        if (selectedPlace == null) {
            // 선택된 장소가 없을 때 처리
            mapView_null.visibility = View.VISIBLE
            mapView.visibility = View.INVISIBLE
            //mapView.overlays.clear()
            return
        }
        else
        {
            Log.d(TAG, "heyhey"+ selectedPlace.toString())
            mapView_null.visibility = View.INVISIBLE
            mapView.visibility = View.VISIBLE
        }


        val location = GeoPoint(selectedPlace!!.latitude, selectedPlace!!.longitude)
        mapView.controller.setCenter(location)
        mapView.controller.setZoom(15.0)

        // 마커 추가
        mapView.overlays.clear()
        val marker = org.osmdroid.views.overlay.Marker(mapView)
        marker.position = location
        marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
        marker.title = selectedPlace!!.name
        mapView.overlays.add(marker)
    }
    private fun initializeMap(){
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // MapView 초기화
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(GeoPoint(37.308711, 127.136805)) // Example: San Francisco
    }

    private fun updateMapMarker(place: Place) {
        val marker = org.osmdroid.views.overlay.Marker(mapView).apply {
            position = GeoPoint(place.latitude, place.longitude)
            setAnchor(
                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
            )
            title = place.name
        }
        mapView.overlays.clear() // 기존 마커 제거
        mapView.overlays.add(marker) // 새로운 마커 추가
        mapView.controller.setCenter(marker.position) // 지도 중심 이동
        mapView.invalidate() // 지도 갱신
    }

    fun updateMap(location: GeoPoint) {
        mapView.controller.setCenter(location)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }
}
