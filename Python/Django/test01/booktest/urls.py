from django.conf.urls import url
from booktest import views

urlpatterns = [
    url(r'^$', views.index),
    # 配置详细页面url,小括号用于取值，传参
    url(r'^(\d+)/$', views.detail)
]