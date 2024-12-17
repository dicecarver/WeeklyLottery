package com.example.weeklylottery

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
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
    private lateinit var mapView: ImageView
    //private lateinit var mapView: MapView
    private lateinit var mapView_null: TextView

    private lateinit var btnNaverMap: ImageButton
    private lateinit var btnGoogleMap: ImageButton
    private lateinit var btnKakaoMap: ImageButton

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
                //spinner.visibility = View.VISIBLE
                (viewPager_navi.adapter as NaviPagerAdapter).getSelectedPlaceForTab0()
            }
            1 -> {
                // 거리순 탭에서 선택된 장소 가져오기
                // 이 부분도 실제 데이터 소스에 따라 변경 필요
                //spinner.visibility = View.GONE
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
        //Log.d(TAG, "heyheyhey?"+ selectedPlace.toString())
        if (selectedPlace == null) {
            // 선택된 장소가 없을 때 처리
            mapView_null.visibility = View.VISIBLE
            mapView.visibility = View.INVISIBLE
            //mapView.overlays.clear()
            return
        }
        else
        {
            //Log.d(TAG, "heyhey"+ selectedPlace.toString())
            mapView_null.visibility = View.INVISIBLE
            mapView.visibility = View.VISIBLE
        }

        /* 임시
        val location = GeoPoint(selectedPlace!!.latitude, selectedPlace!!.longitude)
        mapView.controller.setCenter(location)
        mapView.controller.setZoom(16.0)

        // 마커 추가
        mapView.overlays.clear()
        val marker = org.osmdroid.views.overlay.Marker(mapView)
        marker.position = location
        marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
        marker.title = selectedPlace!!.name

        // 클릭 동작 비활성화
        marker.setOnMarkerClickListener { _, _ ->
            // 아무 동작도 하지 않음
            true
        }

        // Bitmap으로 커스텀 마커 아이콘 크기 조정 및 설정
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.custom_marker_icon)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 50, 85, false)
        val customDrawable = BitmapDrawable(resources, scaledBitmap)
        marker.icon = customDrawable

        // 텍스트 오버레이 추가
        val textOverlay = object : org.osmdroid.views.overlay.Overlay() {
            override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
                super.draw(canvas, mapView, shadow)

                // 마커 위치를 화면 좌표로 변환
                val point = Point()
                val projection = mapView.projection
                projection.toPixels(marker.position, point)


                // 텍스트 스타일 설정
                val textPaint = Paint().apply {
                    color = Color.BLACK // 텍스트 색상
                    textSize = 24f // 텍스트 크기
                    textAlign = Paint.Align.CENTER // 텍스트 중앙 정렬
                    style = Paint.Style.FILL // 텍스트는 채우기 스타일
                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    typeface = ResourcesCompat.getFont(this@NaviActivity, R.font.gmarketsansbold)
                }

                // 텍스트 외곽선 스타일 설정 (외곽선용 Paint)
                val strokePaint = Paint().apply {
                    color = Color.WHITE // 외곽선 색상
                    textSize = 24f // 텍스트 크기 (내부와 동일)
                    textAlign = Paint.Align.CENTER // 텍스트 중앙 정렬
                    style = Paint.Style.STROKE // 외곽선 스타일
                    strokeWidth = 6f // 외곽선 두께
                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    typeface = ResourcesCompat.getFont(this@NaviActivity, R.font.gmarketsansbold)
                }

                // 텍스트 계산
                val text = selectedPlace!!.name
                val textYOffset = 20f // 텍스트 위치 조정 오프섹(마커 밑으로)

                // 텍스트 외곽선 먼저 그리기
                canvas.drawText(text,point.x.toFloat(),point.y.toFloat() + textYOffset,strokePaint)

                // 텍스트 내부 채우기
                canvas.drawText(text, point.x.toFloat(), point.y.toFloat() + textYOffset, textPaint)
            }
        }


        mapView.overlays.add(marker)
        mapView.overlays.add(textOverlay)

        */
    }
    private fun initializeMap(){
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // MapView 초기화
        /* 임시
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(GeoPoint(37.308711, 127.136805)) // Example: San Francisco

         */
    }
    /* 임시
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
    */

}
