package dev.idm.vkp.mvp.core

interface IPresenterFactory<T : IPresenter<*>> {
    fun create(): T
}