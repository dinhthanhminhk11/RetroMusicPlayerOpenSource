package code.name.monkey.retromusic.util.extention

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

fun TextView.setUnderlinedText(text: CharSequence) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    this.text = spannableString
}
