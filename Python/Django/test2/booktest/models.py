from django.db import models

# Create your models here.
#图书管理器
class BookInfoManager(models.Manager):
    def all(self):
        # 默认查询未删除的图书信息
        # 调用父类的成员语法为：super().方法名
        return super().all().filter(isDelete = False)

    # 创建模型类，接收参数为属性赋值
    def create_book(self, title, pub_date):
        # 创建模型类对象 selfs.models 可以获取模型类
        book = self.model()
        book.btitle = title
        book.bpub_date = pub_date
        book.bread = 0
        book.bcomment = 0
        book.isDelete = False
        #将数据插进数据表
        book.save()
        return book

# 定义图书模型类 BookInfo
class BookInfo(models.Model):
    # btitle = models.CharField(max_length=20) # 图书名称
    btitle = models.CharField(max_length=20, db_column='title') # 通过db_column指定btitle对应表格中字段的名字为title
    bpub_date = models.DateField() # 发布日期
    bread = models.IntegerField(default=0) # 阅读量
    bcomment = models.IntegerField(default=0) # 评论量
    isDelete = models.BooleanField(default=False) # 逻辑删除
    books = BookInfoManager()

# 定义英雄模型类 HeroInfo
class HeroInfo(models.Model):
    hname = models.CharField(max_length=20) #英雄名称
    hgender = models.BooleanField(default=True) #英雄性别
    isDelete = models.BooleanField(default=False) # 逻辑删除
    # hcomment = models.CharField(max_length=200) #英雄描叙信息
    # hcomment对应的数据库中的字段可以为空，但通过后台管理页面添加英雄信息时hcomment对应的输入框不能为空
    hcomment = models.CharField(max_length=200, null=True, blank=False)
    hbook = models.ForeignKey('BookInfo')# 英雄与图书为一对多关系


#定义地区模型类，存储省、市、区县信息
class AreaInfo(models.Model):
    atitle=models.CharField(max_length=30)#名称
    aParent=models.ForeignKey('self',null=True,blank=True)#关系