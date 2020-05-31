
import requests

def test():
    request = requests.get('https://baidu.com')
    print(request.text)

