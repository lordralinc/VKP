package dev.idm.vkp.mvp.view.search;

import dev.idm.vkp.model.Community;


public interface ICommunitiesSearchView extends IBaseSearchView<Community> {
    void openCommunityWall(int accountId, Community community);
}