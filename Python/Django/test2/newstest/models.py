from django.db import models

# Create your models here.

class TypeInfo(models.Model):
    tname = models.CharField(max_length=20) #新闻类型

class NewsInfo(models.Model):
    ntitle = models.CharField(max_length=60) # 新闻标题
    ncontent = models.TextField() # 新闻内容
    npub_date = models.DateTimeField(auto_now_add=True) #新闻发布时间
    ntype = models.ManyToManyField('TypeInfo') #通过ManyToManyField建立TypeInfo类和NewsInfo类之间多对多的关系

