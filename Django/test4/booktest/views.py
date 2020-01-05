from django.shortcuts import render, redirect, HttpResponse

# Create your views here.
from booktest.models import BookInfo


def index(request):
    return render(request, 'booktest/index.html')

def temp_var(request):
    book = BookInfo.objects.get(id=1)
    dic = {'title':'字典键值'}
    context = {'dic': dic, 'book': book}
    return render(request, 'booktest/temp_var.html', context)

def temp_tags(request):
    context = {"list":BookInfo.objects.all()}
    return render(request, 'booktest/temp_tags.html', context)

def temp_filter(request):
    context = {'list': BookInfo.objects.all()}
    return render(request, 'booktest/temp_filter.html', context)

def temp_inherit(request):
    context = {'title':'模板继承', 'list':BookInfo.objects.all()}
    return render(request, 'booktest/temp_inherit.html', context)

def html_escape(request):
    context = {'context': '<h1>Hello World</h1>'}
    return render(request, 'booktest/html_escape.html', context)

def login(request):
    return render(request, 'booktest/login.html')

def login_check(request):
    username = request.POST.get('username')
    password = request.POST.get('password')

    if username == 'ckcat' and password =='123456':
        request.session['username'] = username
        request.session['islogin'] = True
        return redirect('/post/')
    else:
        return redirect('/login/')

def post(request):
    return render(request, 'booktest/post.html')

def post_action(request):
    if request.session['islogin']:
        username = request.session['username']
        return HttpResponse('用户'+username+'发了一篇帖子')
    else:
        return HttpResponse('发帖失败')

from PIL import Image, ImageDraw, ImageFont
from django.utils.six import BytesIO

# 验证码
def verify_code(request):
    #引入随机函数模块
    import random
    #定义变量，用于画面的背景色、宽、高
    bgcolor = (random.randrange(20, 100), random.randrange(
        20, 100), 255)
    width = 100
    height = 25
    #创建画面对象
    im = Image.new('RGB', (width, height), bgcolor)
    #创建画笔对象
    draw = ImageDraw.Draw(im)
    #调用画笔的point()函数绘制噪点
    for i in range(0, 100):
        xy = (random.randrange(0, width), random.randrange(0, height))
        fill = (random.randrange(0, 255), 255, random.randrange(0, 255))
        draw.point(xy, fill=fill)
    #定义验证码的备选值
    str1 = 'ABCD123EFGHIJK456LMNOPQRS789TUVWXYZ0'
    #随机选取4个值作为验证码
    rand_str = ''
    for i in range(0, 4):
        rand_str += str1[random.randrange(0, len(str1))]
    #构造字体对象，ubuntu的字体路径为“/usr/share/fonts/truetype/freefont”
    font = ImageFont.truetype('FreeMono.ttf', 23)
    #构造字体颜色
    fontcolor = (255, random.randrange(0, 255), random.randrange(0, 255))
    #绘制4个字
    draw.text((5, 2), rand_str[0], font=font, fill=fontcolor)
    draw.text((25, 2), rand_str[1], font=font, fill=fontcolor)
    draw.text((50, 2), rand_str[2], font=font, fill=fontcolor)
    draw.text((75, 2), rand_str[3], font=font, fill=fontcolor)
    #释放画笔
    del draw
    #存入session，用于做进一步验证
    request.session['verifycode'] = rand_str
    #内存文件操作
    buf = BytesIO()
    #将图片保存在内存中，文件类型为png
    im.save(buf, 'png')
    #将内存中的图片数据返回给客户端，MIME类型为图片png
    return HttpResponse(buf.getvalue(), 'image/png')
