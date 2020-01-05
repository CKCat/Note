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
]