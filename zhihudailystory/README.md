# 简介——知乎简报（知乎日报第三方客户端） 

## 项目地址
https://github.com/RimonHwang/RimonZhiHuStory

## 开发工具
>1. Android Studio
>2. Genymotion虚拟机
>3. 由[izzyleung](https://github.com/izzyleung)提供的知乎日报API（API介绍地址：[点击查看](https://github.com/izzyleung/ZhihuDailyPurify/wiki/知乎日报-API-分析)，紧供学习使用）

## 开发周期
一周

## 软件功能简介
通过API获取知乎日报的数据并显示到安卓客户端上，使得用户能够查看当天和历史日报。

## 实现效果：

## 主要第三方库
>依赖注入框架ButterKnife，
>网络请求库Retrofit，
>异步框架RxJava（RxAndroid），
>图片加载库Glide。

## 主要思路：
1.通过RecyclerView展示日报列表，其中RecyclerView包括四种viewType：
>TYPE_HEADER：用于展示每日热报图片轮播界面
>TYPE_HEADER_SECOND：用于展示新日报提醒小红点
>TYPE_DATE：用于展示日期分类标签项
>TYPE_ITEM：用于展示日报项

2.点击日报项后跳转到日报详情界面，使用WebView显示日报详细内容

## 项目详细总结和分析
请参考我的[个人博客](https://rimonhwang.github.io/)里面的“知乎简报项目总结”系列文章。