from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.conf import settings
from booktest.models import PicTest
from booktest.models import AreaInfo
from django.core.paginator import Paginator
# Create your views here.

def index(request):
    print("++==index==++")
    # raise Exception('自定义异常')
    return render(request, 'booktest/index.html')

def static_test(request):
    return render(request, 'booktest/static_test.html')

def pic_upload(request):
    return render(request, 'booktest/pic_upload.html')

def pic_handle(request):
    f1 = request.FILES.get("pic")
    fname = '%s/booktest/%s' % (settings.MEDIA_ROOT, f1.name)
    with open(fname, 'wb') as pic:
        for c in f1.chunks():
            pic.write(c)
    return HttpResponse("OK")

def pic_show(request):
    pic = PicTest.objects.get(pk=1)
    return render(request, 'booktest/pic_show.html', {'pic':pic})

def page_test(request, pIndex):
    # 查询所有的地区信息
    list_area = AreaInfo.objects.filter(aParent__isnull=True)
    # 将地区信息按一页10条进行分页
    p = Paginator(list_area, 10)
    if pIndex == '':
        pIndex = '1'
    pIndex = int(pIndex)
    # 获取第pIndex页的数据
    list_page = p.page(pIndex)
    # 获取所有页码信息
    plist = p.page_range
    # 将当前页码、当前页的数据、页码信息传递到模板中
    return render(request, 'booktest/page_test.html',{'list_page':list_page, 'plist':plist, 'pIndex':pIndex})


def area1(request):
    return render(request, 'booktest/area1.html')

def area2(request):
    list = AreaInfo.objects.filter(aParent__isnull=True)
    list2 = []
    for item in list:
        list2.append([item.id, item.atitle])
    return JsonResponse({'data': list2})

def area3(request, pid):
    list = AreaInfo.objects.filter(aParent_id= pid)
    list2 = []
    for item in list:
        list2.append([item.id, item.atitle])
    return JsonResponse({'data': list2})
