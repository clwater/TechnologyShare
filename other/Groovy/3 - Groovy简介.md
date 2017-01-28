[Groovy](http://groovy-lang.org/)是基于JVM平台的一门**动态语言**。Gradle是由Groovy写的一种DSL（Domain Specific Language，领域特定语言）。

##和Java相比，一些特性

1. 每行结束的分号是**可选**的
2. 每个类、构造方法，以及方法的默认访问权限都是`public`
3. 方法的`return`也是可选的，如果没有`return`，方法的最后一个表达式的值会被作为返回值
4. 字段(Field)的`getter/setter`是自动生成的，不用手写
5. 使用`==`来比较两个实例时，实际调用的是`equals`方法
6. 既支持静态类型（Java就是静态类型语言），也支持动态类型（比如插件中的配置信息读取后，创建的对象被自动实现了`org.gradle.api.puglins.ExtensionAware`接口）
7. 方法签名中有1个或1个以上参数时，调用方法时的括号是可选的。这个我们见得多了，`build.gradle`文件中随处可见，比如`compileSdkVersion 23`，意思就是调用`compileSdkVersion`方法，传入参数是`23`
8. 单引号和双引号都可以定义字符串，区别是第二种字符串实际上是`Groovy String(GString)`，可以在字符串中插入表示占位的变量，比如
    ```
  def language = 'groovy'
def sentence = "$language is awesome!"
def improvedSentence = "${language.capitalize()} is awesome!"
    ```
9. 直接使用`[]`会实例化出`java.util.ArrayList`的对象，可以使用`<<`运算符将一个对象加入`List`中
10. 直接使用`[key:value]`的方式会实例化出`java.lang.LinkedHashMap`的对象
11. 闭包是一个`groovy.lang.Closure`对象，可以作为变量、方法参数，甚至当做普通方法使用
12. 任何未明确定义的闭包都有一个名为`it`的内部变量，这个变量的值是当前闭包被调用时传入的第一个参数，如果没有参数就是`null`
13. 闭包永远都有返回值，`return`和方法一样是可以省略的

##举个🌰

拿我们项目中最简单的声网的`build.gradle`来看：

```
apply plugin: 'com.android.library' // 调用Project的apply方法，传入参数是一个Map

android { // 调用android方法，传入参数是一个闭包
    compileSdkVersion 23 // 调用compileSdkVersion方法，传入参数是一个int
    buildToolsVersion "23.0.3"

    defaultConfig { // 调用defaultConfig方法，传入参数是一个闭包
        minSdkVersion 14
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
```