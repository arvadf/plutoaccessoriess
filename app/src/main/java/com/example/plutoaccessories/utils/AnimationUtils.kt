package com.example.plutoaccessories.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator

object AnimationUtils {
    
    /**
     * Fade in animation
     */
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(null)
    }
    
    /**
     * Fade out animation
     */
    fun fadeOut(view: View, duration: Long = 300, endAction: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    endAction?.invoke()
                }
            })
    }
    
    /**
     * Scale animation for click feedback
     */
    fun scaleClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
    
    /**
     * Bounce scale animation
     */
    fun bounceScale(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.1f, 1f)
        
        scaleX.duration = 500
        scaleY.duration = 500
        scaleX.interpolator = OvershootInterpolator()
        scaleY.interpolator = OvershootInterpolator()
        
        scaleX.start()
        scaleY.start()
    }
    
    /**
     * Slide up animation
     */
    fun slideUp(view: View, duration: Long = 400) {
        view.translationY = view.height.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(null)
    }
    
    /**
     * Slide down animation
     */
    fun slideDown(view: View, duration: Long = 400, endAction: (() -> Unit)? = null) {
        view.animate()
            .translationY(view.height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    view.translationY = 0f
                    endAction?.invoke()
                }
            })
    }
    
    /**
     * Rotate animation
     */
    fun rotate(view: View, fromDegrees: Float, toDegrees: Float, duration: Long = 300) {
        view.animate()
            .rotation(toDegrees)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
