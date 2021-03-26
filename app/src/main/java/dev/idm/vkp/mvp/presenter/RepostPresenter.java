package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.IWallsRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.AttachmenEntry;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.IRepostView;
import dev.idm.vkp.util.RxUtils;


public class RepostPresenter extends AbsAttachmentsEditPresenter<IRepostView> {

    private final Post post;
    private final Integer targetGroupId;
    private final IWallsRepository walls;
    private boolean publishingNow;

    public RepostPresenter(int accountId, Post post, Integer targetGroupId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        walls = Repository.INSTANCE.getWalls();
        this.post = post;
        this.targetGroupId = targetGroupId;

        getData().add(new AttachmenEntry(false, post));
    }

    @Override
    public void onGuiCreated(@NonNull IRepostView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.setSupportedButtons(false, false, false, false, false, false);
    }

    @OnGuiCreated
    private void resolveProgressDialog() {
        if (isGuiReady()) {
            if (publishingNow) {
                getView().displayProgressDialog(R.string.please_wait, R.string.publication, false);
            } else {
                getView().dismissProgressDialog();
            }
        }
    }

    private void setPublishingNow(boolean publishingNow) {
        this.publishingNow = publishingNow;
        resolveProgressDialog();
    }

    private void onPublishError(Throwable throwable) {
        setPublishingNow(false);
        showError(getView(), throwable);
    }

    @SuppressWarnings("unused")
    private void onPublishComplete(Post post) {
        setPublishingNow(false);
        getView().goBack();
    }

    public final void fireReadyClick() {
        setPublishingNow(true);

        int accountId = getAccountId();
        String body = getTextBody();

        appendDisposable(walls.repost(accountId, post.getVkid(), post.getOwnerId(), targetGroupId, body)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onPublishComplete, this::onPublishError));
    }
}