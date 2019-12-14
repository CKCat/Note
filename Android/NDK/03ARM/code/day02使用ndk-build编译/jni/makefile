
MODALE_NAME=hello

#安装程序到手机
install:
	adb push ../libs/armeabi/$(MODALE_NAME) /data/local/tmp
	adb shell chmod 755 /data/local/tmp/$(MODALE_NAME)
	adb shell /data/local/tmp/$(MODALE_NAME) 

debug:
	adb forward tcp:23946 tcp:23946
	adb shell /data/local/tmp/android_server