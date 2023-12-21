package com.utkangul.falci

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout

class HeartShape : FrameLayout {
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        fillPaint.color = Color.RED
        fillPaint.style = Paint.Style.FILL
        setWillNotDraw(false)
    }

    private val scale = context.resources.displayMetrics.density
    private val heartWidth = (60 * scale + 0.5).toInt()
    private val heartHeight = (60 * scale + 0.5).toInt()

    override fun onDraw(canvas: Canvas) {
        val path = createHeartPath(heartWidth, heartHeight)
        canvas.drawPath(path, paint)
        canvas.drawPath(path, fillPaint)
        super.onDraw(canvas)
    }


    private fun createHeartPath(width: Int, height: Int): Path {
        val path = Path()
        val pX = width / 2f
        val pY = height / 100f * 33.33f
        var x1 = width / 100f * 50
        var y1 = height / 100f * 5
        var x2 = width / 100f * 90
        var y2 = height / 100f * 10
        var x3 = width / 100f * 90
        var y3 = height / 100f * 33.33f
        path.moveTo(pX, pY)
        path.cubicTo(x1, y1, x2, y2, x3, y3)
        path.moveTo(x3, pY)
        x1 = width / 100f * 90
        y1 = height / 100f * 55f
        x2 = width / 100f * 65
        y2 = height / 100f * 60f
        x3 = width / 100f * 50
        y3 = height / 100f * 90f
        path.cubicTo(x1, y1, x2, y2, x3, y3)
        path.lineTo(pX, pY)
        x1 = width / 100f * 50
        y1 = height / 100f * 5
        x2 = width / 100f * 10
        y2 = height / 100f * 10
        x3 = width / 100f * 10
        y3 = height / 100f * 33.33f
        path.moveTo(pX, pY)
        path.cubicTo(x1, y1, x2, y2, x3, y3)
        path.moveTo(x3, pY)
        x1 = width / 100f * 10
        y1 = height / 100f * 55f
        x2 = width / 100f * 35f
        y2 = height / 100f * 60f
        x3 = width / 100f * 50f
        y3 = height / 100f * 90f
        path.cubicTo(x1, y1, x2, y2, x3, y3)
        path.lineTo(pX, pY)
        path.moveTo(x3, y3)
        path.close()
        return path
    }
}