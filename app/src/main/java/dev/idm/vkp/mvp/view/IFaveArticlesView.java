package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.Article;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;


public interface IFaveArticlesView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Article> articles);

    void notifyDataSetChanged();

    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);

    void goToArticle(int accountId, String url);

    void goToPhoto(int accountId, Photo photo);
}
