package com.example.utlikotlin

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.goToNextPage(duration: Long) = setCurrentItem(currentItem + 1, duration)

fun ViewPager2.setOnPageChangeListener(onChange: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onChange(position)
        }
    })
}

fun ViewPager2.setCurrentItem(item: Int, duration: Long) {
    val pxToDrag = width * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0

    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()

        fakeDragBy(-currentPxToDrag)

        previousValue = currentValue
    }

    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(p0: Animator) {
            endFakeDrag()
        }

        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
    })

    animator.interpolator = AccelerateDecelerateInterpolator()
    animator.duration = duration

    animator.start()
}