#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# File Name : search_indexes
# Created by ckcat on 1/28/20

from haystack import indexes
from booktest.models import GoodsInfo

class GoodsInfoIndex(indexes.SearchIndex, indexes.Indexable):
    text = indexes.CharField(document=True, use_template=True)

    def get_model(self):
        return GoodsInfo

    def index_queryset(self, using=None):
        return self.get_model().objects.all()