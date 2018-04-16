## 串口通信工具 v.1.0.0
使用官方提供的串口工具进行串口通讯。

#### 注意：需要读写权限

### 添加依赖方式：
Step 1. 添加以下语句到你 Project 的 build.gradle 下
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. 添加依赖库到你 Module 的 build.gradle 下
```
dependencies {
        implementation 'com.github.shizerixiang:SerialPort:v1.0.0'
}
```

### Using Kotlin
```
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
```