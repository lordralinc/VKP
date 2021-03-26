package dev.idm.vkp.domain.impl;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.VKApiOwner;
import dev.idm.vkp.domain.ILikesInteractor;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;


public class LikesInteractor implements ILikesInteractor {

    private final INetworker networker;

    public LikesInteractor(INetworker networker) {
        this.networker = networker;
    }

    @Override
    public Single<List<Owner>> getLikes(int accountId, String type, int ownerId, int itemId, String filter, int count, int offset) {
        return networker.vkDefault(accountId)
                .likes()
                .getList(type, ownerId, itemId, null, filter, null, offset, count, null, Constants.MAIN_OWNER_FIELDS)
                .map(response -> {
                    List<VKApiOwner> dtos = Utils.listEmptyIfNull(response.owners);
                    List<Owner> owners = new ArrayList<>(dtos.size());

                    for (VKApiOwner dto : dtos) {
                        owners.add(Dto2Model.transformOwner(dto));
                    }

                    return owners;
                });
    }
}