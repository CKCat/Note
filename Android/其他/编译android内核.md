[toc]

# 1. 下载内核源码
## 1. 在android源码目录中新建一个目录Kernel，进入kernel目录，执行下列命令下载内核源码
```
git clone https://aosp.tuna.tsinghua.edu.cn/kernel/msm.git
cd msm
git checkout -b android-msm-hammerhead-3.4-kitkat-mr2  origin/android-msm-hammerhead-3.4-kitkat-mr2
```

# 2. 设置环境
## 1. 进入android源码目录设置编译环境
```
cd ~/android-4.4.4_r1
source build/envsetup.sh  
lunch #选择13，编译hemmerhead  
export ANDROID_SRC_PATH=home/ckcat/android-4.4.4_r1 # 你的源码的位置
export PATH=$ANDROID_SRC_PATH/prebuilts/gcc/linux-x86/arm/arm-eabi-4.6/bin:$PATH   
export ARCH=arm  
export SUBARCH=arm  
export CROSS_COMPILE=arm-eabi- 
```

# 3. 切换到kernel内核源码所在目录，进行编译：
```
cd ../kernel-hammerhead/msm  
make hammerhead_defconfig  
make 
```
编译完内核后，内核生成的路径为：kernel/msm/arch/arm/boot目录下的zImage-dtb文件(nexus 5是这个文件，nexus 4是zImage文件，请注意别弄错了)。

# 4. 替换内核
把该文件复制到源码下的`device/lge/hammerhead-kernel`夹下（注意：nexus 4则为mako-kernel文件夹），覆盖掉同名文件。  编译内核：
```
make bootimage
```

# 5.碰到的一些问题

问题：在编译嵌入式Linux内核时出现了以下错误提示：`Can't use 'defined(@array)' (Maybe you should just omit the defined()?) at kernel/timeconst.pl line 373.`

解决方法：把`kernel/timeconst.pl` 文件中 373行的`if (!defined(@val))`改为`if (!@val)`后，编译成功。

参考：

[android4.4内核编译](https://blog.csdn.net/qq_34457594/article/details/53159103)

[Android系统内核编译及刷机实战 （修改反调试标志位）](https://blog.csdn.net/u012417380/article/details/73353670)