from django.db import models

# Create your models here.

class AreaInfo(models.Model):
    atitle = models.CharField('标题',max_length=30)
    aParent = models.ForeignKey('self', null=True, blank=True)

    def title(self):
        return self.atitle
    title.short_description = '区域名称'

    def parent(self):
        if self.aParent is None:
            return ''
        return self.aParent.atitle
    parent.short_description = '父级区域名称'

    def __str__(self):
        return self.atitle

class PicTest(models.Model):
    pic = models.ImageField(upload_to='booktest/')