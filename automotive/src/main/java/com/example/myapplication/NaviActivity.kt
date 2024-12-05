package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import androidx.fragment.app.Fragment // 추가


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

    private val mockLocation = Location("").apply {
        latitude = 37.308711 // 현재 위치의 위도
        longitude = 127.136805 // 현재 위치의 경도
    }

    private fun getCurrentLocation_navi(): Location {
        return Location("dummyprovider").apply {
            latitude = 37.308711
            longitude = 127.136805
        }
    }

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

        // 네이버 지도 버튼 클릭 리스너
        btnNaverMap.setOnClickListener {
            Log.d("NavigationBottomSheet", "Naver Map Button Clicked") // 추가
            openMapApp("nmap://navigation?dlat=${selectedPlace?.latitude}&dlng=${selectedPlace?.longitude}&dname=${selectedPlace?.name}")
        }

        btnGoogleMap.setOnClickListener {
            Log.d("NavigationBottomSheet", "Google Map Button Clicked") // 추가
            openMapApp("http://maps.google.com/maps?daddr=${selectedPlace?.latitude},${selectedPlace?.longitude}")
        }

        btnKakaoMap.setOnClickListener {
            Log.d("NavigationBottomSheet", "Kakao Map Button Clicked") // 추가
            openMapApp("kakaomap://route?sp=${selectedPlace!!.latitude},${selectedPlace!!.longitude}&ep=${selectedPlace!!.latitude},${selectedPlace!!.longitude}&by=CAR")
        }

        val backButton: AppCompatButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // 뒤로가기 액션 수행
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 아이템 가져오기
                val selectedValue = parent?.getItemAtPosition(position).toString()

                val distanceFilter = when (selectedValue) {
                    "ALL" -> Int.MAX_VALUE
                    "5km 이내" -> 5
                    "10km 이내" -> 10
                    "15km 이내" -> 15
                    "20km 이내" -> 20
                    "30km 이내" -> 30
                    "50km 이내" -> 50
                    else -> 20
                }
                // Fragment 추가

                val myFragment = NaviRecommendFragment.newInstance(selectedValue)
                if (myFragment.isAdded)
                {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutContainer_navi, myFragment)
                    .commit()
                    myFragment.setupRecyclerView(mockLocation, distanceFilter, PlaceRepository.FilterMode.RECOMMENDED)
                //
                }

                // NaviRecommendFragment 초기화
                //val fragmentManager = supportFragmentManager
                //val transaction = fragmentManager.beginTransaction()

                // 기존 Fragment를 제거하고 새 Fragment 추가
                //val fragment = NaviRecommendFragment.newInstance(selectedValue)
                //transaction.replace(R.id.layoutContainer_navi, fragment) // Fragment가 추가될 컨테이너 ID
                //transaction.commit()

                // Adapter의 데이터를 업데이트하여 새로운 상태 반영
                //(viewPager_navi.adapter as NaviPagerAdapter).updateSelectedValue(selectedValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 것도 선택되지 않았을 때 처리 (필요시 구현)
            }
        }

    }
    fun getSelectedSpinnerValue(): String {
        return spinner.selectedItem.toString()
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


    private fun openMapApp(uri: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
                Log.d("NavigationBottomSheet", "Navigation Intent Started: $uri") // 추가
            } else {
                Toast.makeText(this, "해당 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                Log.e("NavigationBottomSheet", "App not installed for URI: $uri") // 추가
            }
        } catch (e: Exception) {
            Log.e("NavigationBottomSheet", "Error opening map app with URI: $uri", e) // 추가
            Toast.makeText(this, "길안내를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    // 구글 지도 버튼 클릭 리스너

    // 카카오 지도 버튼 클릭 리스너

    // 각 탭에 따라 선택된 항목을 가져오는 함수
    private fun getSelectedPlaceForTab(position: Int): Place? {
        return when (position) {
            0 -> {
                // 추천순 탭에서 선택된 장소 가져오기
                // 이 부분은 실제 데이터 소스에 따라 변경 필요
                spinner.visibility = View.VISIBLE
                (viewPager_navi.adapter as NaviPagerAdapter).getSelectedPlaceForTab0()
            }
            1 -> {
                // 거리순 탭에서 선택된 장소 가져오기
                // 이 부분도 실제 데이터 소스에 따라 변경 필요
                spinner.visibility = View.GONE
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
