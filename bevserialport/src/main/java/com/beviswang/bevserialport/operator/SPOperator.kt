package com.beviswang.bevserialport.operator

import android.util.Log
import java.io.File
import java.io.IOException
import com.beviswang.bevserialport.SerialPort
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * 串口模块
 * Created by shize on 2018/1/8.
 */
class SPOperator(file: File, baudrate: Int, flags: Int) {
    // 串口
    private var mSerialPort = SerialPort(file, baudrate, flags)

    /**
     * 写入串口数据
     */
    fun writePort(data: ByteArray,onResult:((Boolean)->Unit)?) {
        doAsync {
            val mOutputStream = mSerialPort.outputStream
            try {
                mOutputStream.write(data)
                mOutputStream.flush()
                Log.i("SPOperator", "写入成功！")
                uiThread { onResult?.invoke(true) }
            } catch (e: IOException) {
                Log.e("SPOperator", "写入失败！")
                uiThread { onResult?.invoke(false) }
                e.printStackTrace()
            }
        }
    }

    /**
     * 读取串口数据
     *
     * @param onResult 回调接口
     */
    fun readPort(onResult: ((ByteArray?) -> Unit)) {
        doAsync {
            val size: Int
            val mInputStream = mSerialPort.inputStream
            try {
                val buffer = ByteArray(256)
                size = mInputStream.read(buffer)
                if (size > 0)
                    Log.i("SPOperator", "接收到的数据：" + byteArray2HexString(buffer))
                uiThread { onResult(buffer) }
                Log.i("SPOperator", "接收成功！")
            } catch (e: IOException) {
                Log.e("SPOperator", "接收失败！")
                uiThread { onResult(null) }
                e.printStackTrace()
            }
        }
    }

    /**
     * byte 数组转换为显示的字符串
     *
     * @param byteArray byte 数组
     */
    private fun byteArray2HexString(byteArray: ByteArray): String {
        var strBa = ""
        byteArray.forEach {
            var s = Integer.toHexString(it.toInt() and 0xFF)
            if (s.length < 2) s = "0$s"
            strBa += s
        }
        return strBa
    }

    /**
     * 关闭串口
     */
    fun close() {
        mSerialPort.close()
        INSTANCE = null
    }

    companion object {
        private var INSTANCE: SPOperator? = null
        /**
         * 获取串口
         * @param path 串口设备路径
         * @param baudrate 串口通讯比特率
         * @param flags 标志位
         */
        fun getSerialPort(path: String, baudrate: Int, flags: Int): SPOperator {
            if (INSTANCE == null) INSTANCE = SPOperator(File(path), baudrate, flags)
            return INSTANCE!!
        }
    }
}