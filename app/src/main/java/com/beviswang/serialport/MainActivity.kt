package com.beviswang.serialport

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beviswang.bevserialport.SerialPortFinder
import com.beviswang.bevserialport.operator.SPOperator
import com.beviswang.capturelib.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick

class MainActivity : AppCompatActivity(), PermissionHelper.OnRequestPermissionsResultCallbacks {
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?, isAllGranted: Boolean) {
        initData()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?, isAllDenied: Boolean) {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PermissionHelper.requestPermissions(this@MainActivity, 0x30,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE))
            initData()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun initData() {
        clickTxt.onClick {
            serialPortOperator()
        }
    }

    /** 串口操作 */
    private fun serialPortOperator() {
        try {
            // 首先获取串口设备的路径
            val deviceFile = SerialPortFinder().allDevicesPath ?: return
            deviceFile.forEach {
                // 打印并查看串口设备
                Log.e("TAG", "串口设备：$it")
            }
            if (deviceFile.isEmpty()) return
            // 通过串口设备的路径及指定的比特率对串口进行读写操作
            val spo = SPOperator.getSerialPort(deviceFile[0], 9600, 0)
            spo.readPort(onResult = { bytes ->
                // TODO 获取到数据后的操作
            })
            // 向串口设备写入数据
            spo.writePort(byteArrayOf(), onResult = { isSucceed ->
                // TODO 对结果进行检测
            })
            // 操作完成后需要关闭串口设备
            spo.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
