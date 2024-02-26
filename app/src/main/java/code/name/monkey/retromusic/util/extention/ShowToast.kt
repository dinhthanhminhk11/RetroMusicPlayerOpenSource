package code.name.monkey.retromusic.util.extention

import androidx.fragment.app.FragmentActivity
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.views.toast.CookieBar

fun showToastSuccess(activity: FragmentActivity, title: String, content: String) {
    CookieBar.build(activity)
        .setTitle(title)
        .setMessage(content)
        .setIcon(R.drawable.ic_complete_order).setTitleColor(R.color.black_color)
        .setMessageColor(R.color.black_color).setDuration(3000)
        .setBackgroundRes(R.drawable.background_toast)
        .setCookiePosition(CookieBar.BOTTOM).show()
}

fun showToastError(activity: FragmentActivity?, title: String, content: String) {
    CookieBar.build(activity)
        .setTitle(title)
        .setMessage(content)
        .setIcon(R.drawable.ic_warning_icon_check).setTitleColor(R.color.black_color)
        .setMessageColor(R.color.black_color).setDuration(3000)
        .setBackgroundRes(R.drawable.background_toast)
        .setCookiePosition(CookieBar.BOTTOM).show()
}