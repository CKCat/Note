from django.contrib import admin

# Register your models here.
from booktest.models import *

class GoodsInfoAdmin(admin.ModelAdmin):
    list_display = ['id']

admin.site.register(GoodsInfo, GoodsInfoAdmin)