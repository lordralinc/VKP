package dev.idm.vkp.mvp.view.search;

import dev.idm.vkp.model.User;


public interface IPeopleSearchView extends IBaseSearchView<User> {
    void openUserWall(int accountId, User user);
}