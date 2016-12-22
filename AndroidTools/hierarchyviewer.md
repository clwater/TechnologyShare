# Android SDK tools 之一 Hierarchy Viewer

>本系列旨在介绍一些被忽略的优质工具 毕竟 能被当做自带的工具总有些做的比较好的地方不是

## Hierarchy Viewer

Hierarchy Viewer是一个可以用来查看View的试用工具

### 启动Hierarchy Viewer

hierarchyviewer工具在sdk/tools路径下

再次打开后会出现如下提示
```
The standalone version of hieararchyviewer is deprecated.
Please use Android Device Monitor (tools/monitor) instead.
```
主要想说的就是 单独使用hieararchyviewer已经不被建议  建议使用Android Device Monitor(Android Device Monitor的相关使用后续会详细介绍)

直接运行monitor或者在在Android Studio -> tools -> android -> Android Device Monitor中将hieararchyviewer工具打开

**以下对hieararchyviewer工具进行操作均为直接打开hieararchyviewer工具 和通过monitor工具打开的hieararchyviewer的UI可能略有不同  以直接打开hieararchyviewer工具操作为准**


#### 无法正常使用

在连接过程中可能遇到无法连接到手机的问题 详情参考以下文章

[HierachyViewer无法连接真机调试](http://blog.csdn.net/yafeng_0306/article/details/17224001)

[HierachyViewer无法连接真机调试详解](http://maider.blog.sohu.com/255448342.html)

### 使用Hierarchy Viewer

![Hierarchy Viewer 1-1](\image/1_1.png)

成功连接后会出现如上页面

当前页面正在显示的进程被加粗显示

(那些看着是空的位置 进入后会显示通知栏中的View布局)

选择想要查看的进程后进入 Load View Hierarchy页面
