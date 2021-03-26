package dev.idm.vkp.activity;

import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;

public interface ProfileSelectable {

    void select(Owner owner);

    SelectProfileCriteria getAcceptableCriteria();
}