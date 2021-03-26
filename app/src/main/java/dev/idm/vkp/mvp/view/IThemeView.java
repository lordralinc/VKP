package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.ThemeValue;
import dev.idm.vkp.mvp.core.IMvpView;


public interface IThemeView extends IMvpView {
    void displayData(List<ThemeValue> data);
}
