## 我的博客
http://rimonhwang.com/

# <img src="http://od6atq4th.bkt.clouddn.com/static/images/simplezhihustory_ic_launcher.png" width = "5%"  alt="简报" align=center />简报（知乎日报第三方客户端）

> APK下载地址：[点击下载](http://rimonhwang.com/posts/2016/09/08/52346/)  
> 项目源码地址：https://github.com/RimonHwang/RimonZhiHuStory

## 简介
参考知乎日报打造的简约版知乎日报第三方客户端，体积小，功能齐全。 目前实现功能：
>1. 查看每日日报、主题日报及其相应的评论；
>2. 支持新浪微博一键登录、分享；
>3. 登陆后支持收藏日报到云端，跨设备查看；
>4. 支持切换夜间模式；
>5. 支持每日精彩日报推送和软件版本更新检查。 

客户端仍在更新中，欢迎下载使用。[API数据接口](https://github.com/izzyleung/ZhihuDailyPurify/wiki/知乎日报-API-分析)由[izzyleung](https://github.com/izzyleung)提供，紧供学习使用。

## 开发平台
>* Android Studio 2.2

## 开发周期
一个月

## 实现效果：
<div  align="center">
 <img src="http://od6atq4th.bkt.clouddn.com/static/images/zhihudaily_welcome.jpg?watermark/2/text/QFJpbW9uX0h3YW5n/font/5b6u6L2v6ZuF6buR/fontsize/500/fill/I0Y3RjBGMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageView2/2/w/360/h/640/interlace/0/q/100" width = "250"  alt="欢迎界面" align=center /> <img src="http://od6atq4th.bkt.clouddn.com/static/images/zhihudaily_drawer.jpg?watermark/2/text/QFJpbW9uX0h3YW5n/font/5b6u6L2v6ZuF6buR/fontsize/500/fill/I0Y3RjBGMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageView2/2/w/360/h/640/interlace/0/q/100" width = "250"  alt="侧滑菜单" align=center /> <img src="http://od6atq4th.bkt.clouddn.com/static/images/zhihudaily_homepage.jpg?watermark/2/text/QFJpbW9uX0h3YW5n/font/5b6u6L2v6ZuF6buR/fontsize/500/fill/I0Y3RjBGMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageView2/2/w/360/h/640/interlace/0/q/100" width = "250"  alt="首页" align=center />
</div>

<div  align="center">    
<img src="http://od6atq4th.bkt.clouddn.com/static/images/zhihudaily_story_detail.jpg?watermark/2/text/QFJpbW9uX0h3YW5n/font/5b6u6L2v6ZuF6buR/fontsize/500/fill/I0Y3RjBGMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageView2/2/w/360/h/640/interlace/0/q/100" width = "250"  alt="日报详情" align=center /> <img src="http://od6atq4th.bkt.clouddn.com/static/images/zhihudaily_collect.jpg?watermark/2/text/QFJpbW9uX0h3YW5n/font/5b6u6L2v6ZuF6buR/fontsize/500/fill/I0Y3RjBGMA==/dissolve/100/gravity/SouthEast/dx/10/dy/10|imageView2/2/w/360/h/640/interlace/0/q/100" width = "250"  alt="收藏界面" align=center />
</div>


## 主要第三方库和服务支持
>* 依赖注入框架 ButterKnife
>* 网络请求 Retrofit
>* 数据库 greenDao
>* 响应式编程 RxJava（RxAndroid）
>* 图片加载 Glide
>* Bmob后端云SDK
>* 小米推送和更新SDK
>* 新浪微博登陆分享SDK

## 主要思路：
1. 欢迎界面采用计时器展示从网络加载的欢迎图片，定时3秒。加载的图片会保存为本地文件，每次进入程序时判断是否需要更新图片，从而加快图片显示速度和降低流量消耗；

2. 由于使用Scrollview嵌套Recyclerview时会出现滑动卡顿现象，故使用包含多种ViewType的RecyclerView来展示首页相关内容，分为四种ViewType：
>**TYPE_HEADER：**ViewPager实现每日热闻图片轮播界面  
>**TYPE_HEADER_SECOND：**用于展示今日热闻标签和新热报提醒小红点  
>**TYPE_DATE：**用于展示日期分类标签项  
>**TYPE_ITEM：**用于展示日报项  

  日报列表数据会保存到本地数据库，定时从网络拉取新数据，从而避免每次进入页面都重新加载数据，提高流畅度。此外，当有新日报发表时，会出现小红点提醒用户手动更新日报列表；

3. 使用DrawerLayout实现侧滑菜单显示主题日报列表；

4. 使用WebView显示日报详细内容，其中涉及 Html + 本地css 的重新组装加载；

5. 使用新浪微博登陆SDK和Bmob后端云来实现用户登陆和收藏功能；

6. 使用小米推送SDK和自动更新SDK实现消息推送和软件版本更新功能。

7. 离线功能开发中……（思路：列表数据和日报内容存储通过Sqlite数据库实现、图片存储通过Glide的缓存文件实现， WebView通过开启自身的缓存功能实现）

## 项目详细总结和分析
请参考我的[个人博客](http://rimonhwang.com/)里面的“知乎简报项目总结”系列文章（待更新）。
