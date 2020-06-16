package studio.codable.unpause.utilities.extensions

import android.animation.ObjectAnimator
import android.view.View
import android.widget.TextView

fun TextView.crossFadeText(newText: String?, duration: Long = 1000) {

    ObjectAnimator.ofFloat(this, "alpha", this.alpha, 0f)
        .apply {
            this.duration = duration
            start()
        }

    this.text = newText

    ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        .apply {
            this.duration = duration
            start()
        }

}