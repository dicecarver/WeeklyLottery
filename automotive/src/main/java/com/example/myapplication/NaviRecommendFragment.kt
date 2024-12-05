package com.example.myapplication

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView
import com.google.android.gms.maps.model.Marker
import org.osmdroid.util.GeoPoint
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.interfaces.MapViewUpdateListener
import com.example.myapplication.PlaceRepository.FilterMode
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NaviRecommendFragment : Fragment(), PlaceAdapter.OnItemClickListener {

    private var mapViewUpdateListener: MapViewUpdateListener? = null

    private lateinit var recommendRecyclerView: RecyclerView


    //private lateinit var lottoResults: List<LottoResult>

    private lateinit var placeAdapter: PlaceAdapter
    private var places: List<Place> = emptyList()

    private val placeRepository by lazy { PlaceRepository }

    // NaviPagerAdapter 참조를 위한 변수
    private lateinit var parentAdapter: NaviPagerAdapter

    private val mockLocation = Location("").apply {
        latitude = 37.308711 // 현재 위치의 위도
        longitude = 127.136805 // 현재 위치의 경도
    }

    companion object {
        fun newInstance(selectedValue: String): NaviRecommendFragment {
            val fragment = NaviRecommendFragment()
            val args = Bundle()
            args.putString("selectedValue", selectedValue)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_navi_recommend, container, false)

        recommendRecyclerView = rootView.findViewById(R.id.recommendRecyclerView)

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NaviPagerAdapter 참조 초기화
        parentAdapter = (requireActivity() as NaviActivity).viewPager_navi.adapter as NaviPagerAdapter

        // 현재 위치 가져오기
        val currentLocation = getCurrentLocation_navi()

        //
        val parent_activity = requireActivity() as? NaviActivity
        val spinnerValue = parent_activity?.getSelectedSpinnerValue()

        val distanceFilter = when (spinnerValue) {
            "ALL" -> Int.MAX_VALUE
            "5km 이내" -> 5
            "10km 이내" -> 10
            "15km 이내" -> 15
            "20km 이내" -> 20
            "30km 이내" -> 30
            "50km 이내" -> 50
            else -> 20
        }
        Log.d(TAG, "sssss : NaviRecommendFragment get Spinner distanceFilter: $distanceFilter")

        setupRecyclerView(currentLocation, distanceFilter, PlaceRepository.FilterMode.RECOMMENDED)

        //clearAndSetNewData()

    }
    private fun filterAndSortPlacesByDistance(
        placeList: List<Place>,
        currentLocation: Location,
        maxDistanceKm: Int
    ): List<Place> {
        // 거리 계산 및 정렬
        return placeList.map { place ->
            val placeLocation = Location("").apply {
                latitude = place.latitude
                longitude = place.longitude
            }

            val distance = currentLocation.distanceTo(placeLocation) // 거리 계산 (미터 단위)
            place.apply { this.distance = distance.toDouble() } // Place 객체에 distance 설정
        }.filter { it.distance!! / 1000 <= maxDistanceKm } // maxDistanceKm 이내로 필터링
    }

    fun setupRecyclerView(location: Location, distanceFilter: Int, filterMode: PlaceRepository.FilterMode = PlaceRepository.FilterMode.DISTANCE) {
        Log.d(TAG, "Setting up RecyclerView with filter mode: $filterMode and distance filter: $distanceFilter km")

        val places = placeRepository.getPlacesFilteredByDistance(
            context = requireContext(), // Context 전달
            currentLocation = location,
            distance = distanceFilter,
            filterMode = filterMode

        )

        // 1등 당첨 수 기준으로 정렬
        val sortedPlaces = places.sortedByDescending { it.firstPrizeCount }

        // 현재 위치 가져오기 및 권한 확인
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val placeList = PlaceRepository.getPlaces(requireContext()) // **데이터 가져오기
            val sortedPlaces = filterAndSortPlacesByDistance(
                placeList = placeRepository.getPlaces(requireContext()),
                currentLocation = mockLocation,
                maxDistanceKm = distanceFilter
            )

            // RecyclerView 초기화
            val placeAdapter = PlaceAdapter(sortedPlaces, this)
            recommendRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = placeAdapter
            }
            Log.d(TAG, "aaaaa-a ${sortedPlaces}")
            Log.d(TAG, "aaaaa-b ${placeAdapter}")
        } else {
            // 위치 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        }
    }

    private fun getCurrentLocation_navi(): Location {
        // 예: 사용자의 현재 위치를 반환 (임의의 Location 반환)
        return Location("dummyprovider").apply {
            latitude = 37.308711
            longitude = 127.136805
        }
    }

    override fun onItemClick(place: Place) {
        Log.d(TAG, "Place selected: ${place.name}(${place.latitude}, ${place.longitude})")

        // 선택된 장소 저장
        parentAdapter.setSelectedPlaceForTab0(place)

        // 선택된 Place 객체를 Bundle에 추가
        val result = Bundle().apply {
            putParcelable("selectedPlace", place) // Place 객체는 Parcelable 구현이 필요
        }

        // FragmentResult로 데이터를 전달
        parentFragmentManager.setFragmentResult("placeSelected", result)
    }

    private fun clearAndSetNewData_dep(newPlaces: List<Place>) {
        // 기존 데이터를 비우기
        val emptyList = emptyList<Place>()
        placeAdapter.updatePlaces(emptyList) // 어댑터의 데이터 초기화
        placeAdapter.notifyDataSetChanged() // UI 업데이트

        // 새로운 데이터로 설정
        placeAdapter.updatePlaces(newPlaces)
        placeAdapter.notifyDataSetChanged()
    }

    private fun clearAndSetNewData() {
        // 기존 데이터를 비우기
        val emptyList = emptyList<Place>()
        placeAdapter.updatePlaces(emptyList) // 어댑터의 데이터 초기화
        placeAdapter.notifyDataSetChanged() // UI 업데이트

    }

}