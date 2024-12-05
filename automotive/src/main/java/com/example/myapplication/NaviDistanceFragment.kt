package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView
import android.Manifest

class NaviDistanceFragment : Fragment(), PlaceAdapter.OnItemClickListener {

    private lateinit var distanceRecyclerView: RecyclerView

    private lateinit var naviAdapter: PlaceAdapter
    private lateinit var mapController: IMapController
    private var selectedPlace: Place? = null
    private var areImagesVisible = false
    private val mockLocation = Location("").apply {
        latitude = 37.308711 // 현재 위치의 위도
        longitude = 127.136805 // 현재 위치의 경도
    }

    private val placeRepository by lazy { PlaceRepository }

    // NaviPagerAdapter 참조를 위한 변수
    private lateinit var parentAdapter: NaviPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_navi_distance, container, false)

        distanceRecyclerView = rootView.findViewById(R.id.distanceRecyclerView)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NaviPagerAdapter 참조 초기화
        parentAdapter = (requireActivity() as NaviActivity).viewPager_navi.adapter as NaviPagerAdapter

        // 현재 위치 가져오기
        val currentLocation = getCurrentLocation_navi()

        val defaultDistanceFilter = 20

        setupRecyclerView_distance(currentLocation, defaultDistanceFilter, PlaceRepository.FilterMode.RECOMMENDED)


    }

    private fun filterAndSortPlacesByDistance(placeList: List<Place>, currentLocation: Location): List<Place> {
        // 거리 계산 및 정렬
        return placeList.map { place ->
            val placeLocation = Location("").apply {
                latitude = place.latitude
                longitude = place.longitude
            }

            val distance = currentLocation.distanceTo(placeLocation) // 거리 계산 (미터 단위)
            place.apply { this.distance = distance.toDouble() } // Place 객체에 distance 설정
        }.filter { it.distance!! / 1000 <= 10 } // 10km 이내 필터링
            .sortedBy { it.distance } // 거리로 오름차순 정렬
    }

    private fun setupRecyclerView_distance(location: Location, distanceFilter: Int = 20, filterMode: PlaceRepository.FilterMode = PlaceRepository.FilterMode.DISTANCE) {
        Log.d(TAG, "Setting up RecyclerView with filter mode: $filterMode and distance filter: $distanceFilter km")

        val places = placeRepository.getPlacesFilteredByDistance(
            context = requireContext(), // Context 전달
            currentLocation = location,
            distance = distanceFilter,
            filterMode = filterMode
        )
        Log.d(TAG, "distance" + places)
        // 1등 당첨 수 기준으로 정렬
        val sortedPlaces = places.sortedByDescending { it.firstPrizeCount }


        // RecyclerView에 어댑터 설정
        /*
        val placeAdapter = PlaceAdapter(places, this)
        distanceRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAdapter
            isVerticalScrollBarEnabled = true
        }

        Log.d(TAG, "Loaded ${places.size} places into RecyclerView")
        */

        // 현재 위치 가져오기 및 권한 확인
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val placeList = PlaceRepository.getPlaces(requireContext()) // **데이터 가져오기
            val sortedPlaces = filterAndSortPlacesByDistance(placeList, mockLocation)

            // RecyclerView 초기화
            val placeAdapter = PlaceAdapter(sortedPlaces, this)
            distanceRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = placeAdapter
            }
            Log.d(TAG, "bbbbb-a ${sortedPlaces}")
            Log.d(TAG, "bbbbb-b ${placeAdapter}")
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
        selectedPlace = place

        // 선택된 장소를 NaviPagerAdapter에 전달
        parentAdapter.setSelectedPlaceForTab1(place)

        // 선택된 Place 객체를 Bundle에 추가
        val result = Bundle().apply {
            putParcelable("selectedPlace", place) // Place 객체는 Parcelable 구현이 필요
        }

        // FragmentResult로 데이터를 전달
        parentFragmentManager.setFragmentResult("placeSelected", result)
    }

}
