import os
import re
import subprocess
import time
import sys

def capture_screenshot(driver):
    # 定义文件名模式
    pattern = r'^selenium-screenshot-(\d+)\.png$'
    max_num = 0
    
    # 遍历当前目录中的所有文件
    for filename in os.listdir('.'):
        # 匹配文件名模式
        match = re.match(pattern, filename)
        if match:
            # 提取序号并更新最大值
            num = int(match.group(1))
            if num > max_num:
                max_num = num
    
    # 计算下一个序号
    next_num = max_num + 1
    screenshot_name = f'selenium-screenshot-{next_num}.png'
    driver.get_screenshot_as_file(screenshot_name)

def SetTimezone(tgt_timezone):
    platform = sys.platform
    try:
        if platform == "win32":
            result = subprocess.run(['tzutil', '/s', tgt_timezone], capture_output=True, text=True)
            if result.returncode != 0:
                print(f"Windows 时区设置失败，错误信息：{result.stderr}")
                return
        #...（其他平台逻辑及错误处理 ）
        print(time.strftime('%Y-%m-%d %H:%M:%S'))
        time.sleep(5)
        print(time.strftime('%Y-%m-%d %H:%M:%S'))
    except Exception as e:
        print(f"执行过程发生异常：{str(e)}")
