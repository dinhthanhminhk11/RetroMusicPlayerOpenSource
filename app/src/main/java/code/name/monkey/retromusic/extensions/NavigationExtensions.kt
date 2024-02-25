
package code.name.monkey.retromusic.extensions

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import code.name.monkey.retromusic.R

fun Fragment.navigate(@IdRes id: Int) = findNavController().navigate(id)

fun Fragment.findNavController(@IdRes id: Int): NavController {
    val fragment = childFragmentManager.findFragmentById(id) as NavHostFragment
    return fragment.navController
}

fun Fragment.findActivityNavController(@IdRes id: Int): NavController {
    return requireActivity().findNavController(id)
}

fun AppCompatActivity.findNavController(@IdRes id: Int): NavController {
    val fragment = supportFragmentManager.findFragmentById(id) as NavHostFragment
    return fragment.navController
}

 val fadeNavOptionsInOut
    get() = navOptions {
        anim {
            enter = android.R.anim.fade_in
            exit = android.R.anim.fade_out
            popEnter = android.R.anim.fade_in
            popExit = android.R.anim.fade_out
        }
    }

val navOptionsOpen by lazy {
    navOptions {
        launchSingleTop = false
        anim {
            enter = R.anim.retro_fragment_open_enter
            exit = R.anim.retro_fragment_open_exit
            popEnter = R.anim.retro_fragment_close_enter
            popExit = R.anim.retro_fragment_close_exit
        }
    }
}

fun NavController.findNavControllerOpen(
    resId: Int,
    direction: NavDirections? = null,
) {
    val defaultNavOptions = navOptionsOpen
    if (direction != null) {
        navigate(resId, direction.arguments, defaultNavOptions)
    } else {
        navigate(resId, null, defaultNavOptions)
    }
}

fun NavController.findNavControllerInOut(
    resId: Int,
    direction: NavDirections? = null,
) {
    val defaultNavOptions = fadeNavOptionsInOut
    if (direction != null) {
        navigate(resId, direction.arguments, defaultNavOptions)
    } else {
        navigate(resId, null, defaultNavOptions)
    }
}