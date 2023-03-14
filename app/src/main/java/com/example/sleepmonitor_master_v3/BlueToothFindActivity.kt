package com.example.sleepmonitor_master_v3

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_blue_tooth_find.*
import kotlinx.coroutines.*
import java.util.*

class BlueToothFindActivity : AppCompatActivity() {
    private val characterUUIDNotify: String? = null
    private val descriptorUUID = "00002902-0000-1000-8000-00805f9b34fb" //BLE设备特性的UUID
    private var gasAdapter: GasAdapter? = null
    private var gasBagAdapter: GasBagAdapter? = null

    val job = Job()

    var adapter: BleAdapter? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    private val OPEN_RESULT_CODE: Int = 1001
    var bleScanner: BluetoothLeScanner? = null
    var selectPos: Int = -1

    //作为中央来使用和处理数据；
    private var mGatt: BluetoothGatt? = null

    //请求BLUETOOTH_SCAN权限意图
    var progressDialog: ProgressDialog? = null
    private val requestBluetoothScan =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            // 已经通过的权限列表
            val grantedList = it.filterValues { it }.mapNotNull { it.key }
            // 是否所有权限都通过
            val allGranted = grantedList.size == it.size
            if (allGranted) { // 所有权限都通过了
                bleScanner?.startScan(scanCallback)
            } else {
                ToastUtils.showToast("APP Bluetooth function requires BleScan permission for Android 12, do I need to open the setting page?")
            }

        }


    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.title).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_blue_tooth_find)
        progressDialog = ProgressDialog(this)

        WriteLogUtil.e(tvStatus.text.toString() + tvHeart.text + tvBreathing.text)

        adapter = BleAdapter()
        gasAdapter = GasAdapter()
        gasBagAdapter = GasBagAdapter()

        rvGas.adapter = gasAdapter
        rvGas.layoutManager = GridLayoutManager(this, 4)



        rvGasBag.adapter = gasBagAdapter
        rvGasBag.layoutManager = GridLayoutManager(this, 4)

        rvBle.adapter = adapter
        rvBle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var systemService = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = systemService.adapter
        if (bluetoothAdapter == null) {
            ToastUtils.showToast("The device does not support Bluetooth")
            return
        }
        bluetoothAdapter.let {
            if (!it!!.isEnabled) {
                val openBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(openBluetoothIntent, OPEN_RESULT_CODE)
            } else {
                ToastUtils.showToast("Bluetooth is turned on")
            }
        }

        bleScanner = bluetoothAdapter!!.bluetoothLeScanner
        adapter?.setOnItemClickListener { adapter, view, position ->


            if (selectPos != -1) {
                var viewByPosition = adapter.getViewByPosition(selectPos, R.id.cls)
                viewByPosition?.setBackgroundResource(R.drawable.shape_background)
            }
            selectPos = position
            var data = adapter.getItem(position) as ScanResult
            view.setBackgroundResource(R.drawable.shape_background_black)
            mGatt?.close()
            progressDialog!!.setMessage("Connecting....")
            progressDialog!!.show()
            data.device.connectGatt(this, true, gattCallback)
            if (bleScanner != null) {
                bleScanner?.stopScan(scanCallback)
            }
            job?.cancel()
            val scope = CoroutineScope(job)
            scope.launch {
                //处理具体逻辑
                delay(15000)
                progressDialog?.isShowing.let {
                    if (it == true) {
                        progressDialog!!.dismiss()
                        ToastUtils.showToast("connect time out")
                    }
                }
            }


        }


    }


    val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        /**
         * 连接回调
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            mGatt = gatt
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> { //已连接
                            gatt.discoverServices()
                            Log.e(TAG, "onConnectionStateChange: ")
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> { //断开连接
                            gatt.close()
                        }
                    }
                }
                else -> {
                    gatt.close()
                    runOnUiThread {
                        ToastUtils.showToast("connect fail")

                    }

                }
            }
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
            super.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val serviceList =
                    gatt!!.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dc4185")) //获取sevices uuid
                val characterList =
                    serviceList.getCharacteristic(UUID.fromString("00010203-0405-0607-0809-0a0b0c0dffc1")) // 获取特征
                if (characterList != null) {
                    setNotification(gatt, characterList)
                }
            }
            if (progressDialog!!.isShowing) {
                progressDialog?.dismiss()
            }
            runOnUiThread {
                rvBle.isVisible = false
                csl.visibility = View.GONE
                llair1.isVisible = true
            }



            ToastUtils.showToast("connect success")

            super.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.e(TAG, "onCharacteristicChanged: " + "123123123")

        }

        @SuppressLint("SetTextI18n")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            var value: String = DensityUtil.byte2hex(characteristic.value)
            Log.e(TAG, "onCharacteristicChanged: $value")
            Log.e(TAG, "onCharacteristicChanged: ${characteristic.value.size}")

            if (characteristic.value.size == 11) {
                runOnUiThread {
                    try {
                        var byteArrayOf = byteArrayOf(characteristic.value[3])
                        val ten = Integer.parseInt(DensityUtil.byte2hex(byteArrayOf), 16)
                        Log.e(TAG, "onCharacteristicChanged@@@@@@: $ten")
                        //心率
                        tvHeart.text = "Heart rate：${
                            Integer.parseInt(
                                DensityUtil.byte2hex(
                                    byteArrayOf(characteristic.value[5])
                                ), 16
                            )
                        }"

                        var breathing = Integer.parseInt(
                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[6])),
                            16
                        )

                        //呼吸
                        tvBreathing.text = "Breath：$breathing"

                        //睡眠状态
                        tvSleep.text = when (Integer.parseInt(
                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[4])),
                            16
                        )) {
                            0 -> "Sleep state: Invalid (off bed)" //离床（无效）

                            1 -> "Sleep state: Sober"//(清醒)

                            2 -> "Sleep state: Light Sleep"//（轻度睡眠）

                            3 -> "Sleep state: Moderate Sleep"//（中度睡眠）

                            else -> "Sleep state: Severe sleep"//（重度睡眠）
                        }
                        when (ten) {
//                            0 -> {
//                                tvStatus.text = "State：In bed"
//                            }
//                            1 -> {
//                                tvStatus.text = "State：Out of bed"
//                            }
//                            2 -> {
//                                tvStatus.text = "State：Body motion"
//                            }

                            3 -> {
                                tvStatus.text = "State：In bed"

                            }
                            4 -> {
                                tvStatus.text = "State：Out of bed"

                            }
                            5 -> {

                                tvStatus.text = "State：Snore"

                            }
                            6 -> {
                                tvStatus.text = "State：Body motion"

                            }
                        }
                        WriteLogUtil.e(tvStatus.text.toString() + " " + tvSleep.text + "  " + tvHeart.text + " " + tvBreathing.text)
                        /**
                         * 气阀状态
                         */
                        var gasStatus = Integer.parseInt(
                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[7])),
                            16
                        )
                        val maxBitIwannaCheck = 3
                        var mutableMapOf = mutableListOf<GasBean>()
                        for (i in 0..maxBitIwannaCheck) {
                            val standard = 1 shl i
                            mutableMapOf.add(
                                GasBean(
                                    false,
                                    gasStatus and standard > 0,
                                    "Air valve${i + 1}"
                                )
                            )
                        }
                        gasAdapter?.setList(mutableMapOf)

                        /**
                         * 气泵状态
                         */
                        var airp =
                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[8])).toInt(16)

//                        var airp = Integer.parseInt(
//                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[10])),
//                            16
//                        )
                        if (airp > 0) {
                            tvAirp.text = "Air pump：Start"
                        } else {
                            tvAirp.text = "Air pump：Not started"
                        }


                        /**
                         * 头枕位置
                         */
                        var gasBagStatus = Integer.parseInt(
                            DensityUtil.byte2hex(byteArrayOf(characteristic.value[10])),
                            16
                        )
                        val maxBitIwannaCheck1 = 3
                        var mutableMapOf1 = mutableListOf<GasBean>()

                        for (i in 0..maxBitIwannaCheck1) {
                            val standard = 1 shl i
                            mutableMapOf1.add(
                                GasBean(

                                    gasBagStatus and standard > 0,
                                    false,
                                    "Air valve${i + 1}"
                                )
                            )
                        }
                        gasBagAdapter?.setList(mutableMapOf1)
                    } catch (e: Exception) {
                        Log.e(TAG, "onCharacteristicChanged: " + e.message)
                    }
                }
            } else {

            }
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }


    private fun setNotification(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)

        val descriptor: BluetoothGattDescriptor = characteristic.getDescriptor(
            UUID.fromString(descriptorUUID)
        )
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)

    }

    private val scanCallback = object : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            ToastUtils.showToast("scan failed$errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.e(TAG, "onScanResult: ")
            if (result.device.name != null) {
                var data = adapter?.data
                data?.add(result)
                var distinctBy = data?.distinctBy { it.device.address }
                adapter?.setList(distinctBy)

                Log.e("TAG", "onScanResult: " + result.device.address)
                Log.e("TAG", "onScanResult: " + result.device.name)
            }


        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart: ")
    }


    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: ")

    }


    override fun onResume() {
        super.onResume()
        rvBle.isVisible = true
        csl.visibility = View.VISIBLE
        llair1.isVisible = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothScan.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            requestBluetoothScan.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }


    }

    override fun onStop() {
        super.onStop()
        if (bleScanner != null) {
            bleScanner?.stopScan(scanCallback)
        }
        mGatt?.close()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()

        }
        job.cancel()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            ToastUtils.showToast("Bluetooth is turned on")
        } else {
            ToastUtils.showToast("User rejected")
        }
    }


}