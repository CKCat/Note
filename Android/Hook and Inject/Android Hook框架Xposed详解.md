
# 1 Introduction
## 1.1  概述
Xposed 是 GitHUB 上 rovo89 大大设计的一个针对 Android 平台的动态劫持项目，通过替换 `/system/bin/app_process` 程序控制 `zygote` 进程，使得 `app_process` 在启动过程中会加载 `XposedBridge.jar` 这个 jar 包，从而完成对系统应用的劫持。

Xposed 框架的基本运行环境如下：

* 因为 Xposed 工作原理是在 `/system/bin` 目录下替换文件，在 install 的时候需要 root 权限，但是运行时不需要 root 权限。
* 需要在 Android 4.0 以上版本的机器中

1. Xposed 在线资源可以参照： 
> http://forum.xda-developers.com/showthread.php?t=1574401
> https://github.com/rovo89

2. GitHub 上的 Xposed 资源梳理一下，可以这么分类：

* XposedBridge.jar ： XposedBridge.jar 是 Xposed 提供的 jar 文件，负责在 Native层与 FrameWork 层进行交互。 /system/bin/app_process 进程启动过程中会加载该 jar 包，其它的 Modules 的开发与运行都是基于该 jar 包的。
* Xposed ： Xposed 的 C++ 部分，主要是用来替换 /system/bin/app_process ，并为 XposedBridge 提供 JNI 方法。
* XposedInstaller ： Xposed 的安装包，负责配置 Xposed 工作的环境并且提供对基于 Xposed 框架的 Modules 的管理。
* XposedMods ：使用 Xposed 开发的一些 Modules ，其中 AppSettings 是一个可以进行权限动态管理的应用

## 1.2 Mechanism ：原理
### 1.2.1 Zygote
在 Android 系统中，应用程序进程都是由 Zygote 进程孵化出来的，而 Zygote 进程是由 Init 进程启动的。 Zygote 进程在启动时会创建一个 Dalvik 虚拟机实例，每当它孵化一个新的应用程序进程时，都会将这个 Dalvik 虚拟机实例复制到新的应用程序进程里面去，从而使得每一个应用程序进程都有一个独立的 Dalvik 虚拟机实例。

Zygote 进程在启动的过程中，除了会创建一个 Dalvik 虚拟机实例之外，还会将 Java 运行时库加载到进程中来，以及注册一些 Android 核心类的 JNI 方法来前面创建的 Dalvik 虚拟机实例中去。注意，一个应用程序进程被 Zygote 进程孵化出来的时候，不仅会获得 Zygote 进程中的 Dalvik 虚拟机实例拷贝，还会与 Zygote 一起共享 Java 运行时库 。这也就是可以将 XposedBridge 这个 jar 包加载到每一个 Android 应用程序中的原因。 XposedBridge 有一个私有的 Native （ JNI ）方法 hookMethodNative，这个方法也在 app_process 中使用。这个函数提供一个方法对象利用 Java 的 Reflection 机制来对内置方法覆写。具体的实现可以看下文的 Xposed 源代码分析。

### 1.2.2 Hook/Replace
Xposed  框架中真正起作用的是对方法的 hook 。在 Repackage 技术中，如果要对 APK 做修改，则需要修改 Smali 代码中的指令。而另一种动态修改指令的技术需要在程序运行时基于匹配搜索来替换 smali 代码，但因为方法声明的多样性与复杂性，这种方法也比较复杂。

在 Android 系统启动的时候， zygote 进程加载 XposedBridge 将所有需要替换的 Method 通过 JNI 方法 hookMethodNative 指向 Native 方法 xposedCallHandler ， xposedCallHandler 在转入 handleHookedMethod 这个 Java 方法执行用户规定的 Hook Func 。

XposedBridge 这个 jar 包含有一个私有的本地方法： hookMethodNative ，该方法在附加的 app_process 程序中也得到了实现。它将一个方法对象作为输入参数（你可以使用 Java 的反射机制来获取这个方法）并且改变 Dalvik 虚拟机中对于该方法的定义。它将该方法的类型改变为 native 并且将这个方法的实现链接到它的本地的通用类的方法。换言之，当调用那个被 hook 的方法时候，通用的类方法会被调用而不会对调用者有任何的影响。在 hookMethodNative 的实现中，会调用 XposedBridge中的handleHookedMethod这个方法来传递参数。 handleHookedMethod 这个方法类似于一个统一调度的 Dispatch 例程，其对应的底层的 C++ 函数是 xposedCallHandler 。而 handleHookedMethod 实现里面会根据一个全局结构 hookedMethodCallbacks 来选择相应的 hook 函数，并调用他们的 before, after 函数。

当多模块同时 Hook 一个方法的时候， Xposed 会自动根据 Module 的优先级来排序，调用顺序如下：
```
A.before -> B.before -> original method -> B.after -> A.after
```

# 2  源代码分析
## 2.1 Cpp 模块
该部分的源代码地址是： https://github.com/rovo89/Xposed ，其文件分类如下：

* app_main.cpp ：类似 AOSP 中的 `frameworks/base/cmds/app_process/app_main.cpp`，即 `/system/bin/app_process` 这个 zygote 真实身份的应用程序的源代码。关于 zygote 进程的分析可以参照 Android:AOSP&Core 中的 Zygote 进程详解。
* xposed.cpp ：提供给 app_main.cpp 的调用函数以及 XposedBridge 的 JNI 方法的实现。主要完成初始化工作以及 Framework 层的 Method 的 Hook 操作。
* xposed.h ， xposed_offsets.h ：头文件
  Xposed 框架中的 app_main.cpp 相对于 AOSP 的 app_main.cpp 中修改之处主要为区分了调用 runtime.start() 函数的逻辑。 Xposed 框架中的 app_main.cpp 在此处会根据情况选择是加载 XposedBridge 类还是 ZygoteInit 或者 RuntimeInit 类。而实际的加载 XposedBridge 以及注册 JNI 方法的操作发生在第四步： xposedOnVmCreated中。

1．包含 cutils/properties.h ，主要用于获取、设置环境变量， xposed.cpp 中需要将 XposedBridge 设置到 ClassPath 中。

2．包含了 dlfcn.h ，用于对动态链接库的操作。

3．包含了 xposed.h ，需要调用 xposed.cpp 中的函数，譬如在虚拟机创建时注册JNI 函数。

4．增加了 initTypePointers 函数，对于 Android SDK 大于等于 18 的会获取到 atrace_set_tracing_enabled 函数指针，在 Zygote 启动时调用。

5．AppRuntime 类中的 onVmCreated 函数中增加 xposedOnVmCreated 函数调用。

6．源代码中的 Log* 全部重命名为 ALog*, 所以 Logv 替换为 Alogv ，但是功能不变。

7．Main 函数开始处增加了大量的代码，但是对于 SDK 版本小于 16 的可以不用考虑。



### 2.1.1 Main 函数： zygote 入口
```
int main(int argc, char* const argv[])
{
  ...
  initTypePointers();
    /*该函数对于SDK>=18的会获取到atrace_set_tracing_enabled的函数指针，获取到的指针会在Zygote初始化过程中调用，函数定义见代码段下方*/
  ...
  xposedInfo();
    /*xposedInfo函数定义在xposed.cpp中，该函数主要获取一些属性值譬如SDK版本，设备厂商，设备型号等信息并且打印到Log文件中*/
    xposedEnforceDalvik();
    keepLoadingXposed = !isXposedDisabled() && !xposedShouldIgnoreCommand(className, argc, argv) && addXposedToClasspath(zygote);

    if (zygote) {
        runtime.start(keepLoadingXposed ? XPOSED_CLASS_DOTS : "com.android.internal.os.ZygoteInit",
                startSystemServer ? "start-system-server" : "");
    } else if (className) {
        // Remainder of args get passed to startup class main()
        runtime.mClassName = className;
        runtime.mArgC = argc - i;
        runtime.mArgV = argv + i;
        runtime.start(keepLoadingXposed ? XPOSED_CLASS_DOTS : "com.android.internal.os.RuntimeInit",
                application ? "application" : "tool");
    } 
  else 
  {
        fprintf(stderr, "Error: no class name or --zygote supplied.\n");
        app_usage();
        LOG_ALWAYS_FATAL("app_process: no class name or --zygote supplied.");
        return 10;
    }
}
void initTypePointers()
{
    char sdk[PROPERTY_VALUE_MAX];
    const char *error;
    property_get("ro.build.version.sdk", sdk, "0");
    RUNNING_PLATFORM_SDK_VERSION = atoi(sdk);
    dlerror();
    if (RUNNING_PLATFORM_SDK_VERSION >= 18) {
        *(void **) (&PTR_atrace_set_tracing_enabled) = dlsym(RTLD_DEFAULT, "atrace_set_tracing_enabled");
        if ((error = dlerror()) != NULL) {
            ALOGE("Could not find address for function atrace_set_tracing_enabled: %s", error);
        }
    }
}
```
上述代码中的keepLoadingXposed 变量主要用于判断是否需要继续加载Xposed 框架，其中 isXposedDisabled 、 xposedShouldIgnoreCommand 以及 addXposedToClasspath 都定义在 xposed.cpp 中。

### 2.1.2 keepLoadingXposed ：判断是否需要加载 XposedBridge  
```
bool isXposedDisabled() {
    // is the blocker file present?
    if (access(XPOSED_LOAD_BLOCKER, F_OK) == 0) {
        ALOGE("found %s, not loading Xposed\n", XPOSED_LOAD_BLOCKER);
        return true;
    }
    return false;
}
```
该函数通过读取 `/data/data/de.robv.android.xposed.installer/conf/disabled` 文件（Xposed 框架通过 XposedInstaller 管理，需要安装此 APK 文件），来判断 Xposed 框架是否被禁用，如果该文件存在，则表示禁用 Xposed 。

xposedShouldIgnoreCommand

为了避免Superuser类似工具滥用Xposed的log文件，此函数会判断是否是SuperUser等工具的启动请求。
```
// ignore the broadcasts by various Superuser implementations to avoid spamming the Xposed log
bool xposedShouldIgnoreCommand(const char* className, int argc, const char* const argv[]) {
    if (className == NULL || argc < 4 || strcmp(className, "com.android.commands.am.Am") != 0)
        return false;
    if (strcmp(argv[2], "broadcast") != 0 && strcmp(argv[2], "start") != 0)
        return false;
    bool mightBeSuperuser = false;
    for (int i = 3; i < argc; i++) {
        if (strcmp(argv[i], "com.noshufou.android.su.RESULT") == 0
         || strcmp(argv[i], "eu.chainfire.supersu.NativeAccess") == 0)
            return true;
        if (mightBeSuperuser && strcmp(argv[i], "--user") == 0)
            return true;
        char* lastComponent = strrchr(argv[i], '.');
        if (!lastComponent)
            continue;
        if (strcmp(lastComponent, ".RequestActivity") == 0
         || strcmp(lastComponent, ".NotifyActivity") == 0
         || strcmp(lastComponent, ".SuReceiver") == 0)
            mightBeSuperuser = true;
    }
    return false;
}
```

addXposedToClasspath

若有新版本的XposedBridge，重命名为XposedBridge.jar并返回false;判断XposedBridge.jar文件是否存在，若不存在，返回false，否则将XposedBridge.jar添加到CLASSPATH环境变量中，返回true。

### 2.1.3 runtime.start() ：初始化 Dalvik 虚拟机
一般情况下keepLoadingXposed值为true，以启动Zygote为例(zygote==true)，分析接下来的代码。
```
runtime . start ( keepLoadingXposed  ?  XPOSED_CLASS_DOTS  :   "com.android.internal.os.RuntimeInit" ,

                application  ?   "application"   :   "tool" );
```
这一行代码是根据 keepLoadingXposed 的值来判断是加载 Xposed 框架还是正常的 ZygoteInit 类。 keepLoadingXposed 值为 true, 则会加载 XPOSED_CLASS_DOTS 类， XPOSED_CLASS_DOTS 值为 de.robv.android.xposed.XposedBridge ，即 XposedBridge 类。

runtime 是 AppRuntime 的实例， AppRuntime 继承自 AndroidRuntime 。
```
......
static AndroidRuntime* gCurRuntime = NULL;
......
AndroidRuntime::AndroidRuntime()
{
  ......
  assert(gCurRuntime == NULL);        // one per process
  gCurRuntime = this;
}
```
AndroidRuntime::start(const char* className, const char* options) 函数完成 Dalvik 虚拟机的初始化和启动以及运行参数 className 指定的类中的 main 方法。当启动完虚拟机后，会调用 onVmCreated(JNIEnv* env) 函数。该函数在 AppRuntime 类中被覆盖。因此直接看 AppRuntime::onVmCreated(JNIEnv* env) 。
```
virtual void onVmCreated(JNIEnv* env)
{
    keepLoadingXposed = xposedOnVmCreated(env, mClassName);
    if (mClassName == NULL) {
        return; // Zygote. Nothing to do here.
    }
    char* slashClassName = toSlashClassName(mClassName);
    mClass = env->FindClass(slashClassName);
    if (mClass == NULL) {
        ALOGE("ERROR: could not find class '%s'\n", mClassName);
    }
    free(slashClassName);
    mClass = reinterpret_cast<jclass>(env->NewGlobalRef(mClass));
}
```
Xposed 相对于 AOSP 就增加了如下代码：
```
keepLoadingXposed  =  xposedOnVmCreated ( env ,  mClassName );
```
调用了 xposed.cpp 中的 xposedOnVmCreated(JNIEnv* env, const char* className) 函数。

### 2.1.4 xposedOnVmCreated：加载Xposedbridge
该函数的主要作用如下：

1. 根据 JIT 是否存在对部分结构体中的成员偏移进行初始化。
```
xposedInitMemberOffsets ();
```
即时编译（ Just-in-time Compilation ， JIT ），又称动态转译（ Dynamic Translation ），是一种通过在运行时将 字节码 翻译为机器码，从而改善字节码 编译语言 性能的技术。

2. 禁用部分访问检查
```
// disable some access checks
    patchReturnTrue((uintptr_t) &dvmCheckClassAccess);
    patchReturnTrue((uintptr_t) &dvmCheckFieldAccess);
    patchReturnTrue((uintptr_t) &dvmInSamePackage);
    if (access(XPOSED_DIR "conf/do_not_hook_dvmCheckMethodAccess", F_OK) != 0)
        patchReturnTrue((uintptr_t) &dvmCheckMethodAccess);
```
3. 针对 MIUI 操作系统移除 android.content.res.MiuiResources 类的 final 修饰符
```
jclass miuiResourcesClass  =  env -> FindClass ( MIUI_RESOURCES_CLASS );

    if   ( miuiResourcesClass  !=  NULL )   {

    ClassObject *  clazz  =   ( ClassObject *) dvmDecodeIndirectRef ( dvmThreadSelf (),  miuiResourcesClass );

        if   ( dvmIsFinalClass ( clazz ))   {

        ALOGD ( "Removing final flag for class '%s'" ,  MIUI_RESOURCES_CLASS);

        clazz -> accessFlags  &=   ~ ACC_FINAL ;

        }

    }
```
4. 获取XposeBridge类并new一个全局引用
```
 xposedClass  =  env -> FindClass ( XPOSED_CLASS );

    xposedClass  =  reinterpret_cast < jclass >( env -> NewGlobalRef ( xposedClass ));

     if   ( xposedClass  ==  NULL )   {

        ALOGE ( "Error while loading Xposed class '%s':\n" ,  XPOSED_CLASS );

        dvmLogExceptionStackTrace ();

        env -> ExceptionClear ();

         return   false ;

     }
```
5. 注册JNI函数，xposed.cpp中定义了供XposedBridge类使用的JNI方法，此处进行注册，这样当XposeBridge中的main函数执行时，就可以调用xposed.cpp中定义的JNI方法
```
  if   ( register_de_robv_android_xposed_XposedBridge ( env )   !=  JNI_OK )   {

        ALOGE ( "Could not register natives for '%s'\n" ,  XPOSED_CLASS );

         return   false ;

     }
```

Xposed 中 JNI 方法有：
```
static const JNINativeMethod xposedMethods[] = {
    {"getStartClassName", "()Ljava/lang/String;", (void*)de_robv_android_xposed_XposedBridge_getStartClassName},
    {"initNative", "()Z", (void*)de_robv_android_xposed_XposedBridge_initNative},
    {"hookMethodNative", "(Ljava/lang/reflect/Member;Ljava/lang/Class;ILjava/lang/Object;)V", (void*)de_robv_android_xposed_XposedBridge_hookMethodNative},
};
static jobject de_robv_android_xposed_XposedBridge_getStartClassName(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF(startClassName);
}
static int register_de_robv_android_xposed_XposedBridge(JNIEnv* env) {
    return env->RegisterNatives(xposedClass, xposedMethods, NELEM(xposedMethods));
}
static const JNINativeMethod xresourcesMethods[] = {
    {"rewriteXmlReferencesNative", "(ILandroid/content/res/XResources;Landroid/content/res/Resources;)V", (void*)android_content_res_XResources_rewriteXmlReferencesNative},
};
static int register_android_content_res_XResources(JNIEnv* env) {
    return env->RegisterNatives(xresourcesClass, xresourcesMethods, NELEM(xresourcesMethods));
}
```
注册的 JNI 方法见 xposedMethods 数组。

至于这些 JNI 方法的用处，在后续 XposedBridge 的 main 函数调用中会继续分析。

如果对 Zygote 启动过程熟悉的话，对后续 XposedBridge 的 main 函数是如何被调用的应该会很清楚。 AndroidRuntime.cpp 的 start(const char* className, const char* options) 函数完成环境变量的设置， Dalvik 虚拟机的初始化和启动，同时 Xposed在 onVmCreated(JNIEnv* env) 中完成了自身 JNI 方法的注册。此后 start() 函数会注册Android 系统的 JNI 方法，调用传入的 className 指定类的 main 方法，进入 Java 世界。
```
void AndroidRuntime::start(const char* className, const bool startSystemServer)
{
  ......
  char* slashClassName = NULL;
  char* cp;
  JNIEnv* env;
  ......
  /* start the virtual machine */
  if (startVm(&mJavaVM, &env) != 0)
    goto bail;
  /*
  * Register android functions.
  */
  if (startReg(env) < 0) {
    LOGE("Unable to register all android natives\n");
    goto bail;
  }
  /*
  * We want to call main() with a String array with arguments in it.
  * At present we only have one argument, the class name.  Create an
  * array to hold it.
  */
  jclass stringClass;
  jobjectArray strArray;
  jstring classNameStr;
  jstring startSystemServerStr;
  stringClass = env->FindClass("java/lang/String");
  assert(stringClass != NULL);
  strArray = env->NewObjectArray(2, stringClass, NULL);
  assert(strArray != NULL);
  classNameStr = env->NewStringUTF(className);
  assert(classNameStr != NULL);
  env->SetObjectArrayElement(strArray, 0, classNameStr);
  startSystemServerStr = env->NewStringUTF(startSystemServer ?
    "true" : "false");
  env->SetObjectArrayElement(strArray, 1, startSystemServerStr);
  /*
  * Start VM.  This thread becomes the main thread of the VM, and will
  * not return until the VM exits.
  */
  jclass startClass;
  jmethodID startMeth;
  slashClassName = strdup(className);
  for (cp = slashClassName; *cp != '\0'; cp++)
    if (*cp == '.')
      *cp = '/';
  startClass = env->FindClass(slashClassName);
  if (startClass == NULL) {
    ......
  } else {
    startMeth = env->GetStaticMethodID(startClass, "main",
      "([Ljava/lang/String;)V");
    if (startMeth == NULL) {
      ......
    } else {
      env->CallStaticVoidMethod(startClass, startMeth, strArray);
      ......
    }
  }
  ......
}
```

由于此时参数 className 为 de.robv.android.xposed.XposedBridge ，因此会调用XposedBridge 类的 main 方法， XposedBridge 源码 https://github.com/rovo89/XposedBridge 。

## 2.2 Java 模块
### 2.2.1 Main 函数：获取所启动的类名
进入 XposedBridge 的 main 函数，首先获取所启动的类名。
```
String startClassName  =  getStartClassName ();

getStartClassName() 是一个 JNI 方法，定义在 xposed.cpp 中。

static  jobject de_robv_android_xposed_XposedBridge_getStartClassName ( JNIEnv *  env ,  jclass clazz )   {

     return  env -> NewStringUTF ( startClassName );

}
```
startClassName 变量在 xposedOnVmCreated 函数中被赋予需启动的类的名称。
```
bool xposedOnVmCreated ( JNIEnv *  env ,   const   char *  className )   {

    startClassName  =  className ;

...

}
```
但是需要注意的是，若是启动 Zygote ，则此时 startClassName 值为 null 。如下代码（ app_main.cpp ）所示，当 zygote 为 true ，即启动 Zygote 时，并没有给 AppRuntime 实例 runtime 的 mClassName 成员赋值。
```
if   ( zygote )   {

        runtime . start ( keepLoadingXposed  ?  XPOSED_CLASS_DOTS  :   "com.android.internal.os.ZygoteInit" ,

                startSystemServer  ?   "start-system-server"   :   "" );

     }   else   if   ( className )   {

         // Remainder of args get passed to startup class main()

        runtime . mClassName  =  className ;

        runtime . mArgC  =  argc  -  i ;

        runtime . mArgV  =  argv  +  i ;

        runtime . start ( keepLoadingXposed  ?  XPOSED_CLASS_DOTS  :   "com.android.internal.os.RuntimeInit" ,

                application  ?   "application"   :   "tool" );

}
```
startClassName 的赋值过程为： AppRuntime 中 mClassName 成员初始值为 NULL；在 app_main.cpp 中的 main 函数中根据 arg 参数解析获得 className ，若是启动Zygote ，则 className 值为 NULL ，否则若 className 有值，则赋值给 AppRuntime 实例 runtime 的 mClassName 成员变量；调用 runtime.start(…) ，进一步调用 onVmCreated(…) ，在 onVmCreated 函数中调用 xposedOnVmCreated(…) ，并传入 mClassName 值， xposedOnVmCreated 函数将 mClassName 赋值给全局变量 startClassName;

jobject de_robv_android_xposed_XposedBridge_getStartClassName(…) 将此全局变量 startClassName 转换为 Java 字符串返回。 

### 2.2.2 初始化 log 文件
XposedBridge 会在 XposedInstaller 的目录下生成 log 文件，该 log 文件的路径为： `/data/data/de.robv.android.xposed.installer/log/debug.log` 。 log 文件的初始化代码如下：
```
  // initialize the Xposed framework and modules

try   {

// initialize log file

try   {

File logFile  =   new  File ( BASE_DIR  +   "log/debug.log" );

if   ( startClassName  ==   null   &&  logFile . length ()   >  MAX_LOGFILE_SIZE )

logFile . renameTo ( new  File ( BASE_DIR  +   "log/debug.log.old" ));

logWriter  =   new  PrintWriter ( new  FileWriter ( logFile ,   true ));

logFile . setReadable ( true ,   false );

logFile . setWritable ( true ,   false );

}   catch   ( IOException ignored )   {}

String date  =  DateFormat . getDateTimeInstance (). format ( new  Date ());

determineXposedVersion ();

log ( "-----------------\n"   +  date  +   " UTC\n"

+   "Loading Xposed v"   +  XPOSED_BRIDGE_VERSION

+   " (for "   +   ( startClassName  ==   null   ?   "Zygote"   :  startClassName )   +   ")..." );

if   ( initNative ())   {

if   ( startClassName  ==   null )   {

// Initializations for Zygote

initXbridgeZygote ();

}

loadModules ( startClassName );

}   else   {

log ( "Errors during native Xposed initialization" );

}

}   catch   ( Throwable t )   {

log ( "Errors during Xposed initialization" );

log ( t );

disableHooks  =   true ;

}
```
若 startClassName==null 并且 log 文件的长度超过阈值，会将 debug.log 重命名为debug.log.old 。调用 determineXposedVersion() 获取 XposedBridge 的版本信息。版本信息存储在 XposedBridge 项目的 assets/VERSION 中。由于 XposedBridge 在 Android 设备上以 Jar 包的形式存在于 XposedInstaller 目录下，因此 determineXposedVersion 以读取 zip 文件的形式获取 VERSION 中的数据，并解析出其中的版本号，赋值给静态成员变量 XPOSED_BRIDGE_VERSION 。
```
ZipInputStream is  =   new  ZipInputStream ( new  FileInputStream ( BASE_DIR  +  "bin/XposedBridge.jar" ));

ZipEntry entry ;

try   {

while   (( entry  =  is . getNextEntry ())   !=   null )   {

if   (! entry . getName (). equals ( "assets/VERSION" ))

continue ;

BufferedReader br  =   new  BufferedReader ( new  InputStreamReader ( is ));

String version  =  br . readLine ();

br . close ();

XPOSED_BRIDGE_VERSION  =  extractIntPart ( version );

if   ( XPOSED_BRIDGE_VERSION  ==   0 )

throw   new  RuntimeException ( "could not parse XposedBridge version from \""  +  version  +   "\"" );

return ;

}

throw   new  RuntimeException ( "could not find assets/VERSION in "   +  BASE_DIR  +   "bin/XposedBridge.jar" );

}   finally   {

try   {

is . close ();

}   catch   ( Exception e )   {   }

}

}
```
### 2.2.3 获取对 Java 层函数的引用
Xposed 在进入 XposedBridge.main 函数之前，注册了 4 个 JNI 方法，其中一个是 initNative() ，这个函数负责获取 XposedBridge 中 Java 函数的引用。在完成 log 文件的初始化后， XposedBridge.main 调用 initNative 函数。
```
if   ( initNative ())   {

if   ( startClassName  ==   null )   {

// Initializations for Zygote

initXbridgeZygote ();

}

loadModules ( startClassName );

}   else   {

log ( "Errors during native Xposed initialization" );

}
```
现在回到 xposed.cpp 中，看下 initNative 这个 JNI 方法的实现。
```
static  jboolean de_robv_android_xposed_XposedBridge_initNative ( JNIEnv *  env,  jclass clazz )   {

...

    xposedHandleHookedMethod  =   ( Method *)  env -> GetStaticMethodID ( xposedClass ,   "handleHookedMethod" ,

         "(Ljava/lang/reflect/Member;ILjava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;" );

...

    xresourcesClass  =  env -> FindClass ( XRESOURCES_CLASS );

    xresourcesClass  =  reinterpret_cast < jclass >( env -> NewGlobalRef ( xresourcesClass ));

...

     if   ( register_android_content_res_XResources ( env )   !=  JNI_OK )   {

        ALOGE ( "Could not register natives for '%s'\n" ,  XRESOURCES_CLASS );

         return   false ;

     }

    xresourcesTranslateResId  =  env -> GetStaticMethodID ( xresourcesClass ,   "translateResId" ,

         "(ILandroid/content/res/XResources;Landroid/content/res/Resources;)I" );

...

    xresourcesTranslateAttrId  =  env -> GetStaticMethodID ( xresourcesClass ,   "translateAttrId" ,

         "(Ljava/lang/String;Landroid/content/res/XResources;)I" );

     ...

     return   true ;

}
```
该函数主要完成对 XposedBridge 类中函数的引用，这样可以实现在 Native 层对 Java 层函数的调用。 譬如获取 XposedBridge 类中的 handlHookedMethod 函数的 method id ，同时赋值给全局变量 xposedHandleHookedMethod 。另外， initNative 函数还会获取 android.content.res.XResources 类中的方法，完成对资源文件的处理；调用 register_android_content_res_XResources 注册 rewriteXmlReferencesNative 这个JNI 方法。

### 2.2.4 Hook ： Java 层获取 hooked method 与 hook func
在完成对 Java 层函数的引用赋值后，如果是启动 Zygote ，会接着执行对某些函数的 hook 处理。个人认为这部分是 Xposed 框架实现对函数 hook 的核心。代码如下：
```
if   ( startClassName  ==   null )   {

// Initializations for Zygote

initXbridgeZygote ();

}
```
initXbridgeZygote 完成对一些函数的 hook 操作，主要是调用 XposedHelpers 类中的 findAndHookMethod 完成。
```
private   static   void  initXbridgeZygote ()   throws  Exception  {

final  HashSet < String >  loadedPackagesInProcess  =   new  HashSet < String >(1 );

// normal process initialization (for new Activity, Service, BroadcastReceiver etc.) 

findAndHookMethod ( ActivityThread . class ,   "handleBindApplication" ,   "android.app.ActivityThread.AppBindData" ,   new  XC_MethodHook ()   {

protected   void  beforeHookedMethod ( MethodHookParam param )   throws  Throwable  {

...

}

}

}
```
以 hook ActivityThread 类的 handleBindApplication 函数为例来分析整个 hook 的过程。 ActivityThread 类定义在 frameworks/base/core/java/android/app/ActivityThread.java 文件中。 ActivityThread 的 main 函数是应用程序启动的入口。 findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback)(另一个重载的 findAndHookMethod 最终也会调用前述 findAndHookMethod 函数 ) 代码如下：
```
public   static  XC_MethodHook . Unhook findAndHookMethod ( Class <?>  clazz ,  String methodName ,  Object ...  parameterTypesAndCallback )   {

if   ( parameterTypesAndCallback . length  ==   0   ||   !( parameterTypesAndCallback [ parameterTypesAndCallback . length - 1 ]   instanceof  XC_MethodHook ))

throw   new  IllegalArgumentException ( "no callback defined" );

XC_MethodHook callback  =   ( XC_MethodHook )  parameterTypesAndCallback [parameterTypesAndCallback . length - 1 ];

Method m  =  findMethodExact ( clazz ,  methodName ,  parameterTypesAndCallback );

return  XposedBridge . hookMethod ( m ,  callback );

}
```
findAndHookMethod函数参数的意义分别为：

1. clazz: 需要hook的函数所在的类；

2. methodName: 需要hook的函数名；

3. parameterTypesAndCallback: 不定参数，包括methodName所指函数的参数，以及回调函数，主要是在执行methodName函数之前和之后调用的回调函数。
```
XC_MethodHook callback  =   ( XC_MethodHook )  parameterTypesAndCallback [parameterTypesAndCallback . length - 1 ];
```
这段代码是用来parameterTypesAndCallback 值为 [“android.app.ActivityThread.AppBindData”, XC_MethodHook 实例 ] ，因此 callback 值为其中的 XC_MethodHook实例。 XC_MethodHook 类关系图如下：


XC_MethodHook 类中的 beforeHookedMethod 函数会在被 hook 的函数调用之前调用，而 afterHookedMethod 函数会在被 hook 的函数调用之后调用。这两个函数的方法体为空，需要在实例化 XC_MethodHook 时根据情况填充方法体。 XC_MethodHook 的内部类 MethodHookParam 保存了相应的信息，如调用方法的参数， this 对象，函数的返回值等。
```
Method m  =  findMethodExact ( clazz ,  methodName ,  parameterTypesAndCallback );
```
本行代码根据需要 hook 的函数的类信息、函数名以及参数信息获取对应的 Method实例，同时将其设置为可访问。第 5 行调用的 findMethodExact 函数的签名为 Method  findMethodExact(Class<?> clazz, String methodName, Object... parameterTypes)，该函数最终会调用 Method findMethodExact(Class<?> clazz, String methodName, Class<?>.parameterTypes) 。在 findMethodExact(Class<?> clazz, String methodName, Class<?>... parameterTypes) 函数中，首先将类名、方法名以及参数信息构建成一个键值，以该键值从 methodCache 中查找是否存在 Method 实例， methodCache相当于缓存了对应的 Method 实例。如果没有找到，会调用 Class 类的 getDeclaredMethod(String name,Class<?>... parameterTypes) 方法获取 Method 实例，同时将该 Method 设置为可访问，加入到 methodCache 中。

接下来调用 XposedBridge 类的静态方法 hookMethod 实现对函数的 hook 和回调函数的注册，代码如下：
```
/**

 * Hook any method with the specified callback

 * 

 * @param hookMethod The method to be hooked

 * @param callback 

 */

public   static  XC_MethodHook . Unhook hookMethod ( Member hookMethod ,  XC_MethodHook callback )   {

if   (!( hookMethod  instanceof  Method )   &&   !( hookMethod  instanceof  Constructor <?>))   {

throw   new  IllegalArgumentException ( "only methods and constructors can be hooked" );

}

boolean  newMethod  =   false ;

CopyOnWriteSortedSet < XC_MethodHook >  callbacks ;

synchronized   ( hookedMethodCallbacks )   {

callbacks  =  hookedMethodCallbacks . get ( hookMethod );

if   ( callbacks  ==   null )   {

callbacks  =   new  CopyOnWriteSortedSet < XC_MethodHook >();

hookedMethodCallbacks . put ( hookMethod ,  callbacks );

newMethod  =   true ;

}

}

callbacks . add ( callback );

if   ( newMethod )   {

Class <?>  declaringClass  =  hookMethod . getDeclaringClass ();

int  slot  =   ( int )  getIntField ( hookMethod ,   "slot" );

Class <?>[]  parameterTypes ;

Class <?>  returnType ;

if   ( hookMethod  instanceof  Method )   {

parameterTypes  =   (( Method )  hookMethod ). getParameterTypes ();

returnType  =   (( Method )  hookMethod ). getReturnType ();

}   else   {

parameterTypes  =   (( Constructor <?>)  hookMethod ). getParameterTypes ();

returnType  =   null ;

}

           AdditionalHookInfo additionalInfo  =   new  AdditionalHookInfo ( callbacks , parameterTypes ,  returnType );

hookMethodNative ( hookMethod ,  declaringClass ,  slot ,  additionalInfo );

}

return  callback . new  Unhook ( hookMethod );

}
```
HookedMethodCallbacks 是一个 hashMap 的实例，存储每个需要 hook 的 method的回调函数。首先查看 hookedMethodCallbacks 中是否有 hookMethod 对应的 callbacks 的集合，如果没有，则创建一个 TreeSet ，将该 callbacks 加入到 hookedMethodCallbacks 中，同时将 newMethod 标志设为 true 。 接下来将传入的 callback 添加到callbacks 集合中。如果是个新的需要 hook 的 Method ，则获取对应的 Class 对象，得到 hookMethod 的 slot 值，然后调用 hookMethodNative 这个 JNI 方法。 最后实例化 Unhook 类，便于在后期需要对 hook 的方法进行 unhook 的操作。

### 2.2.5 Hook ： Native 层 hookMethodNative预处理Hook
进入 hookMethodNative 这个 JNI 方法（ xposed.cpp 中），看下这个方法具体做了哪些操作。
```
static   void  de_robv_android_xposed_XposedBridge_hookMethodNative ( JNIEnv*  env ,  jclass clazz ,  

            jobject  reflectedMethodIndirect ,

            jobject declaredClassIndirect ,  jint slot ,  jobject additionalInfoIndirect )   {

     if   ( declaredClassIndirect  ==   NULL   ||  reflectedMethodIndirect  ==   NULL )  {

        dvmThrowIllegalArgumentException ( "method and declaredClass must not be null" );

         return ;

     }

     // Find the internal representation of the method

    ClassObject *  declaredClass  =   ( ClassObject *)  dvmDecodeIndirectRef ( dvmThreadSelf (),  declaredClassIndirect );

    Method *  method  =  dvmSlotToMethod ( declaredClass ,  slot );

     if   ( method  ==   NULL )   {

        dvmThrowNoSuchMethodError ( "could not get internal representation for method" );

         return ;

     }

     if   ( xposedIsHooked ( method ))   {

         // already hooked

         return ;

     }

     // Save a copy of the original method and other hook info

    XposedHookInfo *  hookInfo  =   ( XposedHookInfo *)  calloc ( 1 ,   sizeof ( XposedHookInfo ));

    memcpy ( hookInfo ,  method ,   sizeof ( hookInfo -> originalMethodStruct ));

    hookInfo -> reflectedMethod  =  dvmDecodeIndirectRef ( dvmThreadSelf (),  env-> NewGlobalRef ( reflectedMethodIndirect ));

    hookInfo -> additionalInfo  =  dvmDecodeIndirectRef ( dvmThreadSelf (),  env -> NewGlobalRef ( additionalInfoIndirect ));

     // Replace method with our own code

    SET_METHOD_FLAG ( method ,  ACC_NATIVE );

    method -> nativeFunc  =   & xposedCallHandler ;

    method -> insns  =   ( const  u2 *)  hookInfo ;

    method -> registersSize  =  method -> insSize ;

    method -> outsSize  =   0 ;

     if   ( PTR_gDvmJit  !=   NULL )   {

         // reset JIT cache

        MEMBER_VAL ( PTR_gDvmJit ,  DvmJitGlobals ,  codeCacheFull )   =   true ;

     }

}
```
代码中首先获得 Dalvik 中对应的 ClassObject 以及 Method ，接下来判断需要 hook的 Method 是否已经被 hook 处理过，若处理过，则直接返回。所有被 hook 的 Method 都会保存在 xposedOriginalMethods 这个 list 中。对于新的需要 hook 的函数，首先将其添加到 xposedOriginalMethods 的列表中。
```
    SET_METHOD_FLAG ( method ,  ACC_NATIVE );

    method -> nativeFunc  =   & xposedCallHandler ;

    method -> insns  =   ( const  u2 *)  hookInfo ;

    method -> registersSize  =  method -> insSize ;

    method -> outsSize  =   0 ;
```
如上几行代码是 Xposed 框架实现 hook 的关键。 Dalvik 中 Method 结构体定义在AOSP 中 /dalvik/vm/oo/Object.h 中。 首先将 ACC_NATIVE 添加到 Method 的 accessFlags 标志位中 , 接下来将 Method 的 nativeFunc 设置为 xposedCallHandler 函数地址， 然后将 Method 的 registerSize 设置为 insSize ， 最后将 outsSize 设置为 0 。可参考 Dalvik 虚拟机的运行过程分析一文。 Dalvik 虚拟机在解释执行函数时，会调用 dvmIsNativeMethod(const Method* method)( 定义于 /dalvik/vm/oo/Object.h) 判断 Method 是否为 Native 方法，若是，则直接调用 Method->nativeFunc 指向的函数。那 dvmIsNativeMethod 又是如何判断一个 Method 是否为 Native 方法呢？代码如下：
```
INLINE  bool  dvmIsNativeMethod ( const  Method *  method )

{

return   ( method -> accessFlags  &  ACC_NATIVE )   !=   0 ;

}
```
正是通过比较 Method 的 accessFlags 与 ACC_NATIVE 来判断的，这也就是为什么14 行调用 SET_METHOD_FLAG 将 ACC_NATIVE 添加到 Method 的 accessFlags 中。 SET_METHOD_FLAG 代码如下：
```
#define SET_METHOD_FLAG(method,flag) \

do {( method ) _ > accessFlags  |=   ( flag );}   while ( 0 )
```
### 2.2.6 Native Hook ： Native Function 的 dispatcher  xposedCallHandler
进入 xposed.cpp 中的 xposedCallHandler 函数，该函数会作为被 hook 的函数的 Native 方法调用，代码如下：
```
static   void  xposedCallHandler ( const  u4 *  args ,  JValue *  pResult ,   const  Method *  method ,   :: Thread *  self )   {

     if   (! xposedIsHooked ( method ))   {

        dvmThrowNoSuchMethodError ( "could not find Xposed original method - how did you even get here?" );

         return ;

     }

    XposedHookInfo *  hookInfo  =   ( XposedHookInfo *)  method -> insns ;

    Method *  original  =   ( Method *)  hookInfo ;

    Object *  originalReflected  =  hookInfo -> reflectedMethod ;

    Object *  additionalInfo  =  hookInfo -> additionalInfo ;

     // convert/box arguments

     const   char *  desc  =   & method -> shorty [ 1 ];   // [0] is the return type.

    Object *  thisObject  =   NULL ;

    size_t srcIndex  =   0 ;

    size_t dstIndex  =   0 ;

     // for non-static methods determine the "this" pointer

     if   (! dvmIsStaticMethod ( original ))   {

        thisObject  =   ( Object *)  args [ 0 ];

        srcIndex ++;

     }

    ArrayObject *  argsArray  =  dvmAllocArrayByClass ( objectArrayClass ,  strlen (method -> shorty )   -   1 ,  ALLOC_DEFAULT );

     if   ( argsArray  ==   NULL )   {

         return ;

     }

     while   (* desc  !=   '\0' )   {

         char  descChar  =   *( desc ++);

        JValue value ;

        Object *  obj ;

         switch   ( descChar )   {

         case   'Z' :

         case   'C' :

         case   'F' :

         case   'B' :

         case   'S' :

         case   'I' :

            value . i  =  args [ srcIndex ++];

            obj  =   ( Object *)  dvmBoxPrimitive ( value ,  dvmFindPrimitiveClass ( descChar ));

            dvmReleaseTrackedAlloc ( obj ,  self );

             break ;

         case   'D' :

         case   'J' :

            value . j  =  dvmGetArgLong ( args ,  srcIndex );

            srcIndex  +=   2 ;

            obj  =   ( Object *)  dvmBoxPrimitive ( value ,  dvmFindPrimitiveClass ( descChar ));

            dvmReleaseTrackedAlloc ( obj ,  self );

             break ;

         case   '[' :

         case   'L' :

            obj   =   ( Object *)  args [ srcIndex ++];

             break ;

         default :

            ALOGE ( "Unknown method signature description character: %c\n" ,  descChar );

            obj  =   NULL ;

            srcIndex ++;

         }

        xposedSetObjectArrayElement ( argsArray ,  dstIndex ++,  obj );

     }

     // call the Java handler function

    JValue result ;

    dvmCallMethod ( self ,  xposedHandleHookedMethod ,   NULL ,   & result ,

        originalReflected ,   ( int )  original ,  additionalInfo ,  thisObject ,  argsArray );

    dvmReleaseTrackedAlloc ( argsArray ,  self );

     // exceptions are thrown to the caller

     if   ( dvmCheckException ( self ))   {

         return ;

     }

     // return result with proper type

    ClassObject *  returnType  =  dvmGetBoxedReturnType ( method );

     if   ( returnType -> primitiveType  ==  PRIM_VOID )   {

         // ignored

     }   else   if   ( result . l  ==   NULL )   {

         if   ( dvmIsPrimitiveClass ( returnType ))   {

            dvmThrowNullPointerException ( "null result when primitive expected" );

         }

        pResult -> l  =   NULL ;

     }   else   {

         if   (! dvmUnboxPrimitive ( result . l ,  returnType ,  pResult ))   {

            dvmThrowClassCastException ( result . l -> clazz ,  returnType );

         }

     }

}
```
第 4-7 获得被 hook 函数的 java.lang.reflect.Method 对象实例。第 9-52 行完成 Java 本地类型到 Java 类型的转换，也就是将被 hook 函数的参数转换为 Java 类型，为后续在 C++ 层调用 Java 层代码做准备。 55 行调用 XposedBridge 类中 handleHookedMethod 函数，参数分别为被 hook 的原始函数 Method 实例， this 对象以及参数信息。这也就是为什么有 9-52 行的参数转换操作。 58-72 行完成对返回值的进一步处理，主要是将 Java 层的返回值类型转换为 C++ 层的类型。 74 行将线程状态设置回调用 handleHookedMethod 之前的状态继续运行。

### 2.2.7 XposedBridge ： handleHookedMethod
handleHookedMethod 将被 hook 的代码又交还给 java 层实现。
```
private static Object handleHookedMethod(Member method, Object thisObject, Object[] args) throws Throwable {

if (disableHooks) {

try {

return invokeOriginalMethod(method, thisObject, args);

} catch (InvocationTargetException e) {

throw e.getCause();

}

}
```
首先判断 hook 是否被禁用，若是，则直接调用 invokeOriginalMethod 函数，完成对原始函数的执行。关于如何执行原始函数的，可以继续跟踪下去分析。
```
TreeSet < XC_MethodHook >  callbacks ;

synchronized  ( hookedMethodCallbacks )   {

callbacks  =  hookedMethodCallbacks . get ( method );

}

if   ( callbacks  ==  null  ||  callbacks . isEmpty ())   {

try   {

return  invokeOriginalMethod ( method ,  thisObject ,  args );

}   catch   ( InvocationTargetException e )   {

throw  e . getCause ();

}

}

synchronized  ( callbacks )   {

callbacks  =   (( TreeSet < XC_MethodHook >)  callbacks . clone ());

}
```
根据 method 值，从 hookedMethodCallbacks 中获取对应的 callback 信息。 hookedMethodCallbacks 的分析可以参考之前对 hookMethod 的分析。 callbacks 中存储了所有对该 method 进行 hook 的 beforeHookedMethod 和 afterHookedMethod 。接着从 callbacks 中获取 beforeHookedMethod 和 afterHookedMethod 的迭代器。
```
Iterator < XC_MethodHook >  before  =  callbacks . iterator ();

Iterator < XC_MethodHook >  after   =  callbacks . descendingIterator ();

// call "before method" callbacks

while   ( before . hasNext ())   {

try   {

before . next (). beforeHookedMethod ( param );

}   catch   ( Throwable t )   {

XposedBridge . log ( t );

// reset result (ignoring what the unexpectedly exiting callback did)

param . setResult ( null );

param . returnEarly  =   false ;

continue ;

}

if   ( param . returnEarly )   {

// skip remaining "before" callbacks and corresponding "after" callbacks

while   ( before . hasNext ()   &&  after . hasNext ())   {

before . next ();

after . next ();

}

break ;

}

}

// call original method if not requested otherwise

if   (! param . returnEarly )   {

try   {

param . setResult ( invokeOriginalMethod ( method ,  param . thisObject ,  param .args ));

}   catch   ( InvocationTargetException e )   {

param . setThrowable ( e . getCause ());

}

}

// call "after method" callbacks

while   ( after . hasNext ())   {

Object lastResult  =   param . getResult ();

Throwable lastThrowable  =  param . getThrowable ();

try   {

after . next (). afterHookedMethod ( param );

}   catch   ( Throwable t )   {

XposedBridge . log ( t );

// reset to last result (ignoring what the unexpectedly exiting callback did)

if   ( lastThrowable  ==  null )

param . setResult ( lastResult );

else

param . setThrowable ( lastThrowable );

}

}

// return

if   ( param . hasThrowable ())

throw  param . getThrowable ();

else

return  param . getResult ();
```
通过以上的分析，基本能够弄清楚 Xposed 框架实现 hook 的原理。 Xposed 将需要hook 的函数替换成 Native 方法 xposedCallHandler ，这样 Dalvik 在执行被 hook 的函数时，就会直接调用 xposedCallHandler ， xposedCallHandler 再调用 XposedBridge 类的 handleHookedMethod 完成注册的 beforeHookedMethod 以及 afterHookedMethod 的调用，这两类回调函数之间，会调用原始函数，完成正常的功能。

### 2.2.8  加载基于 Xposed 模块
继续回到 XposedBridge 的 main 函数中，在处理完对 hook 函数的处理后会调用 loadModules(String startClassName) 加载基于 Xposed 框架的模块。
```
if   ( initNative ())   {

if   ( startClassName  ==  null )   {

// Initializations for Zygote

initXbridgeZygote ();

}

loadModules ( startClassName );

}   else   {

log ( "Errors during native Xposed initialization" );

}
```
loadModules 读取 /data/data/de.robv.android.xposed.installer/conf/modules.list文件，获得 Android 设备上安装的模块的 APK 具体路径，若设备上安装了 XPrivacy，则 modules.list 文件内容为： /data/app/biz.bokhorst.xprivacy-1.apk 。 loadModules 对每个模块调用 loadMoudle ， loadModule 会根据提供的 APK 路径，实例化类，并根据实例的类型，进行一些初始化工作，主要的类型包括 IXposedHookZygoteInit， IXposedHookLoadPackage ， IXposedHookInitPackageResources ，和 IXposedHookCmdInit 。以 XPrivacy 模块为例， XPrivacy 类实现了 IXposedHookLoadPackage 和 IXposedHookZygoteInit 接口。如果是 IXposedHookZygoteInit ， loadModule会调用 initZygote(StartupParam startupParam) 函数。因此在分析基于 Xposed 框架的模块时，需要注意这点。
```
private   static   void  loadModules ( String startClassName )  throws IOException {

BufferedReader apks  =   new  BufferedReader ( new  FileReader ( BASE_DIR  +  "conf/modules.list" ));

String apk ;

while   (( apk  =  apks . readLine ())   !=  null )   {

loadModule ( apk ,  startClassName );

}

apks . close ();

}
```
### 2.2.9 调用 ZygoteInit.main 或 RuntimeInit.main
在 Xposed 的 app_main.cpp 中， runtime.start 调用了 XposedBridge 的 main 函数，对于 Zygote 启动过程来说，还必须完成对 ZygoteInit.main 函数的调用，完成类、资源的预加载以及对应用程序运行请求的处理。所以在 XposedBridge 完成自身的初始化之后，还需要完成对 ZygoteInit.main 的调用，如下代码所示。
```
// call the original startup code

if   ( startClassName  ==  null )

ZygoteInit . main ( args );

else

RuntimeInit . main ( args );
```
# 3 Developer Wiki
## 3.1  创建一个 Xposed Module
一个 XposedModule 本质上是设定了部分特殊元数据标志位的普通应用程序，需要在 AndroidManifest.xml 文件中添加如下设置：
```
  AndroidManifest.xml => Application => Application Nodes (at the bottom) => Add => Meta Data
```
添加节点： name = xposedmodule ， value = true 。 name = xposedminiversion, value = API level 。
```
<?xml version="1.0" encoding="utf-8"?>

<manifest   xmlns:android= "http://schemas.android.com/apk/res/android"

     package= "de.robv.android.xposed.mods.tutorial"

     android:versionCode= "1"

     android:versionName= "1.0"   >

     <uses-sdk   android:minSdkVersion= "15"   />

     <application

         android:icon= "@drawable/ic_launcher"

         android:label= "@string/app_name"   >

         <meta-data   android:value= "true"   android:name= "xposedmodule" />

         <meta-data   android:value= "2.0*"   android:name= "xposedminversion" />

         <meta-data   android:value= "Demonstration of the Xposed framework.\nMakes the status bar clock red."   android:name= "xposeddescription" />

     </application>

</manifest>
```
然后，将 XposedBridge.jar 这个引用导入到工程中，加入到 reference path 中。

下面开始创建一个新的工程：
```
package  com . kevin . myxposed ;

import  android . util . Log ;

import  de . robv . android . xposed . IXposedHookLoadPackage ;

import  de . robv . android . xposed . XposedBridge ;

import  de . robv . android . xposed . callbacks . XC_LoadPackage . LoadPackageParam ;

public   class  XposedInterface  implements  IXposedHookLoadPackage  {

public   void  handleLoadPackage ( final  LoadPackageParam lpparam )   throws  Throwable  {

        XposedBridge . log ( "Kevin-Loaded app: "   +  lpparam . packageName );

     }

}
```
然后在 assets 目录下新建一个 xposed_init 文件，这个文件声明了需要加载到 XposedInstaller 的入口类：
```
com.kevin.myxposed.XposedInterface
```
运行程序并在 XposedInstaller 的 Module 选项中激活，重启机器后可以得到如下数据：


## 3.2 HookedMethod ：定位你要 hook 的方法
在上一步中我们已经定位了需要 Hook 的方法以及所在的类，譬如：

com.android.systemui.statusbar.policy.Clock 类

中的 updateClock 方法。
```
package  de . robv . android . xposed . mods . tutorial ;

import   static  de . robv . android . xposed . XposedHelpers . findAndHookMethod;

import  de . robv . android . xposed . IXposedHookLoadPackage ;

import  de . robv . android . xposed . XC_MethodHook ;

import  de . robv . android . xposed . callbacks . XC_LoadPackage . LoadPackageParam ;

public   class  Tutorial  implements  IXposedHookLoadPackage  {

     public   void  handleLoadPackage ( final  LoadPackageParam lpparam )   throws  Throwable  {

     if   (! lpparam . packageName . equals ( "com.android.systemui" ))

             return ;

     findAndHookMethod ( "com.android.systemui.statusbar.policy.Clock" ,  lpparam . classLoader ,   "handleUpdateClock" ,   new  XC_MethodHook ()   {

     protected   void  beforeHookedMethod ( MethodHookParam param )   throws  Throwable  {

     // this will be called before the clock was updated by the original method

     }

     protected   void  afterHookedMethod ( MethodHookParam param )   throws  Throwable  {

     // this will be called after the clock was updated by the original method

     }

});

     }

}
```
关于findAndHookMethod 方法的说明见下面的 API Reference 。

## 3.3  进行资源替换
### 3.3.1  简易资源替换
下面所使用的方法可以适用于 Boolean 、 Color 、 Integer 、 int[] 、 String 与 String[] 。

其中，对于 Android 框架层的资源（所有的 APP 都需要调用的资源）应该在 initZygote 这个方法中完成替换。而对于属于应用程序的资源，应该在 hookInitPackageResources 这个方法中完成替换。
```
public   void  initZygote ( IXposedHookZygoteInit . StartupParam startupParam )   throws  Throwable  {

XResources . setSystemWideReplacement ( "android" ,   "bool" ,   "config_unplugTurnsOnScreen" ,   false );

}

public   void  handleInitPackageResources ( InitPackageResourcesParam resparam )   throws  Throwable  {

// replacements only for SystemUI

if   (! resparam . packageName . equals ( "com.android.systemui" ))

return ;

// different ways to specify the resources to be replaced

resparam . res . setReplacement ( 0x7f080083 ,   "YEAH!" );   // WLAN toggle text. You should not do this because the id is not fixed. Only for framework resources, you could use android.R.string.something

resparam . res . setReplacement ( "com.android.systemui:string/quickpanel_bluetooth_text" ,   "WOO!" );

resparam . res . setReplacement ( "com.android.systemui" ,   "string" ,   "quickpanel_gps_text" ,   "HOO!" );

resparam . res . setReplacement ( "com.android.systemui" ,   "integer" ,   "config_maxLevelOfSignalStrengthIndicator" ,   6 );

resparam . res . setReplacement ( "com.android.systemui" ,  

                     "drawable" ,   "status_bar_background" ,  

     new  XResources . DrawableLoader ()   {

public  Drawable newDrawable ( XResources res ,   int  id )   throws  Throwable  {

return   new  ColorDrawable ( Color . WHITE );

}

});

}
```
### 3.3.2  复杂资源
```
package  de . robv . android . xposed . mods . coloredcirclebattery ;

import  android . content . res . XModuleResources ;

import  de . robv . android . xposed . IXposedHookInitPackageResources ;

import  de . robv . android . xposed . IXposedHookZygoteInit ;

import  de . robv . android . xposed . callbacks . XC_InitPackageResources . InitPackageResourcesParam ;

public   class  ColoredCircleBattery  implements  IXposedHookZygoteInit ,  IXposedHookInitPackageResources  {

private   static  String MODULE_PATH  =   null ;

public   void  initZygote ( StartupParam startupParam )   throws  Throwable  {

MODULE_PATH  =  startupParam . modulePath ;

}

public   void  handleInitPackageResources ( InitPackageResourcesParam resparam )   throws  Throwable  {

if   (! resparam . packageName . equals ( "com.android.systemui" ))

return ;

XModuleResources modRes  =  XModuleResources . createInstance ( MODULE_PATH ,  resparam . res );

resparam . res . setReplacement ( "com.android.systemui" ,   "drawable" ,   "stat_sys_battery" ,  modRes . fwd ( R . drawable . battery_icon ));

resparam . res . setReplacement ( "com.android.systemui" ,   "drawable" ,   "stat_sys_battery_charge" ,  modRes . fwd ( R . drawable . battery_icon_charge ));

}

}
```
### 3.3.3  修改 layouts
```
public   void  handleInitPackageResources ( InitPackageResourcesParam resparam )   throws  Throwable  {

if   (! resparam . packageName . equals ( "com.android.systemui" ))

return ;

resparam . res . hookLayout ( "com.android.systemui" ,   "layout" ,   "status_bar" ,  new  XC_LayoutInflated ()   {

public   void  handleLayoutInflated ( LayoutInflatedParam liparam )   throws  Throwable  {

TextView clock  =   ( TextView )  liparam . view . findViewById (

liparam . res . getIdentifier ( "clock" ,   "id" ,   "com.android.systemui" ));

clock . setTextColor ( Color . RED );

}

});  

}
```
# 4 API Reference
## 关键类 /API 说明

### IXposedHookLoadPackage
 	 
 	
这个方法用于在加载应用程序的包的时候执行用户的操作。
```
public class XposedInterface implements IXposedHookLoadPackage {

public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        XposedBridge.log("Kevin-Loaded app: " + lpparam.packageName);
```
 final LoadPackageParam lpparam这个参数包含了加载的应用程序的一些基本信息。

 	 
### XposedHelpers
 	 
 	
这是一个辅助方法，可以通过如下方式静态导入：
```
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "handleUpdateClock", new XC_MethodHook() {

     protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

     // this will be called before the clock was updated by the original method

     protected void afterHookedMethod(MethodHookParam param) throws Throwable {

     // this will be called after the clock was updated by the original method

v findAndHookMethod(Class<?> clazz,   // 需要 Hook 的类名

      ClassLoader，   // 类加载器，可以设置为 null

      String methodName,    // 需要 Hook 的方法名

      Object... parameterTypesAndCallback
```
该函数的最后一个参数集，包含了：

（1）Hook 的目标方法的参数 , 譬如：

     "com.android.internal.policy.impl.PhoneWindow.DecorView"

     是方法的参数的类。

（ 2 ）回调方法：

     b.XC_MethodReplacement

 	 
## 辅助项 API
Xposed 框架也为我们提供了很多的辅助项来帮助我们快速开发 XposedModule 。

### XposedBridge 类
 	 
 	
该方法可以将 log 信息以及 Throwable 抛出的异常信息输出到标准的 logcat 以及 /data/xposed/debug.log这个文件中。

#### hookAllMethods / hookAllConstructors

该方法可以用来hook 某个类中的所有方法或者构造函数，但是不同的 Rom （非 Android 原生 Rom ）会有不同的变种。

### XposedHelpers 类
这个类用的也是比较多，可以使用

Window => Preferences => Java => Editor => Content Assist => Favorites => New Type, enter de.robv.android.xposed.XposedHelpers

这种方式将XposedHelpers 这个类加入到 Eclipse 静态调用中方便查阅。

 	 
#### findMethod / findConstructor / findField

这是一组用于检索方法的方法。

#### callMethod / callStaticMethod / newInstance

 
 	
以字节数组的形式返回 asset ，可以以如下方式调用：
```
public class XposedTweakbox {

private static final String MODULE_PATH = null; 

    // injected by XposedBridge

public static void init(String startClassName) throws Exception {

if (startClassName != null)

Resources tweakboxRes =

 XModuleResources.createInstance(MODULE_PATH, null);

byte[] crtPatch = assetAsByteArray(tweakboxRes, 

"crtfix_samsung_d506192d5049a4042fb84c0265edfe42.bsdiff");
```

返回对于一个文件的 MD5 校验值，需要 root权限。

获取一个进程的 PID 值，输入参数为 /proc/[pid]/cmdline