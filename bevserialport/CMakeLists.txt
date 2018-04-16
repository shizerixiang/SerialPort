# CMake的编译脚本配置文件
# 1. 标注需要支持的CMake最小版本
cmake_minimum_required(VERSION 3.4.1)

# 每次创建一个新的库，需要添加另一个 add_library() 脚本，每个add_library 脚本语句只能导入一个库
# 创建串口框架的库
add_library(serial_port
            SHARED
            src/main/cpp/SerialPort.c)
# 3. find_library 定义当前代码库需要依赖的系统或者第三方库文件
find_library( # Sets the name of the path variable.
              # 指定要查找的系统库, 给一个名字
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              # 真正要查找的liblog.so或者liblog.a
              log )
# 可以写多个 find_library

target_link_libraries( # Specifies the target library.
                       # add_library 生成的
                       serial_port

                       # Links the target library to the log library
                       # included in the NDK.
                       # find_library 找到的系统库
                       ${log-lib} )