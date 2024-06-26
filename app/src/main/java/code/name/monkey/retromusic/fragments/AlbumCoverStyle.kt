
package code.name.monkey.retromusic.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import code.name.monkey.retromusic.R

enum class AlbumCoverStyle(
    @StringRes val titleRes: Int,
    @DrawableRes val drawableResId: Int,
    val id: Int
) {
    Card(R.string.card, R.drawable.np_blur_card, 3),
    Circle(R.string.circular, R.drawable.np_circle, 2),
    Flat(R.string.flat, R.drawable.np_flat, 1),
    FullCard(R.string.full_card, R.drawable.np_adaptive, 5),
    Full(R.string.full, R.drawable.np_full, 4),
    Normal(R.string.normal, R.drawable.np_normal, 0),
}
