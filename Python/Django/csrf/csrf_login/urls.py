from django.conf.urls import url
from csrf_login import views

urlpatterns = [
    url(r'^$', views.index),
    url(r'^post/$', views.post),

]