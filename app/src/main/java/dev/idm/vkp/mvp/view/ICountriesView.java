package dev.idm.vkp.mvp.view;

import java.util.List;

import dev.idm.vkp.model.database.Country;
import dev.idm.vkp.mvp.core.IMvpView;


public interface ICountriesView extends IMvpView, IErrorView {
    void displayData(List<Country> countries);

    void notifyDataSetChanged();

    void displayLoading(boolean loading);

    void returnSelection(Country country);
}