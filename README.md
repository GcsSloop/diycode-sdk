<h1 align="center">diycode-sdk</h1>

<p align="center">
<a href="https://www.apache.org/licenses/LICENSE-2.0"> <img src="https://img.shields.io/badge/license-Apache%202-green.svg" /></a>
<a href="#"> <img src="https://img.shields.io/badge/Support-14%2B-green.svg" /></a>
<a href="https://jitpack.io/#GcsSloop/diycode-sdk"> <img src="https://jitpack.io/v/GcsSloop/diycode-sdk.svg" /></a>
</p>

## 项目简介

该项目是对 [diycode api](https://www.diycode.cc/api) 的再次封装，从标准的 http 接口转化为用户可以直接调用的接口，在封装过程中会屏蔽掉部分细节，让其更加容易使用。

> 例如，让身份认证机制的透明化，用户正常登录后可以直接调用 API ，不需要为每一次请求都手动添加认证信息和捕获异常状态，假如用户操作了没有权限的内容，会返回错误信息，程序可以根据错误信息进一步的处理。

目前 SDK 是 Beta 公测版，可能某些接口是有问题的，如果使用过程中遇到了问题，可以提交 Issues 反馈问题详情，以方便我在后续版本中修复。

## 创作动机

 [Diycode 社区、项目、News、sites 的 API 发布了](https://www.diycode.cc/topics/411)

diycode 的 api 发布已经很长时间了，想必有很多想做客户端练手的小伙伴看到它复杂的接口后都打了退堂鼓，毕竟相对于干货集中营这类的开放 api 来说，diycode api 的使用难度是其数倍之多。

为了让小伙伴们能更快速，更方便的开发出一个属于自己的 app，我才特地将数据请求这一层单独摘出来做了这个开源库，使用它，会让复杂的逻辑瞬间变的简单起来。

不信的话看下面，不到 10 行代码就能完成一次数据的请求处理，并且是用 EventBus 异步返回的，完全不用担心各种回调和线程问题。

## 使用方式

使用方式大概有 3 步，非常简单。

#### Step 1. 初始化 Diycode

初始化过程中使用的 应用ID 和 私钥 请到 Diycode 上自行注册获得。 [点击此处注册应用](https://www.diycode.cc/oauth/applications/new)

```java
// (上下文，应用ID，私钥)
Diycode.init(context, client_id, client_secret);
```

**初始化过程只需要执行一次，建议将该初始化的调用位置放在 Application 的 `onCreate` 方法中**。

#### Step 2. 获取实例

```java
Diycode mDiycode = Diycode.getSingleInstance();
```

理论上只要你对其进行了初始化，就可以在任意的位置获取到 Diycode 的实例，并使用该实例进行数据请求。

#### Step 3. 请求数据并接收返回的内容

```java
mDiycode.getTopic(topic.getId()); // 发出请求

// 接收数据
@Subscribe(threadMode = ThreadMode.MAIN)
public void onTopicDetail(GetTopicEvent event) {
    if (event.isOk()) {
      	TopicContent topicContent = event.getBean();
    }
}
```

> 所有返回的 Event 命名都是很有规律的，一律为 “方法名+Event” ，例如： 
>
> * getTopic => GetTopicEvent
> * login       => LoginEvent

如果不知道返回的 Event 类型，也可以去查看注释中的内容，例如 getTopic 的注释，其中 @see 就是返回的 Event 类型：

```java
/**
 * 获取 topic 内容
 *
 * @param id topic 的 id
 * @see GetTopicEvent <= 返回的 Event 类型
 */
```

当然了，**使用 EventBus 的时候不要忘记注册**，不然是接收不到事件的。

```java
@Override
protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
}

@Override
protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
}
```

**如果想要了解更多的 API 的话请查看 [在线文档](https://jitpack.io/com/github/GcsSloop/diycode-sdk/0.0.7/javadoc/) 。**

## Demo 效果

这是我开发的一个尚未完成的客户端，使用了这个 sdk 后，每一个 Activity(Fragment) 的代码量不超过 300 行就能完成所有功能。(Demo同样是公开的，放在 [diycode](https://github.com/GcsSloop/diycode) 中)

![](https://diycode.b0.upaiyun.com/photo/2017/01b87d582182e34dac091269a5e8d7ba.gif)

更新一个 demo 展示，如下图，在完善了下拉刷新和上拉加载后，代码依旧不超过 300 行，去除导包只有注释大约 200 行左右。

![](https://ww1.sinaimg.cn/large/006tKfTcly1fdlp10opypg308c0et7wj.gif)

## 如何添加

**Step 1. 添加JitPack仓库**

在当前项目的根目录下的 `build.gradle` 文件中添加如下内容:

``` gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

**Step 2. 添加项目依赖**

``` gradle
dependencies {
        compile 'com.github.gcssloop:diycode-sdk:0.0.7'
}
```
## 常见问题

#### 1. 异常状况的处理

在 SDK 设计过程中已经已经考虑到了可能会出现的一些意外情况，例如，网络差，数据请求失败，没有权限等等。

当出现异常情况时，结果依旧按照之前的方式返回，当出现意外状况时 `eveng.isOk()` 会被置为 false 状态，此时你可以使用 `event.getCode()` 来获取错误状态码，通过 `event.getCodeDescribe()` 来获取对错误状态码的描述。可以根据这些信息进行下一步的处理。

#### 2. 如何处理重复请求

由于设计中使用了 EventBus 作为传递的媒介，在某些特殊的条件下很可能会出现如下的情况，在用户网络状态不好的情况下，用户重复的调用加载方法，发出多次重复请求，这些请求结果可能会在网络变好之后在很短的时间内一同返回，如何判断处理这些数据呢？

为例应对这种情况，我对所有的请求都进行了唯一的编号，可以根据编号进行处理。首先，在发出请求的时候可以获得一个编号：

```java
String uuid = mDiycode.getTopic(topic.getId());
```

在数据返回的时候会附加上这一个编号，可以使用 `getUUID` 获得：

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onTopicDetail(GetTopicEvent event) {
    String uuid = event.getUUID();
}
```

可以通过对比这两个编号来确定是哪次请求。

例如：用户多次调用上拉加载函数，我们只想保留第一次请求成功的结果，并且忽略后续重复的请求，就可以将请求同一区间数据的 uuid 全部记录下来，当返回结果属于这些 uuid 并且成功时，更新状态，并且忽略后续请求结果。

#### 3. 如何区分请求

如果是请求的是不同数据类型，那么返回的接口是不同的，很容易区分，但是**当请求的是同一数据类型，但作用不同时该如何区分？如 Demo 中第 2 个例子，里面涉及了两种请求，下拉刷新和上拉加载，所有数据返回都是使用 GetTopicsListEvent 接收，此时就要使用到 uuid 了，核心思想是请求时记录 uuid 和其对应的类型，接收时根据 uuid 判断请求类型：**

```java
// 请求状态 - 下拉刷新 还是 加载更多
private static final String POST_LOAD_MORE = "load_more";
private static final String POST_REFRESH = "refresh";
private ArrayMap<String, String> mPostTypes = new ArrayMap<>();    // 请求类型

// 刷新
private void refresh() {
    ...
    String uuid = mDiycode.getTopicsList(null, null, pageIndex * pageCount, pageCount);
    mPostTypes.put(uuid, POST_REFRESH);
    ...
}

// 加载更多
private void loadMore() {
  	...
    String uuid = mDiycode.getTopicsList(null, null, pageIndex * pageCount, pageCount);
    mPostTypes.put(uuid, POST_LOAD_MORE);
    ...
}

@Subscribe(threadMode = ThreadMode.MAIN)
public void onTopicList(GetTopicsListEvent event) {
    String postType = mPostTypes.get(event.getUUID());	// 获取请求类型
    if (event.isOk()) {
        if (postType.equals(POST_LOAD_MORE)) {
           	// 是加载更多
        } else if (postType.equals(POST_REFRESH)) {
            // 是下拉刷新
        }
    } else {
        // 出现错误
    }
    mPostTypes.remove(event.getUUID());	// uuid 完成使命，从存储中移除
}
```

#### 4. Diycode SDK 有缓存机制吗

就目前而言是没有的，Diycode SDK 仅仅是一个 api 的再次封装，如果是需要缓存建议放在上层应用，或者重新抽象一个数据层出来。

之前有考虑过载 Diycode 中添加数据缓存机制，但是由于不清楚上层应用的状态，随意的使用缓存机制反而可能会导致数据与服务器不同步，从而引发一些异常状态，所以推荐将缓存放在上层应用中，该 SDK 只做一些简单的数据请求。

## 作者简介

#### 作者微博: [@GcsSloop](http://weibo.com/GcsSloop)

#### 个人网站: http://www.gcssloop.com

<a href="http://www.gcssloop.com/info/about/" target="_blank"> <img src="http://ww4.sinaimg.cn/large/005Xtdi2gw1f1qn89ihu3j315o0dwwjc.jpg" width="300"/> </a>



## 版权信息

```
Copyright (c) 2017 GcsSloop

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

