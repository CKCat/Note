from setuptools import setup, find_packages

setup(
    name = 'ckcatdemo',
    version = '0.0.2',
    keywords= 'demo',
    description = 'a demo',
    license = 'MIT License',
    url = 'https://github.com/ckcat',
    author='ckcat',
    author_email= 'ckcatck@qq.com',
    packages = find_packages(),
    install_requires = ['requests',],
)