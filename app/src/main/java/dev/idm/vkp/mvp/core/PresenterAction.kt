package dev.idm.vkp.mvp.core

interface PresenterAction<P : IPresenter<V>, V : IMvpView> {
    fun call(presenter: P)
}