## 前言

最近公司接到小需求--「可以滚动的提示」，其实就是跑马灯。这让我想到了大学时专业**物联网**，当时学的单片机入门教程就是跑马灯，很是亲切。其实就是灯(或文字)按照某个方向循环滚动。

## Android 原生的跑马灯

其实，`Android`中的`TextView`自带跑马灯效果，只需要通过简单的配置，就可以完成滚动的效果。

在`XML`中进行配置

    <TextView
        android:id="@+id/test"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ellipsize="marquee"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:marqueeRepeatLimit="marquee_forever"
        android:scrollHorizontally="true"
        android:singleLine="true"
        android:text="我是跑马灯，我是跑马灯，我是跑马灯，我是跑马灯，我是跑马灯，我是跑马"
        android:textSize="28sp" />
    
可以看到需要很多的属性配置，了解一下每个属性的含义：

- `android:ellipsize="marquee"` 设置为跑马灯效果
- `android:focusable="true"` 获取焦点
- `android:focusableInTouchMode="true"` touch 时获取焦点
- `android:marqueeRepeatLimit="marquee_forever"` 设置重复次数
- `android:scrollHorizontally="true"` 设置为水平滚动
- `android:singleLine="true"` 单行显示

按照上面的配置，正常情况下是可以运转的，但是用到项目中的时候，会发现很多`bug`和不足之处。

比如，偶尔突然不滚动了，具体的原因是没有获取到焦点。我觉得这是原生跑马灯最坑的一点，必须获取到焦点才能正常运行。

当然解决方式也有，第一种，通过主动获取焦点的方式，即调用`view.setFocusable(true)`。还有一种就是重写`TextView`的`isFocused()`方法，强制让他获取焦点。

    @Override
    public boolean isFocused() {
        return true;
    }
    
就算这样，在遇到复杂的界面还是会遇到问题，要么焦点会被断断续续的被抢夺，导致卡顿，要么不符合`UI`提出的滚动速度要求。


## 自定义跑马灯

鉴于这个背景，通过`Scroller`完成自定义的跑马灯，代码已上传至`GitHub`上:[MarqueeTextView](https://github.com/xiaweizi/MarqueeTextView)

先看一下整体的效果：

![MarqueeTextView](http://upload-images.jianshu.io/upload_images/4043475-693d71be1451c080.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果想直接使用，在根`build.gradle`配置：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
在`app`下的`build.gradle`添加依赖

	dependencies {
		compile 'com.github.xiaweizi:MarqueeTextView:1.0'
	}
	
最后在`XML`直接使用即可：

       <com.xiaweizi.marquee.MarqueeTextView
            android:id="@+id/marquee1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="10dp"
            android:text="@string/string1"
            android:textColor="#ff0000"
            android:textSize="18sp"
            app:scroll_first_delay="0"
            app:scroll_interval="2000"
            app:scroll_mode="mode_forever" />
	
具有一下功能：

- 控制滚动时间
- 控制滚动延迟
- 控制滚动模式
- 生命周期可以自己控制
    - 暂停
    - 继续
    - 重新开始
    - 停止

## 实现原理

通过`Scroller`控制器来控制整个`View`的滚动，那什么是`Scroller`，做个简单的介绍。

`Scroller`内部封装了滚动的操作，通过构造函数中传入插值器。可以控制起始位置和整个滚动的时间，并且通过`computeScrollOffset()`得到滚动动作是否结束。

最核心的方法有两个：

1. `startScroll`

        /**
         * @param startX 水平方向滚动的偏移值，以像素为单位。
         * @param startY 垂直方向滚动的偏移值，以像素为单位
         * @param dx     水平方向滚动的距离
         * @param dy     垂直方向滚动的距离
         * @param duration 滚动持续的时间，以毫秒为单位
         */
        public void startScroll (int startX, int startY, int dx, int dy, int duration) {
            ...
        } 
    
2. `computeScrollOffset`

        /**
         * @return 返回动画是否结束
         */
        public boolean computeScrollOffset (){
            ...
        }

注释已经很清楚了，那么接下来讲一下滚动的大概实现。

**首先**，要算出从初始位置开始滚动，到结束的距离，其实就是文字的长度。

    /**
     * 计算滚动的距离
     * @return 滚动的距离
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }
    
**其次**，调用`startScroll`方法进行滚动，注意的是需要调用`invalidate`方法，才会有效果。

**最后**一个问题就是，滚动结束后继续滚动。`Scroller`在滚动的时候，会不断回调`View`的`computeScroll`方法，于是就可以在这个方法里进行判断，如果结束了，就重新开始。

到此一个简单的跑马灯效果就实现了，当然如果还想添加别的需要，只要搞懂其原理，这些都不是问题。
    
