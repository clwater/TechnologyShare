# android xbb系列  五分钟带你成功使用开源库之一 EventBus

>官方网址:http://greenrobot.org/eventbus/

> github地址:https://github.com/greenrobot/EventBus


## 为什么要用EventBus
  简化组件之间的通信暂不提常用的后台数据更新及UI更新之间的交互 私以为更主要的是EventBus使你不会在app的调用逻辑中迷路 让你开了上帝视角 很可能你在A页面中需要修改某些数据 但是触发的事件源却在B页面下 平时通过静态变量 CallBack接口或者扩展onResume()方法下 貌似不是那么的让人愉快 如果有这样的困惑 那么就应该试试EventBus了 可以让你不再纠结这些 极大的精简了跨线程跨页面调用的问题

## 使用前准备

* Gradle:

  compile 'org.greenrobot:eventbus:3.0.0'

* Maven:
```Maven
  <dependency>
    <groupId>org.greenrobot</groupId>
    <artifactId>eventbus</artifactId>
    <version>3.0.0</version>
  </dependency>
```
* [download EventBus from Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.greenrobot%22%20AND%20a%3A%22eventbus%22)

## 五分钟快速使用

1. 定义事件
```java
  //类名随意
  public class EventBusMessage{}
```

2. 初始化
