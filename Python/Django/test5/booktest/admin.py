from django.contrib import admin

# Register your models here.
from booktest.models import *

class AreaStackedInline(admin.StackedInline):
    model = AreaInfo    # 关联子对象
    extra = 2           # 额外编辑2个子对象

class AreaTabularInline(admin.TabularInline):
    model = AreaInfo    # 关联子对象
    extra = 2           # 额外编辑2个子对象

@ admin.register(AreaInfo)
class AreaAdmin(admin.ModelAdmin):
    list_per_page = 10
    actions_on_top = True
    actions_on_bottom = True
    list_display = ['id', 'atitle', 'title', 'parent']
    list_filter = ['atitle']
    search_fields = ['atitle']
    # fields = ['aParent', 'atitle']
    fieldsets = (
        ('基本', {'fields': ['atitle']}),
        ('高级', {'fields': ['aParent']}),
    )
    # inlines = [AreaStackedInline]
    inlines = [AreaTabularInline]

admin.site.register(PicTest)

# admin.site.register(AreaInfo, AreaAdmin)

