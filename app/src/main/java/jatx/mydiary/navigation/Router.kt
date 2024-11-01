package jatx.mydiary.navigation

import androidx.compose.runtime.mutableStateListOf

object Router {
    private val stack = mutableStateListOf<ScreenVariant>(ScreenVariant.MainScreenVariant)

    fun push(screenVariant: ScreenVariant) = stack.add(screenVariant)

    fun pop() = stack.removeLast()

    val currentScreenVariant: ScreenVariant
        get() = stack.last()
}