package com.example.myapplication

import android.content.res.Configuration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

/**
 * Aplica un tamaño compacto a la barra de navegación inferior en modo landscape.
 * NO se toca la altura: BottomNavigationView gestiona su propio mínimo interno.
 * Solo se reducen los iconos y se ocultan las etiquetas.
 */
object BottomNavHelper {

    fun apply(bottomNav: BottomNavigationView) {
        val isLandscape = bottomNav.resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Ocultar etiquetas de texto para ganar espacio vertical
            bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED
            // Reducir iconos a 20dp en landscape
            val iconSizePx = bottomNav.resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size)
            bottomNav.itemIconSize = iconSizePx
        } else {
            // Portrait: etiquetas visibles e iconos a tamaño normal (24dp)
            bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
            bottomNav.itemIconSize =
                bottomNav.resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size_portrait)
        }
    }
}
