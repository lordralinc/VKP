package dev.idm.vkp.mvp.view

import dev.idm.vkp.model.Message
import dev.idm.vkp.model.Peer
import dev.idm.vkp.mvp.core.IMvpView
import dev.idm.vkp.mvp.view.base.IAccountDependencyView

interface ILocalJsonToChatView : IAccountDependencyView, IMvpView, IErrorView,
    IAttachmentsPlacesView {
    fun displayData(posts: List<Message>)
    fun notifyDataSetChanged()
    fun notifyDataAdded(position: Int, count: Int)
    fun showRefreshing(refreshing: Boolean)
    fun setToolbarTitle(title: String?)
    fun setToolbarSubtitle(subtitle: String?)
    fun scroll_pos(pos: Int)
    fun displayToolbarAvatar(peer: Peer)
    fun attachments_mode(accountId: Int, last_selected: Int)
}