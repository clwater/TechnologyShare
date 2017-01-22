源码：<https://github.com/Tencent/tinker.git> ，我这里用到的是v1.7.7版。

##源码结构

使用Android Studio打开tinker的源码后，可以看到有以下几个Module：

* aosp-dexutils：用来描述Dex文件结构
* bsdiff-util：BsDiff的java实现，就三个类：BSDiff、BSPatch、BSUtil
* tinker-android-anno：用来生成真正的Application的库
* tinker-android-lib：加载补丁合成新的dex、加载新的dex替换旧dex
* tinker-android-loader：主要是tinker-android-lib中用到的补丁应用过程的各种listener
* tinker-commons：tinker-android-lib和tinker-patch-lib中用到的一些工具类
* tinker-patch-cli：命令行接入
* tinker-patch-gradle-plugin：gradle方式接入
* tinker-patch-lib：构建过程的具体实现

##构建过程

接入tinker，首先做的事情是引入依赖包和tinker的gradle插件，配置好后执行`./gradlew :app.oa:tinkerPatchRelease`。分析构建过程要从tinker-patch-gradle-plugin的源码入手。

###TinkerPatchPlugin

先说一点基础知识：

Gradle是一种配置脚本，编程语言是Groovy。脚本分为三种：初始化脚本`init.gradle`、设置脚本`settings.gradle`和构建脚本`build.gradle`。后两种是最常见的。

初始化脚本不常用，对应构建过程中的`Gradle`对象，一次构建只有一个`Gradle`对象。

设置脚本是每一个工程都用到的，这个脚本中配置了参加构建过程的构建脚本，每一个设置脚本对应一个`Settings`对象。

构建脚本，每一个`build.gradle`对应一个`Project`对象，`Project`对象实际上是一个`Task`的集合。

构建过程：

1. 如果有初始化脚本，则执行初始化脚本。
2. 执行设置脚本，确定加入构建的项目，并创建相应的`Project`对象。
3. 执行各个构建脚本，确定构建阶段的`Project`和`Task`的关系，建立一个有向图来描述`Task`之间的依赖关系。
4. 执行指定的`Task`及其依赖的`Task`。

其实还需要一点`Groovy`的基础知识，在后面用到时慢慢说。

我们在执行`./gradlew :app.oa:tinkerPatchRelease`时，构建过程前两步不用管，在第三步执行app.oa的`build.gradle`时，根据我们的配置，如果`tinkerEnabled`为`true`，会执行到`apply plugin: 'com.tencent.tinker.patch'`，这是Groovy的语法，意思是调用`apply`方法，传入参数是一个`Map`。在`classpath`中找到`com.tencent.tinker.patch`对应的`Plugin`实现是`TinkerPatchPlugin`。怎么找到的呢？在`classpath`中查找jar包的`resources/META-INF.gradle-plugins/com.tencent.tinker.patch.properties`文件，内容是`implementation-class=com.tencent.tinker.build.gradle.TinkerPatchPlugin`。

创建`TinkerPatchPlugin`对象后，会执行它的`apply()`方法。终于说到正题了，下面说说大概流程，不涉及具体实现：

1. 添加一个依赖的插件`osdetector`，用来检测当前平台的。
2. 读取tinker的配置，有`buildConfig`、`dex`、`lib`、`res`、`packageConfig`、`sevenZip`几个部分，不细说，对照我们的构建脚本看就知道了。需要了解一下的是，这些配置都是以一种数据模型（我们习惯称之为Model）来描述的，并没有继承某个类或者实现某个接口。但是在读取配置的时候，实际的对象类型是创建时传入的类型，同时这个对象也被动态实现了`org.gradle.api.puglins.ExtensionAware`接口。[ExtensionAware](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.ExtensionAware.html)的文档描述很详细。
3. 检查当前`Project`的插件中有没有`com.android.application`，如果没有就抛个异常结束构建。
4. 在生成有向图后，对有向图做一些修改，将指定的几个Task插入构建过程。
5. 执行Tasks。

上面第4步的实现是，调用`project.afterEvaluate`函数，传入一个闭包，闭包是一个`groovy.lang.Closure`对象，可以作为变量、方法参数，甚至当作普通方法调用，且必定有返回值。`project.afterEvaluate`意思是在有向图建立完毕后，执行一个闭包。我们看看这个闭包中干了些撒：

1. 如果配置中`tinkerEnable`为`false`，直接返回，不修改有向图。
2. 否则，开启`jumboMode`，在Dex文件结构中，`stringIds`中角标用`@BBBB`来表示，也就是最大只能是`65535`，开启`jumboMode`后，角标会用`@BBBBBBBB`来表示。
3. 关闭`preDexLibraries`，这个是增量构建时用到的
4. 然后就是打印一堆日志
5. 对每一个此次构建中的`Build Variant`，修改其有向图

`Build Variant`是撒？大类型有三种：`ApplicationVariant`、`LibraryVariant`和`TestVariant`，这里用到的是`ApplicationVariant`。`android.applicationVariants`是本次构建的`ApplicationVariant`集合，这些`ApplicationVariant`就是`Flavor`、`BuildType`，甚至屏幕设置等等的组合。BuildType一般只有release和debug两种，假设有Xiaomi、Huawei两种Flavor，组合一下就有4种ApplicationVariant：XiaomiRelease、XiaomiDebug、HuaweiRelease、HuaweiDebug。咱们在使用tinker时，都是直接指定了BuildType，上面第5步分析起来就简单了：

1. 获得当前Build Variant的输出，和当前Build Variant的名称。输出是一个`BaseVariantOutput`对象，可以从中得到构建过程中的一些`Task`，比如`PackageApplicationTask`、`ProcessResourcesTask`等
2. 如果检测到当前开启了Instant Run，抛一个异常，终止构建。
3. 创建一个名为`tinkerPatch${variantName}`的`Task`，定义在`TinkerPatchSchemaTask`。设置`tinkerPatch`依赖`assemble`，设置`tinkerPatch`的签名配置，将本次构建产出的apk的路径设置为`tinkerPatch`的`buildApkPath`（我们在接入AndResGuard的时候，就是通过修改这个`buildApkPath`实现的），设置`tinkerPatch`的`outputFolder`。
4. 创建一个名为`tinkerProcess${variantName}Manifest`的`Task`，定义在`TinkerManifestTask`。设置TinkerManifestTask的`manifestPath`，然后将其设置为必须在`variantOutput.processManifest`之后执行，并设置`variant.processResources`依赖`TinkerManifestTask`。
5. 创建一个名为`tinkerProcess${variantName}ResourceId`的`Task`，定义在`TinkerResourceIdTask`。设置`TinkerResourceIdTask`的`resDir`，设置`TinkerResourceIdTask`必须在`TinkerManifestTask`后执行，并设置`variant.processResource`依赖`TinkerResourceIdTask`。
6. 如果开启了混淆，就创建一个名为`tinkerProcess${variantName}Proguard`的`Task`，定义在`TinkerProguardConfigTask`。设置`TinkerProguardConfigTask`必须在`TinkerManifestTask`之后执行，并设置`proguardTask`依赖`TinkerProguardConfigTask`。
7. 如果开启了MultiDex，就创建一个名为`tinkerProcess${variantName}MultidexKeep`的`Task`，定义在`TinkerMultidexConfigTask`。设置`TinkerMultidexConfigTask`必须在`TinkerManifestTask`之后执行，设置`multiDexTask`依赖`TinkerMultidexConfigTask`，设置`TinkerMultidexConfigTask`必须在`collect${variantName}MultiDexComponents`之后执行。
8. 如果`tinkerPatch{}`的`buildConfig{}`中配置了`keepDexApply true`（默认是`false`），且`oldApk`有效，就hook Dex Transform。由于`transform`坑比较多，所以我们不用。这个选项的官方解释是：**如果我们有多个dex,编译补丁时可能会由于类的移动导致变更增多。若打开keepDexApply模式，补丁包将根据基准包的类分布来编译**。

