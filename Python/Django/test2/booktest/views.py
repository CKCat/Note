from django.shortcuts import render, redirect

# Create your views here.
from booktest.models import *
from  datetime import date
from django.db.models import F
from django.db.models import Q
from django.db.models import Sum
# 查询所有图书并显示
# def index(request):
#     list = BookInfo.objects.all()
#     bklist =[]
#     for book in list:
#         if book.isDelete:
#             continue
#         bklist.append(book)
#     return render(request, 'booktest/index.html', {'list':bklist})

# 查询所有图书并显示
# def index(request):
#     # 查询没有被逻辑删除的图书,下列两种方式都可以
#     #bklist = BookInfo.objects.filter(isDelete__exact= 0)
#     bklist = BookInfo.objects.filter(isDelete = 0)
#     return render(request, 'booktest/index.html', {'list':bklist})

def index(request):
    bklist = BookInfo.objects.filter(btitle__contains = "传")
    # bklist = BookInfo.objects.filter(btitle__startswith = "射")
    # bklist = BookInfo.objects.filter(btitle__endswith =  "部")
    # bklist = BookInfo.objects.filter(btitle__isnull=False)
    # bklist = BookInfo.objects.filter(id__in=[1, 3, 5])
    # bklist = BookInfo.objects.filter(id__gt = 3)
    # bklist = BookInfo.objects.exclude(id=3)
    # bklist = BookInfo.objects.filter(bpub_date__year=1980)
    # bklist = BookInfo.objects.filter(bpub_date__gt=date(1980,1,1))
    # bklist = BookInfo.objects.filter(bread__gte=F('bcomment'))
    # bklist = BookInfo.objects.filter(bread__gt=F('bcomment')*2)
    # bklist = BookInfo.objects.filter(bread__gt=20, id__lt=3)
    # bklist = BookInfo.objects.filter(Q(bread__gt=20))
    # bklist = BookInfo.objects.filter(~Q(pk=3))
    # bklist = BookInfo.objects.aggregate(sum('bread'))
    # bklist = BookInfo.objects.filter(heroinfo__hcomment__contains='八')
    # hlist = HeroInfo.objects.filter(hbook__btitle="天龙八部")

    return render(request, 'booktest/index.html', {'list':bklist})

# 创建新图书
def create(request):
    book = BookInfo()
    book.btitle = '流星蝴蝶剑'
    book.bpub_date = date(1995, 12, 12)
    book.save()
    # 转向首页
    return redirect('/')

# 逻辑删除指定编号的图书
def delete(request, id):
    book = BookInfo.objects.get(id=int(id))
    book.isDelete = True
    book.save()
    # 转向首页
    return redirect('/')

def area(request):
    area = AreaInfo.objects.get(pk=440100)
    return render(request, 'booktest/area.html', {'area':area})
