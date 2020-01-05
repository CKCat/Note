from django.shortcuts import render, HttpResponse

# Create your views here.

def index(request):
    return HttpResponse("Hello World")

def post(request):
    return render(request, "csrf_login/post.html")