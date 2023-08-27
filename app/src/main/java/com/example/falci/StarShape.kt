package com.example.falci

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class StarShape(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = centerX.coerceAtMost(centerY)

        path.reset()

        val innerRadius = radius / 512  // İçbükeylik sağlamak için iç yarıçapı belirleyin

        for (i in 0 until 6) {
            val angle = Math.PI * i / 4
            var x = (centerX + radius * cos(angle)).toFloat()
            var y = (centerY + radius * sin(angle)).toFloat()


            if (i == 0) {
                path.moveTo(x, y)
            } else {
                // İçbükeylik için kontrol noktalarını hesaplayın
                val prevAngle = Math.PI * (i - 1) / 2
                val prevX = (centerX + radius * cos(prevAngle)).toFloat()
                val prevY = (centerY + radius * sin(prevAngle)).toFloat()

                val midX = (prevX + x) / 2
                val midY = (prevY + y) / 2

                val controlX = (centerX + innerRadius * cos(angle - Math.PI / 8)).toFloat()
                val controlY = (centerY + innerRadius * sin(angle - Math.PI / 8)).toFloat()

                path.quadTo(controlX, controlY, prevX, prevY)
            }
        }


        path.close()

        paint.color = android.graphics.Color.WHITE
        paint.style = Paint.Style.FILL

        canvas.drawPath(path, paint)
    }
}