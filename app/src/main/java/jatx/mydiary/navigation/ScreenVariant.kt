package jatx.mydiary.navigation

sealed interface ScreenVariant {
    object MainScreenVariant: ScreenVariant
    object AuthScreenVariant: ScreenVariant
}