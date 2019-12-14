[toc]

# JNI简单的介绍

## 1. 静态方法
- 先编写java代码，然后编译生成.class文件。
- 使用java的工具程序javah，如`javah -o output.h packagename.classname`,这样他就会生成一个output.h的头文件，其中声明了对应的JNI层函数。

例如：
java层`android.media.MediaScanner.native_init`对应的JNI函数名为`java_android_media_MediaScanner_native_1init`。可以发现JNI层的函数名将java函数名的`.`替换成了`_`,并且在函数前添加了`java_`的前缀。**值得注意的是如果java层函数名中有一个`_`,转换成JNI后就变成`_1(数字1)`了。**


## 2. 动态注册
### 1. JNINativeMethod结构
```
typedef struct{
    const char* name;       //java中native函数名字
    const char* signature;  //java层函数签名信息
    void* fnPtr;            //JNI层对应的函数指针
}
```

例如：
```
{
    "native_init";
    "()V";
    "(void*)android_media_MediaScanner_native_init" //函数指针，函数名可以随意
}
```
此结构体保存了了java native函数和JNI函数对应的关系，可以通过RegisterNative(env, className, gMethonds, numMethods)完成注册。

下面是使用JNI动态注册com.jni.MainActivity.MyAdd函数的使用例子：
```
JNINativeMethod g_NativeMethod[] = {
		"MyAdd", "(II)I;", (void*)MyAdd
};
JNIEXPORT JNICALL jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	//获取jniEnv
	JNIEnv *pEnv = NULL;
	int version = vm->GetEnv((void**)&pEnv, JNI_VERSION_1_4);
	if (pEnv == NULL)
	{
		return JNI_VERSION_1_4;
	}
    //通过java类名获取类对象
	jclass clsMainActivity = pEnv->FindClass("com/jni/MainActivity");
    //动态注册
	pEnv->RegisterNatives(clsMainActivity, g_NativeMethod,
			              sizeof(g_NativeMethod) / sizeof(g_NativeMethod[0]));

	return JNI_VERSION_1_4;
}
```
## 3. 数据类型
### 1.基本数据类型转换关系表
| java    | native类型 | 符号属性 | 字长  |
|---------|----------|------|-----|
| boolean | jboolean | 无符号  | 8位  |
| byte    | jbyte    | 无符号  | 8位  |
| char    | jchar    | 无符号  | 16位 |
| short   | jshort   | 有符号  | 16位 |
| int     | jint     | 有符号  | 32位 |
| long    | jlong    | 有符号  | 64位 |
| float   | jfloat   | 有符号  | 32位 |
| double  | jdouble  | 有符号  | 64位 |
**特别注意，jchar为16位的无符号数。**

### 2. 引用数据类型的转换关系
| java引用类型              | native类型      | java引用类型 | native类型     |
|-----------------------|---------------|----------|--------------|
| All object            | jobject       | char[]   | jcharArray   |
| java.lang.Class实例     | jclass        | short[]  | jshortArray  |
| java.lang.String实例    | jstring       | int[]    | jintArray    |
| Object[]              | jobjectArray  | long[]   | jlongArray   |
| boolean[]             | jbooleanArray | float[]  | floatArray   |
| byte[]                | jbyteArray    | double[] | jdoubleArray |
| java.lang.Throwable实例 | jthrowable    |          |              |

## 4. JNIEnv介绍
JNIEnv是一个与线程相关的代表JNI环境的结构体。调用JavaVM的AttachCurrentThread函数，就可以得到这个线程的JNIEnv结构体。退出线程前，需要调用JavaVM的DetachCurrentThread函数来释放对应的资源。

## 5. 通过JNIEnv操作jobject

### 1.jfieldID和jmethodID
```
//JNI中表示java类的成员变量
jfieldID GetFieldID(jclass clazz,   //java类实例
const char*name,    //成员变量名称
const char*sig);    //签名

//JNI中表示java类的成员函数
jmethodID GetMethodID（(jclass clazz,   //java类实例
const char*name,    //成员变量名称
const char*sig);    //签名
```

下面我们列出一些常用的Get/Set函数。
```
GetObjectField() SetObjectField() 
GetBooleanField() SetBooleanField() 
GetByteField() SetByteField() 
GetCharField() SetCharField() 
GetShortField() SetShortField() 
GetIntField() SetIntField() 
GetLongField() SetLongField() 
GetFloatField() SetFloatField() 
GetDoubleField() SetDoubleField() 
```

## 6. jstring介绍
Java中的String也是引用类型，不过由于它的使用频率较高，所以在JNI规范中单独创建了一个jstring类型来表示Java中的String类型。
jstring对象看成是Java中String对象在JNI层的代表，也就是说，jstring就是一个Java String。但由于Java String存储的是Unicode字符串，所以NewString函数的参数也必须是Unicode字符串。

## 7. JNI类型签名介绍
格式: `(参数标示1参数标示2...参数标示n)返回值标示`

例子:
```
void processFile(Stringpath,String mimeType);
对应的签名
(Ljava/lang/String;Ljava/lang/String;Landroid/media/MediaScannerClient;)V
```
常见来的类型标示:
| 类型标示 | java类型  | 类型标示                | java类型   |
|------|---------|---------------------|----------|
| Z    | boolean | F                   | float    |
| B    | byte    | D                   | double   |
| C    | char    | L/java/lang/String; | String   |
| S    | short   | [I                  | int[]    |
| I    | int     | [Ljava/lang/object; | Object[] |
| J    | long    |                     |          |

> java提供了一个叫javap的工具能帮助生成函数和变量的签名信息。

## 8. 垃圾回收

- Local Reference: 本地引用。在JNI层函数中使用的非全局引用对象都是Local Reference，它包括函数调用时传入的jobject和在JNI层函数中创建的jobject。Local Reference最大的特点就是，一旦JNI层函数返回，这些jobject就可能被垃圾回收
```
调用NewStringUTF创建一个jstring对象，它是Local Reference类型。
调用DeleteLocalRef回收jstring对象。
```
- Global Reference：全局引用，这种对象如不主动释放，它永远不会被垃圾回收。
```
调用NewGlobalRef创建一个Global Reference;
调用DeleteGlobalRef释放这个全局引用。
```
- Weak Global Reference：弱全局引用，一种特殊的Global Reference，在运行过程中可能会被圾回收。所以在使用它之前，需要调用JNIEnv的IsSameObject判断它是否被回收了。

> 平时用得最多的是Local Reference和Global Reference。

## 9. 异常处理
```
ExceptionOccured函数，用来判断是否发生异常。
ExceptionClear函数，用来清理当前JNI层中发生的异常。
ThrowNew函数，用来向Java层抛出异常。
```
下面封装了一个函数进行异常判断,并打印出异常信息。
```
bool IsException(JNIEnv *pEnv)
{
	if (pEnv->ExceptionCheck())
	{
		pEnv->ExceptionDescribe();
		pEnv->ExceptionClear();
		return true;
	}
	return false;
}
```
















