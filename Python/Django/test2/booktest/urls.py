#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : urls
# Created by ckcat on 12/23/19

__author__ = 'ckcat'

from django.conf.urls import url
from booktest import views

urlpatterns=[
    url(r'^$', views.index),
    url(r'^delete(\d+)/$', views.delete),
    url(r'^create/$', views.create),
    url(r'^area/$', views.area)
]