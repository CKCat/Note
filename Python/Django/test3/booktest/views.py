from django.shortcuts import render, HttpResponse
from django.template import RequestContext, loader
from django.http import JsonResponse
from django.http import HttpResponseRedirect
# Create your views here.

def index(request):
    str = '%s, %s' %(request.path, request.encoding)
    return render(request, 'booktest/index.html', {'str':str})

def method_show(request):
    return HttpResponse(request.method)

def show_args(requset, id):
    return HttpResponse('show_args %s' % id)

def show_arg(requset, id1):
    return HttpResponse('show_arg %s' % id1)

def show_reqarg(request):
    if request.method == 'GET':
        a = request.GET.get('a') #获取请求参数a
        b = request.GET.get('b') #获取请求参数b
        c = request.GET.get('c') #获取请求参数c
        return render(request, 'booktest/show_getarg.html', {'a':a, 'b':b, 'c':c})
    else:
        name = request.POST.get('name')
        gender = request.POST.get('gender')
        hobbys = request.POST.getlist('hobby')
        return render(request, 'booktest/show_postarg.html', {'name':name, 'gender':gender, 'hobbys':hobbys})

def index2(request):
    str = '<h1>Hello world</h1>'
    return HttpResponse(str)

def index3(request):
    # 加载模板
    t1 = loader.get_template('booktest/index3.html')
    # 构造上下文
    context = RequestContext(request, {'h1':"Hello World"})
    # 使用上下文渲染模板，生成字符串后返回响应对象
    return HttpResponse(t1.render(context))

def json1(request):
    return render(request, 'booktest/json1.html')

def json2(request):
    return JsonResponse({'h1':'hello', 'h2':'world'})

def red1(request):
    return HttpResponseRedirect('/')

def cookie_set(request):
    response = HttpResponse("<h1>设置 Cookie</h1>")
    response.set_cookie('h1', 'helloworld')
    return response

def cookie_get(request):
    response = HttpResponse("读取Cookie,数据如下")
    if 'h1' in request.COOKIES:
        response.write("<h1>" + request.COOKIES['h1'] + "</h1>")
    return response

def session_test(request):
    request.session['h1'] = 'hello django'
    return HttpResponse("写 Session")

def session_read(request):
    h1 = request.session.get("h1")
    return HttpResponse(h1)

def session_del(request):
    del request.session["h1"]
    return HttpResponse("del OK")

def del_session(request):
    request.session.flush()
    return HttpResponse("Del session OK")
