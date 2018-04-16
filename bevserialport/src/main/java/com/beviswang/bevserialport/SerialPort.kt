/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.beviswang.bevserialport

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import android.util.Log

class SerialPort @Throws(SecurityException::class, IOException::class)
constructor(device: File, baudrate: Int, flags: Int) {

    /*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
    private val mFd: FileDescriptor?
    private val mFileInputStream: FileInputStream
    private val mFileOutputStream: FileOutputStream

    // Getters and setters
    val inputStream: InputStream
        get() = mFileInputStream

    val outputStream: OutputStream
        get() = mFileOutputStream

    init {
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                val su: Process = Runtime.getRuntime().exec("/system/bin/su")
                val cmd = ("chmod 666 " + device.absolutePath + "\n"
                        + "exit\n")
                su.outputStream.write(cmd.toByteArray())
                if (su.waitFor() != 0 || !device.canRead()
                        || !device.canWrite()) {
                    throw SecurityException()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw SecurityException()
            }

        }

        mFd = open(device.absolutePath, baudrate, flags)
        if (mFd == null) {
            Log.e(TAG, "本地串口打开方法返回值为空！！！")
            throw IOException()
        }
        mFileInputStream = FileInputStream(mFd)
        mFileOutputStream = FileOutputStream(mFd)
    }

    /**
     * 关闭方法
     */
    external fun close()

    // JNI
    /**
     * 打开串口
     *
     * @param path 串口路径
     * @param baudrate 比特率
     * @param flags 标志位
     */
    private external fun open(path: String, baudrate: Int, flags: Int): FileDescriptor?

    companion object {

        private val TAG = "SerialPort"

        init {
            System.loadLibrary("serial_port")
        }
    }
}
