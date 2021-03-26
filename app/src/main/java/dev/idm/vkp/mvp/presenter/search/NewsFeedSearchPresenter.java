package dev.idm.vkp.mvp.presenter.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.Injection;
import dev.idm.vkp.api.model.VKApiPost;
import dev.idm.vkp.db.model.PostUpdate;
import dev.idm.vkp.domain.IFeedInteractor;
import dev.idm.vkp.domain.IWallsRepository;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.fragment.search.criteria.NewsFeedCriteria;
import dev.idm.vkp.fragment.search.nextfrom.StringNextFrom;
import dev.idm.vkp.model.Commented;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.mvp.view.search.INewsFeedSearchView;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

public class NewsFeedSearchPresenter extends AbsSearchPresenter<INewsFeedSearchView, NewsFeedCriteria, Post, StringNextFrom> {

    private final IFeedInteractor feedInteractor;

    private final IWallsRepository walls;

    public NewsFeedSearchPresenter(int accountId, @Nullable NewsFeedCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        feedInteractor = InteractorFactory.createFeedInteractor();
        walls = Repository.INSTANCE.getWalls();

        appendDisposable(walls.observeMinorChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onPostUpdate, RxUtils.ignore()));
    }

    private void onPostUpdate(PostUpdate update) {
        // TODO: 03.10.2017
    }

    @Override
    StringNextFrom getInitialNextFrom() {
        return new StringNextFrom(null);
    }

    @Override
    boolean isAtLast(StringNextFrom startFrom) {
        return Utils.isEmpty(startFrom.getNextFrom());
    }

    @Override
    Single<Pair<List<Post>, StringNextFrom>> doSearch(int accountId, NewsFeedCriteria criteria, StringNextFrom startFrom) {
        return feedInteractor.search(accountId, criteria, 50, startFrom.getNextFrom())
                .map(pair -> Pair.Companion.create(pair.getFirst(), new StringNextFrom(pair.getSecond())));
    }

    @Override
    NewsFeedCriteria instantiateEmptyCriteria() {
        return new NewsFeedCriteria("");
    }

    @Override
    public void firePostClick(@NonNull Post post) {
        if (post.getPostType() == VKApiPost.Type.REPLY) {
            getView().openComments(getAccountId(), Commented.from(post), post.getVkid());
        } else {
            getView().openPost(getAccountId(), post);
        }
    }

    @Override
    boolean canSearch(NewsFeedCriteria criteria) {
        return Utils.nonEmpty(criteria.getQuery());
    }

    public void fireLikeClick(Post post) {
        int accountId = getAccountId();

        appendDisposable(walls.like(accountId, post.getOwnerId(), post.getVkid(), !post.isUserLikes())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(RxUtils.ignore(), t -> showError(getView(), t)));
    }
}