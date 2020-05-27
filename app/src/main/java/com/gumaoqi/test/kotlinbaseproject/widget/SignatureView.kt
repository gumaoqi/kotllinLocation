package com.gumaoqi.test.kotlinbaseproject.widget

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class SignatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /**
     * 笔画X坐标起点
     */
    private var mX: Float = 0.toFloat()
    /**
     * 笔画Y坐标起点
     */
    private var mY: Float = 0.toFloat()
    /**
     * 画笔
     */
    private var mPaint: Paint? = null
    /**
     * 路径
     */
    private var mPath: Path? = null
    /**
     * 记录每一个笔画的对象
     */
    private var mDrawPath: DrawPath? = null
    /**
     * 签名bitmap画布
     */
    private var mBitmapCanvas: Canvas? = null
    /**
     * 签名位图
     */
    private var mBitmap: Bitmap? = null
    /**
     * 画笔宽度
     */
    private var mPaintWidth = 10
    /**
     * 笔画颜色
     */
    private var mPaintColor = Color.BLACK
    /**
     * 背景色
     */
    private var mBgColor = Color.TRANSPARENT

    /**
     * mSavePath：保存笔画的集合，list有序保存;mDeletePath：撤销的笔画
     */
    private var mSavePath: MutableList<DrawPath>? = null
    private var mDeletePath:MutableList<DrawPath>? = null


    /**
     * 笔画路径和画笔储存
     */
    inner class DrawPath {
        var path: Path? = null// 路径
        var paint: Paint? = null// 画笔
    }


    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPaint()
        initCanvas()

        mSavePath = ArrayList<DrawPath>()
        mDeletePath = ArrayList<DrawPath>()
    }

    /**
     * 设置画笔
     */
    private fun setPaint() {
        mPaint = Paint()
        //设置抗锯齿
        mPaint!!.isAntiAlias = true
        //设置签名笔画样式
        mPaint!!.style = Paint.Style.STROKE
        //设置笔画宽度
        mPaint!!.strokeWidth = mPaintWidth.toFloat()
        //设置签名颜色
        mPaint!!.color = mPaintColor
    }

    /**
     * 初始化bitmap、画布
     */
    private fun initCanvas() {
        //创建跟view一样大的bitmap，用来保存签名
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888)
        mBitmapCanvas = Canvas(mBitmap!!)
        mBitmapCanvas!!.drawColor(mBgColor)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event)
            MotionEvent.ACTION_MOVE -> touchMove(event)
            MotionEvent.ACTION_UP -> {
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                mBitmapCanvas!!.drawPath(mPath!!, mPaint!!)
                //                mPath.reset();
                //将一条完整的路径保存下来(相当于入栈操作)
                mSavePath!!.add(mDrawPath!!)
                mPath = null// 重新置空
            }
        }
        // 更新绘制
        invalidate()
        return true
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画此次笔画之前的签名
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mPaint)
        // 通过画布绘制多点形成的图形
        if (mPath != null) {//当手指滑动时也实时画上
            canvas.drawPath(mPath!!, mPaint!!)
        }
    }

    // 手指点下屏幕时调用
    private fun touchDown(event: MotionEvent) {
        // 每次按下都是新的一笔，创建新的path
        mPath = Path()
        val x = event.x
        val y = event.y
        mX = x
        mY = y
        // mPath绘制的绘制起点
        mPath!!.moveTo(x, y)
        //新的笔画存在新的对象里，方便撤回操作
        mDrawPath = DrawPath()
        mDrawPath!!.path = mPath
        mDrawPath!!.paint = mPaint
    }

    // 手指在屏幕上滑动时调用
    private fun touchMove(event: MotionEvent) {
        val x = event.x
        val y = event.y
        val previousX = mX
        val previousY = mY
        val dx = Math.abs(x - previousX)
        val dy = Math.abs(y - previousY)
        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            val cX = (x + previousX) / 2
            val cY = (y + previousY) / 2
            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath!!.quadTo(previousX, previousY, cX, cY)
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x
            mY = y
        }
    }

    /**
     * 撤销上一步
     */
    fun goBack() {
        if (mSavePath != null && mSavePath!!.size > 0) {
            val drawPath = mSavePath!![mSavePath!!.size - 1]
            mDeletePath!!.add(drawPath)
            mSavePath!!.removeAt(mSavePath!!.size - 1)
            redrawBitmap()
        }
    }

    /**
     * 前进
     */
    fun goForward() {
        if (mDeletePath != null && mDeletePath!!.size > 0) {
            val drawPath = mDeletePath!!.get(mDeletePath!!.size - 1)
            mSavePath!!.add(drawPath)
            mDeletePath!!.removeAt(mDeletePath!!.size - 1)
            redrawBitmap()
        }
    }

    /**
     * 重画bitmap
     */
    private fun redrawBitmap() {
        /*mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布*/
        initCanvas()
        val iter = mSavePath!!.iterator()
        while (iter.hasNext()) {
            val drawPath = iter.next()
            mBitmapCanvas!!.drawPath(drawPath.path!!, drawPath.paint!!)
        }
        invalidate()// 刷新
    }

    /**
     * 清除画板
     */
    fun clear() {
        if (mSavePath != null && mSavePath!!.size > 0) {
            mSavePath!!.clear()
            mDeletePath!!.clear()
            redrawBitmap()
        }
    }

    /**
     * 保存画板
     *
     * @param path 保存到路径
     */
    @Throws(IOException::class)
    fun save(path: String) {
        save(path, false, 0)
    }

    /**
     * 保存画板
     *
     * @param path       保存到路径
     * @param clearBlank 是否清除边缘空白区域
     * @param blank      要保留的边缘空白距离
     */
    @Throws(IOException::class)
    fun save(path: String, clearBlank: Boolean, blank: Int) {

        var bitmap = mBitmap
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  压缩图片
        if (clearBlank) {
            bitmap = clearBlank(bitmap!!, blank)
        }
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val buffer = bos.toByteArray()
        if (buffer != null) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
            val outputStream = FileOutputStream(file)
            outputStream.write(buffer)
            outputStream.close()
        }
    }

    /**
     * 获取画板的bitmap
     *
     * @return
     */
    fun getBitMap(): Bitmap {
        setDrawingCacheEnabled(true)
        buildDrawingCache()
//        setDrawingCacheEnabled(false);
        return getDrawingCache()
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private fun clearBlank(bp: Bitmap, blank: Int): Bitmap {
        var blank = blank
        val HEIGHT = bp.height
        val WIDTH = bp.width
        var top = 0
        var left = 0
        var right = 0
        var bottom = 0
        var pixs = IntArray(WIDTH)
        var isStop: Boolean
        //扫描上边距不等于背景颜色的第一个点
        for (y in 0 until HEIGHT) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBgColor) {
                    top = y
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (y in HEIGHT - 1 downTo 0) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBgColor) {
                    bottom = y
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        pixs = IntArray(HEIGHT)
        //扫描左边距不等于背景颜色的第一个点
        for (x in 0 until WIDTH) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT)
            isStop = false
            for (pix in pixs) {
                if (pix != mBgColor) {
                    left = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (x in WIDTH - 1 downTo 1) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT)
            isStop = false
            for (pix in pixs) {
                if (pix != mBgColor) {
                    right = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        if (blank < 0) {
            blank = 0
        }
        //计算加上保留空白距离之后的图像大小
        left = if (left - blank > 0) left - blank else 0
        top = if (top - blank > 0) top - blank else 0
        right = if (right + blank > WIDTH - 1) WIDTH - 1 else right + blank
        bottom = if (bottom + blank > HEIGHT - 1) HEIGHT - 1 else bottom + blank
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top)
    }

    /**
     * 设置画笔宽度 默认宽度为10px
     * 这边设置可以对接下来的笔画生效
     * @param mPaintWidth
     */
    fun setPaintWidth(mPaintWidth: Int) {
        var mPaintWidth = mPaintWidth
        mPaintWidth = if (mPaintWidth > 0) mPaintWidth else 10
        this.mPaintWidth = mPaintWidth
        setPaint()

    }


    fun setBgColor(@ColorInt backColor: Int) {
        mBgColor = backColor

    }


    /**
     * 设置画笔颜色
     * 这边设置可以对接下来的笔画生效
     * @param paintColor 画笔颜色
     */
    fun setPaintColor(paintColor: Int) {
        this.mPaintColor = paintColor
        setPaint()
    }

    /**
     * 是否有签名,根据是否有笔画来判断
     * @return
     */
    fun isSign(): Boolean {
        return if (mSavePath != null && mSavePath!!.size > 0) {
            true
        } else {
            false
        }
    }
}