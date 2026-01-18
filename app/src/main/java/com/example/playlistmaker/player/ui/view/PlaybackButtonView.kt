package com.example.playlistmaker.player.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val playBitmap: Bitmap?
    private val pauseBitmap: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private val minViewSize = resources.getDimensionPixelSize(
        R.dimen.playback_button_size
    )

    enum class ButtonState {
        PLAY,
        PAUSE
    }

    private var state = ButtonState.PAUSE

    private var upListener: UpListener? = null

    fun setUpListener(listener: UpListener) {
        upListener = listener
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playBitmap = getDrawable(R.styleable.PlaybackButtonView_playImg)?.toBitmap()
                pauseBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseImg)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            ButtonState.PLAY -> pauseBitmap?.let {
                canvas.drawBitmap(pauseBitmap, null, imageRect, null)
            }

            ButtonState.PAUSE -> playBitmap?.let {
                canvas.drawBitmap(playBitmap, null, imageRect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                upListener?.onUp()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    fun setState(newState: ButtonState) {
        state = newState
        invalidate()
    }

    interface UpListener {
        fun onUp()
    }
}