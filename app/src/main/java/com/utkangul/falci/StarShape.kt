package com.utkangul.falci

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class StarShape(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val path = Path()

    // Animator nesnesini oluşturun
    private val colorAnimator = ValueAnimator().apply {
        paint.color = Color.WHITE
        setIntValues(Color.WHITE, Color.CYAN)
        duration = 1000
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            paint.color = animatedValue
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = centerX.coerceAtMost(centerY)

        path.reset()

        val innerRadius = radius / 512

        for (i in 0 until 6) {
            val angle = Math.PI * i / 4
            val x = (centerX + radius * cos(angle)).toFloat()
            val y = (centerY + radius * sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                val prevAngle = Math.PI * (i - 1) / 2
                val prevX = (centerX + radius * cos(prevAngle)).toFloat()
                val prevY = (centerY + radius * sin(prevAngle)).toFloat()

                val controlX = (centerX + innerRadius * cos(angle - Math.PI / 8)).toFloat()
                val controlY = (centerY + innerRadius * sin(angle - Math.PI / 8)).toFloat()

                path.quadTo(controlX, controlY, prevX, prevY)
            }
        }
        path.close()
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)
    }

    fun startColorAnimation() {
        if (!colorAnimator.isRunning) {
            colorAnimator.start()
        }
    }
}