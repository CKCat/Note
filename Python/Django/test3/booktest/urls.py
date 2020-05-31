#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : urls
# Created by ckcat on 12/27/19
from django.conf.urls import url
from booktest import views

urlpatterns = [
    url(r'^$', views.index),
    url(r'^delete(\d+)/$', views.show_args),
    url(r'^add(?P<id1>\d+)/', views.show_arg),
    url(r'^method_show/$', views.method_show),
    url(r'^show_regarg/$', views.show_reqarg),
    url(r'^index2/$', views.index2),
    url(r'^index3/$', views.index3),
    url(r'^json1/$', views.json1),
    url(r'^json2/$', views.json2),
    url(r'^red1/$', views.red1),
    url(r'^cookie_set/$', views.cookie_set),
    url(r'^cookie_get/$', views.cookie_get),
    url(r'^session_test/$', views.session_test),
    url(r'^session_read/$', views.session_read),
    url(r'^session_del/$', views.session_del),
    url(r'^del_session/$', views.del_session),
]