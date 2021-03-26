package dev.idm.vkp.mvp.core

interface ViewAction<V> {
    fun call(view: V)
}