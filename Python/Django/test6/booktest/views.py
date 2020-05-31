from django.shortcuts import render
from django.conf import settings
from django.core.mail import send_mail
from django.http import HttpResponse

# Create your views here.
from booktest.models import *
def index(request):
    print("hello world")
    return render(request, 'booktest/index.html')

def editor(request):
    return render(request, 'booktest/editor.html')

def show(request):
    goods = GoodsInfo.objects.get(pk=1)
    context = {'g': goods}
    return render(request, 'booktest/show.html', context)

def query(request):
    return render(request, 'booktest/query.html')

def send(request):
    msg = msg='<a href="https://ckcat.github.io/" target="_blank">点击激活</a>'
    send_mail('注册激活', '', settings.EMAIL_FROM,
              ['ckcatck@qq.com'],
              html_message=msg)
    return HttpResponse("OK")
























