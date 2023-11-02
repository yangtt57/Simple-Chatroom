# -*- coding:utf-8 -*-
import os

os.environ['SPARK_APP_ID'] = "6acd3d08" # 星火大模型的APPID
os.environ['SPARK_API_SECRET'] = "NjcxYzMyOWEzMTljZDQ0OTM5ZWUyODEw" # 星火大模型的APISecret
os.environ['SPARK_API_KEY'] = "ccae016636c93e1008c62dc1bfca62ec" # 星火大模型的APIKey
os.environ['SPARK_API_MODEL'] = "v2.0"
os.environ['SPARK_CHAT_MAX_TOKENS'] = "4096"
os.environ['SPARK_CHAT_TEMPERATURE'] = "0.5"
os.environ['SPARK_CHAT_TOP_K'] = "4"

from sparkapi.core.api import SparkAPI
from sparkapi.core.config import SparkConfig
config = SparkConfig().model_dump()
api = SparkAPI(**config)

# start a chat session
api.chat()

# get completion from prompt
res = api.get_completion('hello')
print(''.join(res))

# get completion from messages
messages = [
    {'role': 'user', 'content': 'hello'},
    {'role': 'assistant', 'content': 'Hello! How can I assist'},
    {'role': 'user', 'content': 'write me a Python script of BubbleSort'},
]
res = api.get_completion_from_messages(messages)
print(''.join(res))