#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : urls
# Created by ckcat on 12/27/19
from django.conf.urls import url
from booktest import views

urlpatterns = [
    url(r'^$', views.index)
]