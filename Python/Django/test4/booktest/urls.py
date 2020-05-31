#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : url
# Created by ckcat on 1/4/20

from django.conf.urls import url
from booktest import views

urlpatterns = [
    url(r'^$', views.index),
    url(r'^temp_var/$', views.temp_var),
    url(r'^temp_tags/$', views.temp_tags),
    url(r'^temp_filter/$', views.temp_filter),
    url(r'^temp_inherit/$', views.temp_inherit),
    url(r'^html_escape/$', views.html_escape),
    url(r'^login/$', views.login),
    url(r'^login_check/$', views.login_check),
    url(r'^post/$', views.post),
    url(r'^post_action/$', views.post_action),
    url(r'^verify_code/$', views.verify_code),
    url(r'^verify_show/$', views.verify_show),
    url(r'^verify_yz/$', views.verify_yz),
    url(r'^verify_change/$', views.verify_change),
    url(r'^fan1/$', views.fan1),
    url(r'^fan_show/$', views.fan2, name='fan2'),
    url(r'^fan(\d+)_(\d+)/$', views.fan3, name='fan3'),
    url(r'^fan(?P<id>\d+)_(?P<age>\d+)/$', views.fan4, name='fan4'),
]