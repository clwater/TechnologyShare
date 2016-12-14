# android xbb系列  五分钟带你成功使用开源库之一 EventBus

>官方网址:http://greenrobot.org/eventbus/

> github地址:https://github.com/greenrobot/EventBus


## 为什么要用EventBus
  简化组件之间的通信 暂不提常用的后台数据更新及UI更新之间的交互 私以为更主要的是EventBus使你不会在app的调用逻辑中迷路 让你开了上帝视角 很可能你在A页面中需要修改某些数据 但是触发的事件源却在B页面下 平时通过静态变量 CallBack接口或者扩展onResume()方法下 貌似不是那么的让人愉快 如果有这样的困惑 那么就应该试试EventBus了 可以让你不再纠结这些 极大的精简了跨线程跨页面调用的问题

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
在当前包下任意位置新建该类
  //类名随意
  //类中可以定义变量及对应的set 和get方法 方便以后扩展
  public class EventBusMessage{}
```

2. 初始化
```java
在Activity中重载对应方法 注册和取消注册EventBus

  @Override
  protected void onStart() {
      super.onStart();
      EventBus.getDefault().register(this);
  }

  @Override
  protected void onDestroy() {
      super.onDestroy();
      EventBus.getDefault().unregister(this);
  }
```

3. 准备订阅者(用于响应事件被触发后的相应操作)
```java
建议放在需要操作的Activity或者Fragment中 方便UI的更新或者其它操作

  //@Subscribe注解是必须的 该注解后面还会帮助我们实现更高级的使用方法 比如线程控制和粘性事件等
  @Subscribe
  public void onEventBusMessagePost(EventBusMessage e){
      Toast.makeText(this , "EventBusMessage is posted ." , Toast.LENGTH_SHORT).show();
  }
```

4. 提交事件
```java
整个项目中的代码的任何位置都可以
  EventBus.getDefault().post(new EventBusMessage());
```


## 再理解什么是EventBus
> EventBus is an open-source library for Android using the publisher/subscriber pattern for loose coupling. EventBus enables central communication to decoupled classes with just a few lines of code – simplifying the code, removing dependencies, and speeding up app development. --官方介绍

EventBus是一个基于**观察者模式**的事件发布/订阅框架

来看一下官方的介绍

![EventBus-Publish-Subscribe](https://raw.githubusercontent.com/greenrobot/EventBus/master/EventBus-Publish-Subscribe.png)

具体的简介可以参考一下EventBus的官方网站: [http://greenrobot.org/eventbus/](http://greenrobot.org/eventbus/)

**接下来主要介绍一下EventBus的一些高级用法**

##EventBus的线程模式

|threadMode|介绍|
|-|-|
|PostThread|处理函数会在与发出事件同一线程中运行 应该避免长耗时的操作|
|MainThread|处理函数会在主线程中运行 主要涉及到UI的更新 但不能用来处理耗时的操作|
|BackgroundThread|如果事件是在主线程中发出则该处理函数会在新的线程中运行 如果是在子线程中发出的 则该处理函则会在该子线程中运行 该模式下禁止UI操作|
|Async|该处理函数总会在新的子线程中执行 该模式下禁止UI操作|

具体实现如下

以下是在主线程中发布事件
![在主线程中发布事件](https://raw.githubusercontent.com/clwater/ExternalFile/master/img/eventbuslog1.png)
由此可以看到 此时BackgroundThread模式下是在一个新线程中运行的

以下是在子线程中发布事件
![https://raw.githubusercontent.com/clwater/ExternalFile/master/img/eventbuslog2.png](https://raw.githubusercontent.com/clwater/ExternalFile/master/img/eventbuslog2.png)
而此时在BackgroundThread模式下是和发布事件的线程同一个
