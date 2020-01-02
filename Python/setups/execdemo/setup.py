from setuptools import setup, find_packages

setup(
    name = 'execdemo',
    version = '0.0.1',
    keywords ='exec demo',
    description = 'a exec demo',
    license = 'MIT license',
    
    author = 'ckcat',
    author_email = 'ckcatck@qq.com',
    url = 'https://github.com/ckcat',

    classifiers =[
        'Development Status ::exec demo',
        'License :: OSI Approved :: MIT License',
        'Programming Language:: Python3',
    ],

    packages = find_packages(), 
    install_requires =[],

    zip_safe = False,
    entry_points = {
        'console_scripts':[
            'execdemo = execdemo:main', # 设置好入口后可以直接通过execdemo 命令运行 execdemo:main 方法
        ],
    },
)