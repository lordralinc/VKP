package dev.idm.vkp.mvp.presenter.search;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.Injection;
import dev.idm.vkp.db.model.PostUpdate;
import dev.idm.vkp.domain.ILikesInteractor;
import dev.idm.vkp.domain.IWallsRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.fragment.search.criteria.WallSearchCriteria;
import dev.idm.vkp.fragment.search.nextfrom.IntNextFrom;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.mvp.view.search.IBaseSearchView;
import dev.idm.vkp.mvp.view.search.IWallSearchView;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.isNull;
import static dev.idm.vkp.util.Objects.nonNull;

public class WallSearchPresenter extends AbsSearchPresenter<IWallSearchView, WallSearchCriteria, Post, IntNextFrom> {

    private static final int COUNT = 30;
    private final IWallsRepository walls;

    public WallSearchPresenter(int accountId, @Nullable WallSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        walls = Repository.INSTANCE.getWalls();

        appendDisposable(walls.observeMinorChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onPostMinorUpdates));
    }

    private void onPostMinorUpdates(PostUpdate update) {
        for (int i = 0; i < data.size(); i++) {
            Post post = data.get(i);

            if (post.getVkid() == update.getPostId() && post.getOwnerId() == update.getOwnerId()) {
                if (nonNull(update.getLikeUpdate())) {
                    post.setLikesCount(update.getLikeUpdate().getCount());
                    post.setUserLikes(update.getLikeUpdate().isLiked());
                }

                if (nonNull(update.getDeleteUpdate())) {
                    post.setDeleted(update.getDeleteUpdate().isDeleted());
                }

                boolean pinStateChanged = false;

                if (nonNull(update.getPinUpdate())) {
                    pinStateChanged = true;

                    for (Post p : data) {
                        p.setPinned(false);
                    }

                    post.setPinned(update.getPinUpdate().isPinned());
                }

                if (pinStateChanged) {
                    callView(IBaseSearchView::notifyDataSetChanged);
                } else {
                    int finalI = i;
                    callView(view -> view.notifyItemChanged(finalI));
                }

                break;
            }
        }
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    Single<Pair<List<Post>, IntNextFrom>> doSearch(int accountId, WallSearchCriteria criteria, IntNextFrom startFrom) {
        int offset = isNull(startFrom) ? 0 : startFrom.getOffset();
        IntNextFrom nextFrom = new IntNextFrom(offset + COUNT);

        return walls.search(accountId, criteria.getOwnerId(), criteria.getQuery(), true, COUNT, offset)
                .map(pair -> Pair.Companion.create(pair.getFirst(), nextFrom));
    }

    @Override
    WallSearchCriteria instantiateEmptyCriteria() {
        // not supported
        throw new UnsupportedOperationException();
    }

    @Override
    boolean canSearch(WallSearchCriteria criteria) {
        return Utils.trimmedNonEmpty(criteria.getQuery());
    }

    public final void fireShowCopiesClick(Post post) {
        fireCopiesLikesClick("post", post.getOwnerId(), post.getVkid(), ILikesInteractor.FILTER_COPIES);
    }

    public final void fireShowLikesClick(Post post) {
        fireCopiesLikesClick("post", post.getOwnerId(), post.getVkid(), ILikesInteractor.FILTER_LIKES);
    }

    public void fireLikeClick(Post post) {
        int accountId = getAccountId();

        appendDisposable(walls.like(accountId, post.getOwnerId(), post.getVkid(), !post.isUserLikes())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(RxUtils.ignore(), t -> showError(getView(), t)));
    }
}