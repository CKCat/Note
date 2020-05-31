from django.db import models

# Create your models here.
from tinymce.models import HTMLField

class GoodsInfo(models.Model):
    gcontent = HTMLField()
