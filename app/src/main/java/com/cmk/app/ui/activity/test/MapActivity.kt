package com.cmk.app.ui.activity.test

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.LocationSource
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.model.MyLocationStyle
import com.cmk.app.R
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityMapBinding
import com.cmk.app.ext.toast
import com.cmk.app.util.permission.askPermissions
import com.permissionx.guolindev.PermissionX
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode




class MapActivity : BaseActivity() {

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        binding = ActivityMapBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.mapView.onCreate(savedInstanceState)
        binding.btLocation.setOnClickListener { getLocation() }
        initPermission()
    }

    //权限
    private fun initPermission() {

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .onExplainRequestReason { scope, deniedList ->
                val message = "APP需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "允许", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "去设置", "退出")
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {

                    haveLocationPermission = true
                } else {
                    haveLocationPermission = false
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                    System.exit(0)
                }
            }
    }

    private var haveLocationPermission = false

    private fun getLocation() {

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        //声明AMapLocationClient类对象
        val mLocationClient = AMapLocationClient(this)
        //声明定位回调监听器
        //val mLocationListener = AMapLocationListener() {}

        var mLocationOption = AMapLocationClientOption();
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn

        with(mLocationClient) {
            mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn

            setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            stopLocation();
            startLocation()
        };
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true;
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.isMockEnable = true;
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.httpTimeOut = 20000;
        //关闭缓存机制
        mLocationOption.isLocationCacheEnable = false;
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation()
        //设置定位回调监听
        mLocationClient.setLocationListener(MyLocationListener())
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.showMyLocation(true)
        myLocationStyle.interval(2000)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。

        binding.mapView.map.myLocationStyle = myLocationStyle
        binding.mapView.map.isMyLocationEnabled = true
//        binding.mapView.map.setLocationSource(object : LocationSource {
//            override fun activate(onLocationChangedListener: OnLocationChangedListener) {
//                //初始化定位
//                //初始化定位
//                mlocationClient = AMapLocationClient(this)
//                //初始化定位参数
//                //初始化定位参数
//                mLocationOption = AMapLocationClientOption()
//                //设置定位回调监听
//                //设置定位回调监听
//                mlocationClient.setLocationListener(this)
//                //设置为高精度定位模式
//                //设置为高精度定位模式
//                mLocationOption.locationMode = AMapLocationMode.Hight_Accuracy
//                //设置定位参数
//                //设置定位参数
//                mlocationClient.setLocationOption(mLocationOption)
//                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//                // 在定位结束后，在合适的生命周期调用onDestroy()方法
//                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//                // 在定位结束后，在合适的生命周期调用onDestroy()方法
//                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//                mlocationClient.startLocation() //启动定位
//
//            }
//            override fun deactivate() {
//
//            }
//        })
    }

    inner class MyLocationListener : AMapLocationListener {
        override fun onLocationChanged(p0: AMapLocation) {
            when (p0.errorCode) {
                0 -> {
                    Log.e(
                        "AmapError", "location Error, ErrCode:"
                                + p0.getErrorCode() + ", errInfo:"
                                + p0.getErrorInfo()
                    );
                }
            }

            val locationStr = p0.getCity() + p0.getDistrict() + p0.getStreet()

            Log.e("locationStr", locationStr)
            Log.e("经纬度:", "(${p0.latitude},${p0.longitude})")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        binding.mapView.onPause()
    }

}
