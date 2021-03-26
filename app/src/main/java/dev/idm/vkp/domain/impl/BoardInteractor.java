package dev.idm.vkp.domain.impl;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.VKApiTopic;
import dev.idm.vkp.db.interfaces.IStorages;
import dev.idm.vkp.db.model.entity.OwnerEntities;
import dev.idm.vkp.db.model.entity.TopicEntity;
import dev.idm.vkp.domain.IBoardInteractor;
import dev.idm.vkp.domain.IOwnersRepository;
import dev.idm.vkp.domain.mappers.Dto2Entity;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.domain.mappers.Entity2Model;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.Topic;
import dev.idm.vkp.model.criteria.TopicsCriteria;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.util.VKOwnIds;
import io.reactivex.rxjava3.core.Single;


public class BoardInteractor implements IBoardInteractor {

    private final INetworker networker;
    private final IStorages stores;
    private final IOwnersRepository ownersRepository;

    public BoardInteractor(INetworker networker, IStorages stores, IOwnersRepository ownersRepository) {
        this.networker = networker;
        this.stores = stores;
        this.ownersRepository = ownersRepository;
    }

    @Override
    public Single<List<Topic>> getCachedTopics(int accountId, int ownerId) {
        TopicsCriteria criteria = new TopicsCriteria(accountId, ownerId);
        return stores.topics()
                .getByCriteria(criteria)
                .flatMap(dbos -> {
                    VKOwnIds ids = new VKOwnIds();
                    for (TopicEntity dbo : dbos) {
                        ids.append(dbo.getCreatorId());
                        ids.append(dbo.getUpdatedBy());
                    }

                    return ownersRepository.findBaseOwnersDataAsBundle(accountId, ids.getAll(), IOwnersRepository.MODE_ANY)
                            .map(owners -> {
                                List<Topic> topics = new ArrayList<>(dbos.size());
                                for (TopicEntity dbo : dbos) {
                                    topics.add(Entity2Model.buildTopicFromDbo(dbo, owners));
                                }
                                return topics;
                            });
                });
    }

    //public static final int ORDER_DESCENDING_UPDATE_TIME = 1;
    //public static final int ORDER_DESCENDING_CREATE_TIME = 2;
    //public static final int ORDER_ASCENDING_UPDATE_TIME = -1;
    //public static final int ORDER_ASCENDING_CREATE_TIME = -2;

    @Override
    public Single<List<Topic>> getActualTopics(int accountId, int ownerId, int count, int offset) {
        return networker.vkDefault(accountId)
                .board()
                .getTopics(Math.abs(ownerId), null, null, offset, count, true, null, null, Constants.MAIN_OWNER_FIELDS)
                .flatMap(response -> {
                    List<VKApiTopic> dtos = Utils.listEmptyIfNull(response.items);
                    List<TopicEntity> dbos = new ArrayList<>(dtos.size());

                    for (VKApiTopic dto : dtos) {
                        dbos.add(Dto2Entity.buildTopicDbo(dto));
                    }

                    OwnerEntities ownerEntities = Dto2Entity.mapOwners(response.profiles, response.groups);

                    VKOwnIds ownerIds = new VKOwnIds();
                    for (TopicEntity dbo : dbos) {
                        ownerIds.append(dbo.getCreatorId());
                        ownerIds.append(dbo.getUpdatedBy());
                    }

                    List<Owner> owners = Dto2Model.transformOwners(response.profiles, response.groups);

                    return stores.topics()
                            .store(accountId, ownerId, dbos, ownerEntities, response.canAddTopics == 1, response.defaultOrder, offset == 0)
                            .andThen(ownersRepository.findBaseOwnersDataAsBundle(accountId, ownerIds.getAll(), IOwnersRepository.MODE_ANY, owners)
                                    .map(ownersBundle -> {
                                        List<Topic> topics = new ArrayList<>(dbos.size());
                                        for (TopicEntity dbo : dbos) {
                                            topics.add(Entity2Model.buildTopicFromDbo(dbo, ownersBundle));
                                        }
                                        return topics;
                                    }));
                });
    }
}