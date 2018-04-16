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
import java.io.FileReader
import java.io.IOException
import java.io.LineNumberReader
import java.util.Vector

import android.util.Log

open class SerialPortFinder {

    private var mDrivers: Vector<Driver>? = null

    internal// Issue 3:
    // Since driver name may contain spaces, we do not extract driver name with split()
    var drivers: Vector<Driver>? = null
        @Throws(IOException::class)
        get() {
            if (mDrivers == null) {
                mDrivers = Vector()
                val r = LineNumberReader(FileReader("/proc/tty/drivers"))
                var l: String? = r.readLine()
                while (l != null) {
                    val drivername = l.substring(0, 0x15).trim { it <= ' ' }
                    val w = l.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (w.size >= 5 && w[w.size - 1] == "serial") {
                        Log.d(TAG, "Found new driver " + drivername + " on " + w[w.size - 4])
                        mDrivers!!.add(Driver(drivername, w[w.size - 4]))
                    }
                    l = r.readLine()
                }
                r.close()
            }
            return mDrivers
        }

    // Parse each driver
    val allDevices: Array<String>?
        get() {
            if (drivers == null) return null
            val devices = Vector<String>()
            val itdriv: Iterator<Driver>
            try {
                itdriv = drivers!!.iterator()
                while (itdriv.hasNext()) {
                    val driver = itdriv.next()
                    driver.devices!!
                            .map { it.name }
                            .mapTo(devices) { String.format("%s (%s)", it, driver.name) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return devices.toTypedArray()
        }

    // Parse each driver
    val allDevicesPath: Array<String>?
        get() {
            if (drivers == null) return null
            val devices = Vector<String>()
            val itdriv: Iterator<Driver>
            try {
                itdriv = drivers!!.iterator()
                while (itdriv.hasNext()) {
                    val driver = itdriv.next()
                    driver.devices!!.mapTo(devices) { it.absolutePath }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return devices.toTypedArray()
        }

    inner class Driver(val name: String, private val mDeviceRoot: String) {
        internal var mDevices: Vector<File>? = null

        var devices: Vector<File>? = null
            get() {
                if (mDevices == null) {
                    mDevices = Vector()
                    val dev = File("/dev")
                    val files = dev.listFiles()
                    var i = 0
                    while (i < files.size) {
                        if (files[i].absolutePath.startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + files[i])
                            mDevices!!.add(files[i])
                        }
                        i++
                    }
                }
                return mDevices
            }
    }

    companion object {

        private val TAG = "SerialPort"
    }
}
