package com.example.myapplication

import android.content.res.Configuration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

/**
 * Aplica un tamaño compacto a la barra de navegación inferior en modo landscape.
 * En portrait se deja el comportamiento por defecto (wrap_content + labels visibles).
 */
object BottomNavHelper {

    fun apply(bottomNav: BottomNavigationView) {
        val isLandscape = bottomNav.resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Ocultar etiquetas para ganar altura
            bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED

            // Reducir tamaño de iconos
            val iconSizePx = bottomNav.resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size)
            bottomNav.itemIconSize = iconSizePx

            // Forzar altura más pequeña
            val heightPx = bottomNav.resources.getDimensionPixelSize(R.dimen.bottom_nav_height)
            val lp = bottomNav.layoutParams
            lp.height = heightPx
            bottomNav.layoutParams = lp
        } else {
            // Portrait: valores por defecto
            bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
            bottomNav.itemIconSize =
                bottomNav.resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size_portrait)
            val lp = bottomNav.layoutParams
            lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            bottomNav.layoutParams = lp
        }
    }
}

