package com.example.sleepmonitor_master_v3.utils

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.sleepmonitor_master_v3.*
import com.example.sleepmonitor_master_v3.TimeUtil
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class DataUtil {
    private lateinit var requestBluetoothScan: ActivityResultLauncher<Array<String>>
    private var isSnoring: Boolean = false
    var airp: Boolean = false
    var isConnect: Boolean = true
    val timer: Timer? = Timer()
    var bodyRem = HashMap<String, Int>()
    var bleScanner: BluetoothLeScanner? = null
    var progressDialog: ProgressDialog? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    var context: Activity? = null
    var snoreList = HashMap<String, Int>()
    var snoreList1 = HashMap<String, Int>()

    var inbedList = HashMap<String, String>()
    var outOffbedList = HashMap<String, String>()
    var inbedList1 = HashMap<String, String>()
    var outOffbedList1 = HashMap<String, String>()
    var breath: Int? = null
    var scoreHour: String? = null
    var scoreMinute: String? = null
    var bodyCount: Int = 0
    var inBedTotal: Int = 0
    var offBedTotal: Int = 0
    var motionTotal: Int = 0
    var remTotal: Int = 0
    var snoreHour: String? = null
    var snoreMiute: String? = null
    var heartMap = HashMap<String, Int>()
    var totalHeart = 0
    var haveZero = mutableListOf<String>()
    var lightCount: Int = 0
    var lightWeight = HashMap<String, Int>()
    var inbedCount: Int = 0
    var outbedCount: Int = 0
    var sonreCount: Int = 0
    var sonreCount1: Int = 0
    private val OPEN_RESULT_CODE: Int = 1001

    var interventionList = HashMap<String, Int>()
    var errorList = HashMap<String, Int>()
    var errorCount = 0
    var interventionCount = 0
    private val descriptorUUID = "00002902-0000-1000-8000-00805f9b34fb" //BLE设备特性的UUID
    private var dataListener: DataListener? = null
    var mGatt: BluetoothGatt? = null
    var firstKeySnore: String? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.NONE) {
            DataUtil()
        }
    }


    fun initContext(context: ComponentActivity) {
        this.context = context
        progressDialog = ProgressDialog(context)
        var systemService =
            context.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = systemService.adapter
        if (bluetoothAdapter == null) {
            ToastUtils.showToast(context.getString(R.string.not_support))

            context.finish()
        }
        bluetoothAdapter.let {
            if (!it!!.isEnabled) {
                val openBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                context.startActivityForResult(openBluetoothIntent, OPEN_RESULT_CODE)
            } else {
                ToastUtils.showToast("Bluetooth is turned on")
            }
        }
        bleScanner = bluetoothAdapter!!.bluetoothLeScanner
        requestBluetoothScan =
            context.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
                // 已经通过的权限列表
                val grantedList = it.filterValues { it }.mapNotNull { it.key }
                // 是否所有权限都通过
                val allGranted = grantedList.size == it.size
                if (allGranted) { // 所有权限都通过了
                    bleScanner?.startScan(scanCallback)
                    progressDialog!!.setMessage(context?.getString(R.string.connecting))
                    progressDialog!!.show()
                } else {
                    ToastUtils.showToast(context?.getString(R.string.not_support_12))
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.parse("package:${context?.packageName}")
                    context?.startActivity(intent)
                }

            }

    }


    fun connect(activity: MainActivity) {
        requestPermission(activity)
    }

    /**
     * 请求权限
     */
    private fun requestPermission(activity: MainActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothScan.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH,

                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            requestBluetoothScan.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
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
            Log.e("TAG", "onScanResult: " + result.device.address)
            val name = result.device.name
            Log.e("TAG", "onScanResult: $name")

            if (name != null) {
                if (name.startsWith("LKBM")) {
                    result.device.connectGatt(MyApp.context, true, gattCallback)

                    Log.e("TAG", "onScanResult: " + result.device.address)
                    Log.e("TAG", "onScanResult: $name")
                }
            }
        }
    }

    private fun setNotification(gatt: BluetoothGatt, characterList: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characterList, true)

        val descriptor: BluetoothGattDescriptor = characterList.getDescriptor(
            UUID.fromString(descriptorUUID)
        )
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)

    }

    val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            mGatt = gatt
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> { //已连接
                            gatt.discoverServices()
                            Log.e(ContentValues.TAG, "onConnectionStateChange: ")

                        }
                        BluetoothProfile.STATE_DISCONNECTED -> { //断开连接
                            gatt.close()
                        }
                    }
                }
                else -> {
                    gatt.close()
                    context?.runOnUiThread {
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
                firstKeySnore = TimeUtil.getNowM()


            }
            super.onServicesDiscovered(gatt, status)

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = DensityUtil.byte2hex(characteristic.value)

            if (characteristic.value.size == 13) {
                /**
                 * 头枕位置
                 */
                var gasBagStatus = Integer.parseInt(
                    DensityUtil.byte2hex(byteArrayOf(characteristic.value[12])),
                    16
                )

                var gasStatus = Integer.parseInt(
                    DensityUtil.byte2hex(byteArrayOf(characteristic.value[9])),
                    16
                )
                Log.e(TAG, "onCharacteristicChangedgasStatus: "+ gasStatus )
                val maxBitIwannaCheck1 = 3
                var mutableMapOf1 = mutableListOf<GasBean>()

                for (i in 0..maxBitIwannaCheck1) {
                    val standard = 1 shl i
                    mutableMapOf1.add(
                        GasBean(
                            gasBagStatus and standard > 0,//头枕位置
                            gasStatus and standard > 0,//气阀状态
                            "Air valve${i + 1}"
                        )
                    )
                }

                var heart = DensityUtil.byte2hex(byteArrayOf(characteristic.value[7])).toInt(16)
                breath = DensityUtil.byte2hex(byteArrayOf(characteristic.value[8])).toInt(16) / 10
                context?.runOnUiThread {
//                    breath?(breath.toString())
                    if (snoreList1.isNotEmpty()) {
                        var firstSnore = snoreList1.toSortedMap().firstKey()

                        var lastKeySnore = snoreList1.toSortedMap().lastKey()
                        try {
                            snoreHour = (TimeUtil.timeReduce(
                                TimeUtil.timeStrToSecond(firstSnore),
                                TimeUtil.timeStrToSecond(lastKeySnore)
                            ).split(":")[0])
                            snoreMiute = (TimeUtil.timeReduce(
                                TimeUtil.timeStrToSecond(firstSnore),
                                TimeUtil.timeStrToSecond(lastKeySnore)
                            ).split(":")[1])
                        } catch (e: Exception) {

                        }
                    }
//                    try {
//                        var firstKey = inbedList.toSortedMap().firstKey()
//                        var lastKey = outOffbedList.toSortedMap().lastKey()

                    scoreHour = (TimeUtil.timeReduce(
                        TimeUtil.timeStrToSecond(firstKeySnore),
                        TimeUtil.timeStrToSecond(TimeUtil.getNowM())
                    ).split(":")[0])
                    scoreMinute = (TimeUtil.timeReduce(
                        TimeUtil.timeStrToSecond(firstKeySnore),
                        TimeUtil.timeStrToSecond(TimeUtil.getNowM())
                    ).split(":")[1])
//                    } catch (e: Exception) {
//
//                    }

                    inBedTotal = inbedList.values.sumOf { it.toInt() }
                    offBedTotal = outOffbedList.values.sumOf { it.toInt() }
                    motionTotal = bodyCount
                    remTotal = bodyCount
                }

                Log.e(ContentValues.TAG, "onCharacteristicChangedbreath: $breath")

                var i = heartMap[TimeUtil.getNowM()]
                Log.e(ContentValues.TAG, "onCharacteristicChanged: $i")
                if (i == null) { //最新的一分钟未赋值，把总心率赋值为空
                    totalHeart = 0
                }
                totalHeart += heart

                heartMap[TimeUtil.getNowM()] = totalHeart

                if (heart == 0) {
                    haveZero.add(TimeUtil.getNowM())
                }

//
                Log.e("TAG", "onCharacteristicChangedheart: $heart")
                Log.e(ContentValues.TAG, "onCharacteristicChangedheart: $heartMap")
                if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                        .toInt(16) == 2)
                ) { //体动次数
                    bodyRem[TimeUtil.getNowH()] = bodyCount + 1

                }

                dataListener?.stringData(
                    heart,
                    snoreHour,
                    snoreMiute,
                    scoreHour,
                    scoreMinute,
                    inBedTotal,
                    offBedTotal,
                    motionTotal,
                    remTotal,
                    heartMap,
                    bodyRem,
                    mutableMapOf1
                )
                if (isConnect) {
                    isConnect = false
                    timer?.schedule(object : TimerTask() { //上床离床次数
                        override fun run() {
                            if (DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                                    .toInt(16) == 4
                            ) { // 重物

                                lightWeight[TimeUtil.getNowH()] = ++lightCount
                            }
                            if (DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                                    .toInt(16) == 0
                            ) { // 在床次数

                                inbedList[TimeUtil.getNowH()] = (++inbedCount).toString()
                                inbedList1[TimeUtil.getNowM()] = (++inbedCount).toString()

                            }
                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                                    .toInt(16) == 1)
                            ) { //离床次数
                                outOffbedList[TimeUtil.getNowH()] = (++outbedCount).toString()
                                outOffbedList1[TimeUtil.getNowM()] = (++outbedCount).toString()

                            }


                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                                    .toInt(16) == 5)
                            ) { //打鼾开始时间
                                if (snoreList[TimeUtil.getNowH()] != 0) {
                                    snoreList[TimeUtil.getNowH()] = ++sonreCount
                                } else {
                                    sonreCount = 0
                                    snoreList[TimeUtil.getNowH()] = ++sonreCount

                                }
                                if (snoreList1[TimeUtil.getNowM()] != 0) {
                                    snoreList1[TimeUtil.getNowM()] = ++sonreCount1
                                } else {
                                    sonreCount1 = 0
                                    snoreList1[TimeUtil.getNowM()] = ++sonreCount1
                                }


                            }
                            Log.e(
                                ContentValues.TAG,
                                "onCharacteristicChanged: " + (DensityUtil.byte2hex(
                                    byteArrayOf(characteristic.value[10])
                                )
                                    .toInt(16))
                            )
                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[10]))
                                    .toInt(16) == 1) && (DensityUtil.byte2hex(
                                    byteArrayOf(
                                        characteristic.value[6]
                                    )
                                )
                                    .toInt(16) == 5)
                            ) { //气泵启动时间
                                isSnoring = true
                                airp = true
                            }
                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[9]))
                                    .toInt(16) == 1) && isSnoring
                            ) { //气阀启动,说明气阀工作 并且气泵也在工作 此时干预次数加1
                                isSnoring = false
                                interventionList[TimeUtil.getNowH()] = ++interventionCount

                            } else if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[9]))
                                    .toInt(16) == 0) && isSnoring && airp
                            ) { //气阀未启动，气泵在工作，此时异常
                                errorList[TimeUtil.getNowM()] = ++errorCount

                            } else if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
                                    .toInt(16) == 5) && (DensityUtil.byte2hex(
                                    byteArrayOf(
                                        characteristic.value[10]
                                    )
                                )
                                    .toInt(16) == 0)
                            ) { //打鼾，气泵未工作
                                errorList[TimeUtil.getNowM()] = ++errorCount

                            }
                            dataListener?.mapData(
                                lightWeight,
                                inbedList,
                                outOffbedList,
                                snoreList,
                                interventionList,
                                errorList

                            )
                        }

                    }, 0, 60000)
                }
            }
            Log.e(ContentValues.TAG, "onCharacteristicChanged: $value")
//            Log.e(TAG, "onCharacteristicChanged: ${characteristic.value.size}")
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }

    public fun setDataListener(dataListener: DataListener) {
        this.dataListener = dataListener
    }

    public interface DataListener {
        public fun stringData(
            breath: Int?,
            snoreHour: String?,
            snoreMiute: String?,
            scoreHour: String?,
            scoreMinute: String?,
            inBedTotal: Int,
            offBedTotal: Int,
            motionTotal: Int,
            remTotal: Int,
            heartMap: HashMap<String, Int>,
            bodyRem: HashMap<String, Int>,
            mutableMapOf1: MutableList<GasBean>
        ) {

        }

        public fun mapData(
            lightWeight: HashMap<String, Int>,
            inBed: HashMap<String, String>,
            outOffbedList: HashMap<String, String>,
            snoreList: HashMap<String, Int>,
            interventionList: HashMap<String, Int>,
            errorList: HashMap<String, Int>

        ) {

        }
    }
}