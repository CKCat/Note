# 1. 配置JAVA环境变量

为了配置JDK的系统变量环境，我们需要设置三个系统变量，分别是JAVA_HOME，Path和CLASSPATH。

## 1. JAVA_HOME
先设置这个环境变量名，将java安装目录的全路径作为变量值，如`D:\Program Files\Java\jdk1.8.0_121`。创建好后则可以利用`%JAVA_HOME%`作为JDK安装目录的统一引用路径。

## 2. Path
PATH属性已存在，可直接编辑，在原来变量后追加：`;%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin` 。

## 3. CLASSPATH 
设置系统变量名为：CLASSPATH  变量值为：`.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar` 。
注意变量值字符串前面有一个"."表示当前目录，设置CLASSPATH 的目的，在于告诉Java执行环境，在哪些目录下可以找到您所要执行的Java程序所需要的类或者包。


# 2. 配置android SDK工具环境变量

## 1. ANDROID_HOME
设置ANDROID_HOME这个环境变量名，将Android sdk的路径作为值，如`D:\Android\Sdk`。

## 2. path
在path变量的值后面追加：`%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\platforms\android-27`。

# 3. 配置NDK工具环境变量

## 1. ANDROID_NDK
设置ANDROID_NDK这个环境变量名，将Android ndk的路径作为值，如`D:\Android\Sdk\ndk-bundle`。

## 2. path
在path变量的值后面追加：`%ANDROID_NDK%\prebuilt\windows-x86_64\bin`

