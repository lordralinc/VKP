package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.DrawerCategory;
import dev.idm.vkp.mvp.core.IMvpView;


public interface IDrawerEditView extends IMvpView {
    void displayData(List<DrawerCategory> data);

    void goBackAndApplyChanges();
}