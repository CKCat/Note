
class my_mid:
    def __init__(self):
        print("-----init-----")

    def process_request(self, request):
        print("-----process_request-----")

    def process_view(self, view_func, *view_args, **kwargs):
        print("-----process_view-----")

    def process_response(self, request, response):
        print("-----process_response-----")
        return response

class exp1:
    def process_exception(self, request, exception):
        print("-----exp1-----")

class exp2:
    def process_exception(self, request, exception):
        print("-----exp2-----")