
# 搭建Mobile-Security-Framework-MobSF平台

# 1. 下载Mobile-Security-Framework-MobSF
```
git clone https://github.com/MobSF/Mobile-Security-Framework-MobSF.git
```

# 2. 配置环境
## 1. 安装虚拟环境
```
pip3 install -U pip virtualenv
python -m virtualenv -p python ./venv
.\venv\Scripts\activate
```

## 2. 安装APKiD，此环境需要Visual Studio Build Tools 工具
```
pip install wheel
pip wheel --wheel-dir=yara-python --build-option="build" --build-option="--enable-dex" git+https://github.com/VirusTotal/yara-python.git@v3.10.0
pip install --no-index --find-links=yara-python yara-python
```

## 3. 安装依赖库
```
pip install -r requirements.txt
```

## 4. 迁移数据库
```
python manage.py makemigrations
python manage.py makemigrations StaticAnalyzer
python manage.py migrate
```

# 3. 动态环境安装
## 1. 安装
按照下列网址安装就好了
> https://github.com/MobSF/Mobile-Security-Framework-MobSF/wiki/11.-Configuring-Dynamic-Analyzer-with-MobSF-Android-4.4.2-x86-VirtualBox-VM

## 2.注意事项
* 设置WIFI代理时，务必在虚拟机获取到ip地址之后设置，然后再保存快照。
* 如果VirtualBox不是安装在C盘，无比设置VBOXMANAGE_BINARY的值，根据我的安装目录设置如下：
    ```
    VBOXMANAGE_BINARY = "D:/Program Files/Oracle/VirtualBox/VBoxManage.exe"
    ```

