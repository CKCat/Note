from django.conf.urls import url

from booktest import views

urlpatterns = [
    url(r'^$', views.index),
    url(r'^static_test$', views.static_test),
    url(r'^pic_upload/$', views.pic_upload),
    url(r'^pic_handle/$', views.pic_handle),
    url(r'^pic_show/$', views.pic_show),
    url(r'^page(?P<pIndex>[0-9]*)/$', views.page_test),
    url(r'^area1/$', views.area1),
    url(r'^area2/$', views.area2),
    url(r'^area3_(\d+)/$', views.area3),
]