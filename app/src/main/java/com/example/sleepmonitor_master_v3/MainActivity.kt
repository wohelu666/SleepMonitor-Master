package com.example.sleepmonitor_master_v3

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.util.Log
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.sleepmonitor_master_v3.utils.DataUtil
import com.example.sleepmonitor_master_v3.utils.SerializableMap
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_blue_tooth_find.*
import kotlinx.android.synthetic.main.activity_inbed.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private var isSnoring: Boolean = false
    var airp: Boolean = false
    var bleScanner: BluetoothLeScanner? = null
    var adapter: BleAdapter? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    var progressDialog: ProgressDialog? = null
    val timer: Timer? = Timer()
    var inbedList = HashMap<String, String>()
    var outOffbedList = HashMap<String, String>()
    var heartMap = HashMap<String, Int>()
    var bodyRem = HashMap<String, Int>()
    var lightWeight = HashMap<String, Int>()
    var lightCount: Int = 0

    private val descriptorUUID = "00002902-0000-1000-8000-00805f9b34fb" //BLE设备特性的UUID
    var inbedCount: Int = 1
    var outbedCount: Int = 1
    var bodyCount: Int = 0

    var sonreCount: Int = 1
    var totalHeart = 0
    var haveZero = mutableListOf<String>()
    var breath: Int? = 0
    var snoreList = HashMap<String, Int>()
    var interventionList = HashMap<String, Int>()
    var errorList = HashMap<String, Int>()
    var errorCount = 1
    var isConnect: Boolean = true
    var interventionCount = 1

    //作为中央来使用和处理数据；
    private var mGatt: BluetoothGatt? = null

    private val requestBluetoothScan =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            // 已经通过的权限列表
            val grantedList = it.filterValues { it }.mapNotNull { it.key }
            // 是否所有权限都通过
            val allGranted = grantedList.size == it.size
            if (allGranted) { // 所有权限都通过了
//                DataUtil.instance.bleScanner?.startScan(scanCallback)
                progressDialog!!.setMessage(getString(R.string.connecting))
                progressDialog!!.show()
            } else {
                ToastUtils.showToast(getString(R.string.not_support_12))
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        DataUtil.instance.initContext(this)
        DataUtil.instance.setDataListener(object : DataUtil.DataListener {
            override fun stringData(
                heart: Int?,
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
                runOnUiThread {
                    clsConnect?.isVisible = false
                    clsHome?.isVisible = true
                    tvRemOnce.text = scoreHour
                    tvRemMinute.text = scoreMinute
                    tvHour.text = scoreHour
                    tvMinute.text = scoreMinute
                    tvSnoreHour.text = snoreHour
                    tvSnoreMinute.text = snoreMiute
                    tvApneaOnce.text = heart.toString()
                    tvSitOnce.text = inBedTotal.toString()
                    tvOffbedOnce.text = offBedTotal.toString()
                    tvSleepOnce.text = remTotal.toString()
                    this@MainActivity.heartMap = heartMap
                }
                Log.e(TAG, "stringData: " + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            }

            override fun mapData(
                lightWeight: HashMap<String, Int>,
                inBed: HashMap<String, String>,
                outOffbedList: HashMap<String, String>,
                snoreList: HashMap<String, Int>,
                interventionList: HashMap<String, Int>,
                errorList: HashMap<String, Int>

            ) {
                Log.e(TAG, "mapData: " + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                this@MainActivity.inbedList = inBed
                this@MainActivity.outOffbedList = outOffbedList
                this@MainActivity.snoreList = snoreList
                this@MainActivity.interventionList = interventionList
                this@MainActivity.errorList = errorList
                this@MainActivity.lightWeight = lightWeight
            }
        })
//        var systemService = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
//        bluetoothAdapter = systemService.adapter
//        if (bluetoothAdapter == null) {
//            ToastUtils.showToast(getString(R.string.not_support))
//
//            finish()
//        }
//
//        bleScanner = bluetoothAdapter!!.bluetoothLeScanner
        tvConnect.setOnClickListener {
            DataUtil.instance.connect(MainActivity@ this)

        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(R.string.connecting))

//        inbedList["03:35"] = "56"
//        inbedList["04:24"] = "60"
//        inbedList["05:11"] = "76"
//        inbedList["06:23"] = "86"
//        outOffbedList["03:37"] = "1"
//        outOffbedList["04:34"] = "2"
//        outOffbedList["05:22"] = "1"
//        outOffbedList["07:30"] = "0"
//        snoreList["03:00"] = 56
//        snoreList["04:00"] = 60
//        snoreList["05:00"] = 76
//        snoreList["06:00"] = 86
//        interventionList["03:00"] = 16
//        interventionList["04:00"] = 20
//        interventionList["05:00"] = 36
//        interventionList["06:00"] = 46
//        errorList["03:00"] = 0
//        errorList["04:00"] = 0
//        errorList["05:00"] = 1
//        errorList["06:00"] = 2


    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        card2.setOnClickListener {

            var intent = Intent(this@MainActivity, InbedActivity::class.java)
            val serializableMap =
                SerializableMap(inbedList.toSortedMap(), outOffbedList.toSortedMap())
            val bundle = Bundle()
            bundle.putSerializable("bed", serializableMap)
            intent.putExtra("bed", bundle)
            intent.putExtra("bedhour", tvHour.text.toString())
            intent.putExtra("bedminute", tvMinute.text.toString())

            startActivity(intent)
        }
        card3.setOnClickListener {
            var intent = Intent(this@MainActivity, SnoreActivity::class.java)
            val serializableMap = SerializableMap(
                snoreList.toSortedMap(),
                interventionList.toSortedMap(),
                errorList.toSortedMap()
            )
            val bundle = Bundle()
            bundle.putSerializable("snore", serializableMap)
            intent.putExtra("snore", bundle)
            intent.putExtra("hour",tvSnoreHour.text.toString())
            intent.putExtra("minute",tvSnoreMinute.text.toString())

            startActivity(intent)

        }
        card4.setOnClickListener { view ->
            haveZero.distinctBy { it }.forEach {
                heartMap[it] = 0
            }
            val intent = Intent(this@MainActivity, ApneaActivity::class.java)
            var toSortedMap = heartMap.toSortedMap()
//            toSortedMap.put("13:46", 5485)
//            toSortedMap.put("13:47", 4200)
//            toSortedMap.put("13:48", 5025)
//            toSortedMap.put("13:49", 4122)
//            toSortedMap.put("13:50", 5003)
//            toSortedMap.put("13:51", 5560)
//            toSortedMap.put("13:52", 4085)
//            toSortedMap.put("13:53", 5231)
//            toSortedMap.put("13:54", 4305)
//            toSortedMap.put("13:55", 5315)
//            toSortedMap.put("13:56", 4325)
//            toSortedMap.put("13:57", 4335)
//            toSortedMap.put("13:58", 4345)
//            toSortedMap.put("14:00", 4385)
//            toSortedMap.put("14:01", 4385)
//            toSortedMap.put("14:02", 4385)
//            toSortedMap.put("14:03", 4385)
//            toSortedMap.put("14:04", 4120)
//            toSortedMap.put("14:05", 4128)
//            toSortedMap.put("14:06", 4385)
//            toSortedMap.put("14:07", 4005)
//            toSortedMap.put("14:08", 4605)
//            toSortedMap.put("14:09", 4285)
//            toSortedMap.put("14:10", 4185)
//            toSortedMap.put("14:11", 5100)
//            toSortedMap.put("14:12", 5200)
//            toSortedMap.put("14:13", 5300)
//            toSortedMap.put("14:14", 5400)
//            toSortedMap.put("14:15", 5500)

            val it: MutableIterator<MutableMap.MutableEntry<String, Int>> = toSortedMap.iterator()
            while (it.hasNext()) {
                val i = it.next()
                if (i.value < 3500 || i.value > 10000) {
                    it.remove()
                }
            }
            val serializableMap = SerializableMap(toSortedMap)
            val bundle = Bundle()
            bundle.putSerializable("heart", serializableMap)

            intent.putExtra("heart", bundle)
            intent.putExtra("breath", breath)
            startActivity(intent)

        }

        card5.setOnClickListener {
            var intent = Intent(this@MainActivity, SitupActivity::class.java)
            intent.putExtra("situp", tvSitOnce.text)
            startActivity(intent)

        }
        card6.setOnClickListener {
            var intent = Intent(this@MainActivity, OffBedActivity::class.java)
            intent.putExtra("offbed", tvOffbedOnce.text)
            startActivity(intent)

        }
        card7.setOnClickListener {
            var intent = Intent(this@MainActivity, BodyMovementActivity::class.java)
            intent.putExtra("body", bodyCount)
            startActivity(intent)

        }

        card8.setOnClickListener {
//            inbedList["03:00"] = "36"
//            inbedList["04:00"] = "20"
//            inbedList["05:00"] = "26"
//            inbedList["06:00"] = "20"
//            lightWeight["03:00"] = 10
//            lightWeight["04:00"] = 30
//            lightWeight["05:00"] = 24
//            lightWeight["07:00"] = 30
//            bodyRem["03:00"] = 14
//            bodyRem["04:00"] = 10
//            bodyRem["05:00"] = 10
//            bodyRem["07:00"] = 10
            var intent = Intent(this@MainActivity, RemActivity::class.java)
            val serializableMap =
                SerializableMap(
                    inbedList.toSortedMap(),
                    lightWeight.toSortedMap(),
                    bodyRem.toSortedMap(),
                    null
                )
            val bundle = Bundle()
            bundle.putSerializable("rem", serializableMap)
            intent.putExtra("rem", bundle)
            startActivity(intent)
        }
    }

    //    fun isBluetoothHeadsetConnected(): Boolean {
//        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled
//                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED)
//    }
//
//
//    val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
//        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//            mGatt = gatt
//            when (status) {
//                BluetoothGatt.GATT_SUCCESS -> {
//                    when (newState) {
//                        BluetoothProfile.STATE_CONNECTED -> { //已连接
//                            gatt.discoverServices()
//                            Log.e(TAG, "onConnectionStateChange: ")
//
//                        }
//                        BluetoothProfile.STATE_DISCONNECTED -> { //断开连接
//                            gatt.close()
//                        }
//                    }
//                }
//                else -> {
//                    gatt.close()
//                    runOnUiThread {
//                        ToastUtils.showToast("connect fail")
//                    }
//                }
//            }
//            if (progressDialog?.isShowing == true) {
//                progressDialog?.dismiss()
//            }
//            super.onConnectionStateChange(gatt, status, newState)
//        }
//
//        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                val serviceList =
//                    gatt!!.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dc4185")) //获取sevices uuid
//                val characterList =
//                    serviceList.getCharacteristic(UUID.fromString("00010203-0405-0607-0809-0a0b0c0dffc1")) // 获取特征
//                if (characterList != null) {
//                    setNotification(gatt, characterList)
//                }
//
//                runOnUiThread {
//                    clsConnect.isVisible = false
//                    clsHome.isVisible = true
//                }
//            }
//            super.onServicesDiscovered(gatt, status)
//
//        }
//
//        override fun onCharacteristicRead(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic?,
//            status: Int
//        ) {
//            super.onCharacteristicRead(gatt, characteristic, status)
//        }
//
//        override fun onCharacteristicWrite(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic?,
//            status: Int
//        ) {
//            super.onCharacteristicWrite(gatt, characteristic, status)
//        }
//
//        @RequiresApi(Build.VERSION_CODES.N)
//        override fun onCharacteristicChanged(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic
//        ) {
//            val value = DensityUtil.byte2hex(characteristic.value)
//            runOnUiThread {
//                clsConnect.isVisible = false
//                clsHome.isVisible = true
//            }
//            if (characteristic.value.size == 13) {
//
//                var heart = DensityUtil.byte2hex(byteArrayOf(characteristic.value[7])).toInt(16)
//                breath = DensityUtil.byte2hex(byteArrayOf(characteristic.value[8])).toInt(16) / 10
//                runOnUiThread {
//                    tvApneaOnce.text = if (breath == null) {
//                        "0"
//                    } else {
//                        breath.toString()
//
//                    }
//                    if (snoreList.isNotEmpty()) {
//                        var firstKeySnore = snoreList.toSortedMap().firstKey()
//                        var lastKeySnore = snoreList.toSortedMap().lastKey()
//                        try {
//                            tvSnoreHour.text = TimeUtil.timeReduce(
//                                TimeUtil.timeStrToSecond(firstKeySnore),
//                                TimeUtil.timeStrToSecond(lastKeySnore)
//                            ).split(":")[0]
//                            tvSnoreMinute.text = TimeUtil.timeReduce(
//                                TimeUtil.timeStrToSecond(firstKeySnore),
//                                TimeUtil.timeStrToSecond(lastKeySnore)
//                            ).split(":")[1]
//                        } catch (e: Exception) {
//
//                        }
//                    }
//                    var firstKey = inbedList.toSortedMap().firstKey()
//                    var lastKey = outOffbedList.toSortedMap().lastKey()
//                    try {
//                        tvHour.text = TimeUtil.timeReduce(
//                            TimeUtil.timeStrToSecond(firstKey),
//                            TimeUtil.timeStrToSecond(lastKey)
//                        ).split(":")[0]
//                        tvMinute.text = TimeUtil.timeReduce(
//                            TimeUtil.timeStrToSecond(firstKey),
//                            TimeUtil.timeStrToSecond(lastKey)
//                        ).split(":")[1]
//                    } catch (e: Exception) {
//
//                    }
//
//                    tvSitOnce.text = inbedList.values.sumOf { it.toInt() }.toString()
//                    tvOffbedOnce.text = outOffbedList.values.sumOf { it.toInt() }.toString()
//                    tvSleepOnce.text = bodyCount.toString()
//                    tvRemOnce.text = bodyCount.toString()
//                }
//
//                Log.e(TAG, "onCharacteristicChangedbreath: " + breath)
//
//                var i = heartMap[TimeUtil.getNowM()]
//                Log.e(TAG, "onCharacteristicChanged: " + i)
//                if (i == null) { //最新的一分钟未赋值，把总心率赋值为空
//                    totalHeart = 0
//                }
//                totalHeart += heart
//
//                heartMap[TimeUtil.getNowM()] = totalHeart
//
//                if (heart == 0) {
//                    haveZero.add(TimeUtil.getNowM())
//                }
//
////
//                Log.e("TAG", "onCharacteristicChangedheart: " + heart)
//                Log.e(TAG, "onCharacteristicChangedheart: $heartMap")
//                if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                        .toInt(16) == 2)
//                ) { //体动次数
//                    bodyRem[TimeUtil.getNowH()] = bodyCount++
//
//                }
//
//
//                if (isConnect) {
//                    isConnect = false
//                    timer?.schedule(object : TimerTask() { //上床离床次数
//                        override fun run() {
//                            if (DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                                    .toInt(16) == 4
//                            ) { // 重物
//
//                                lightWeight[TimeUtil.getNowH()] = lightCount++
//                            }
//                            if (DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                                    .toInt(16) == 0
//                            ) { // 在床次数
//
//                                inbedList[TimeUtil.getNowH()] = (inbedCount++).toString()
//                            }
//                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                                    .toInt(16) == 1)
//                            ) { //离床次数
//                                outOffbedList[TimeUtil.getNowH()] = (outbedCount++).toString()
//
//                            }
//
//
//                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                                    .toInt(16) == 5)
//                            ) { //打鼾开始时间
//
//                                snoreList[TimeUtil.getNowH()] = sonreCount++
//                            }
//                            Log.e(
//                                TAG,
//                                "onCharacteristicChanged: " + (DensityUtil.byte2hex(
//                                    byteArrayOf(characteristic.value[10])
//                                )
//                                    .toInt(16))
//                            )
//                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[10]))
//                                    .toInt(16) == 1) && (DensityUtil.byte2hex(
//                                    byteArrayOf(
//                                        characteristic.value[6]
//                                    )
//                                )
//                                    .toInt(16) == 5)
//                            ) { //气泵启动时间
//                                isSnoring = true
//                                airp = true
//                            }
//                            if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[9]))
//                                    .toInt(16) == 1) && isSnoring
//                            ) { //气阀启动,说明气阀工作 并且气泵也在工作 此时干预次数加1
//                                isSnoring = false
//                                interventionList[TimeUtil.getNowH() + ":00"] = interventionCount++
//
//                            } else if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[9]))
//                                    .toInt(16) == 0) && isSnoring && airp
//                            ) { //气阀未启动，气泵在工作，此时异常
//                                errorList[TimeUtil.getNowH() + ":00"] = errorCount++
//
//                            } else if ((DensityUtil.byte2hex(byteArrayOf(characteristic.value[6]))
//                                    .toInt(16) == 5) && (DensityUtil.byte2hex(
//                                    byteArrayOf(
//                                        characteristic.value[10]
//                                    )
//                                )
//                                    .toInt(16) == 0)
//                            ) { //打鼾，气泵未工作
//                                errorList[TimeUtil.getNowH() + ":00"] = errorCount++
//
//                            }
//                        }
//
//                    }, 0, 60000)
//                }
//            }
//            Log.e(TAG, "onCharacteristicChanged: $value")
////            Log.e(TAG, "onCharacteristicChanged: ${characteristic.value.size}")
//            super.onCharacteristicChanged(gatt, characteristic)
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        bleScanner?.stopScan(scanCallback)
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        DataUtil.instance.mGatt?.close()
//    }
//
//    private fun setNotification(gatt: BluetoothGatt, characterList: BluetoothGattCharacteristic) {
//        gatt.setCharacteristicNotification(characterList, true)
//
//        val descriptor: BluetoothGattDescriptor = characterList.getDescriptor(
//            UUID.fromString(descriptorUUID)
//        )
//        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//        gatt.writeDescriptor(descriptor)
//
//    }
//
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
//                    result.device.connectGatt(MyApp.context, true, gattCallback)

                    Log.e("TAG", "onScanResult: " + result.device.address)
                    Log.e("TAG", "onScanResult: $name")
                }
            }
        }
    }
}