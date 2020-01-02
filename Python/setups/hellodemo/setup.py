from setuptools import setup, find_packages
import os


def read(fname):
    return open(os.path.join(os.path.dirname(__file__),fname)).read()

setup(
    name = 'hellodemo',
    version = '0.0.2',
    keywords= 'demo',
    description = 'a demo',
    long_description = read('README.md'),
    license = 'MIT License',
    url = 'https://github.com/ckcat',
    author='ckcat',
    author_email= 'ckcatck@qq.com',
    classifiers=[
        'Development Status ::demo',
        'topics:: demo',
        'programming language :: Python3:: Only',
    ],

    packages = find_packages(exclude=['tests']),
    install_requires = ['requests',],
)