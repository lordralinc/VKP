package dev.idm.vkp.domain.impl;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.feedback.VkApiBaseFeedback;
import dev.idm.vkp.db.interfaces.IStorages;
import dev.idm.vkp.db.model.IdPairEntity;
import dev.idm.vkp.db.model.entity.OwnerEntities;
import dev.idm.vkp.db.model.entity.feedback.CopyEntity;
import dev.idm.vkp.db.model.entity.feedback.FeedbackEntity;
import dev.idm.vkp.db.model.entity.feedback.LikeCommentEntity;
import dev.idm.vkp.db.model.entity.feedback.LikeEntity;
import dev.idm.vkp.db.model.entity.feedback.MentionCommentEntity;
import dev.idm.vkp.db.model.entity.feedback.MentionEntity;
import dev.idm.vkp.db.model.entity.feedback.NewCommentEntity;
import dev.idm.vkp.db.model.entity.feedback.PostFeedbackEntity;
import dev.idm.vkp.db.model.entity.feedback.ReplyCommentEntity;
import dev.idm.vkp.db.model.entity.feedback.UsersEntity;
import dev.idm.vkp.domain.IFeedbackInteractor;
import dev.idm.vkp.domain.IOwnersRepository;
import dev.idm.vkp.domain.mappers.Dto2Entity;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.domain.mappers.Entity2Model;
import dev.idm.vkp.domain.mappers.FeedbackEntity2Model;
import dev.idm.vkp.model.AnswerVKOfficialList;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.criteria.NotificationsCriteria;
import dev.idm.vkp.model.feedback.Feedback;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.util.VKOwnIds;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.domain.mappers.Entity2Model.fillCommentOwnerIds;
import static dev.idm.vkp.util.Utils.isEmpty;

public class FeedbackInteractor implements IFeedbackInteractor {

    private final IStorages cache;
    private final INetworker networker;
    private final IOwnersRepository ownersRepository;

    public FeedbackInteractor(IStorages cache, INetworker networker, IOwnersRepository ownersRepository) {
        this.cache = cache;
        this.networker = networker;
        this.ownersRepository = ownersRepository;
    }

    private static void populateOwnerIds(VKOwnIds ids, FeedbackEntity dbo) {
        fillCommentOwnerIds(ids, dbo.getReply());

        if (dbo instanceof CopyEntity) {
            populateOwnerIds(ids, (CopyEntity) dbo);
        } else if (dbo instanceof LikeCommentEntity) {
            populateOwnerIds(ids, (LikeCommentEntity) dbo);
        } else if (dbo instanceof LikeEntity) {
            populateOwnerIds(ids, (LikeEntity) dbo);
        } else if (dbo instanceof MentionCommentEntity) {
            populateOwnerIds(ids, (MentionCommentEntity) dbo);
        } else if (dbo instanceof MentionEntity) {
            populateOwnerIds(ids, (MentionEntity) dbo);
        } else if (dbo instanceof NewCommentEntity) {
            populateOwnerIds(ids, (NewCommentEntity) dbo);
        } else if (dbo instanceof PostFeedbackEntity) {
            populateOwnerIds(ids, (PostFeedbackEntity) dbo);
        } else if (dbo instanceof ReplyCommentEntity) {
            populateOwnerIds(ids, (ReplyCommentEntity) dbo);
        } else if (dbo instanceof UsersEntity) {
            populateOwnerIds(ids, (UsersEntity) dbo);
        }
    }

    private static void populateOwnerIds(VKOwnIds ids, UsersEntity dbo) {
        ids.appendAll(dbo.getOwners());
    }

    private static void populateOwnerIds(VKOwnIds ids, ReplyCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        Entity2Model.fillOwnerIds(ids, dbo.getFeedbackComment());
        Entity2Model.fillOwnerIds(ids, dbo.getOwnComment());
    }

    private static void populateOwnerIds(VKOwnIds ids, PostFeedbackEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getPost());
    }

    private static void populateOwnerIds(VKOwnIds ids, NewCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getComment());
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
    }

    private static void populateOwnerIds(VKOwnIds ids, MentionEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getWhere());
    }

    private static void populateOwnerIds(VKOwnIds ids, MentionCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        Entity2Model.fillOwnerIds(ids, dbo.getWhere());
    }

    private static void populateOwnerIds(VKOwnIds ids, LikeEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getLiked());
        ids.appendAll(dbo.getLikesOwnerIds());
    }

    private static void populateOwnerIds(VKOwnIds ids, LikeCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getLiked());
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        ids.appendAll(dbo.getLikesOwnerIds());
    }

    private static void populateOwnerIds(VKOwnIds ids, CopyEntity dbo) {
        for (IdPairEntity i : dbo.getCopies().getPairDbos()) {
            ids.append(i.getOwnerId());
        }

        Entity2Model.fillOwnerIds(ids, dbo.getCopied());
    }

    @Override
    public Single<List<Feedback>> getCachedFeedbacks(int accountId) {
        NotificationsCriteria criteria = new NotificationsCriteria(accountId);
        return getCachedFeedbacksByCriteria(criteria);
    }

    @Override
    public Single<AnswerVKOfficialList> getOfficial(int accountId, Integer count, Integer startFrom) {
        return networker.vkDefault(accountId)
                .notifications()
                .getOfficial(count, startFrom, null, null, null)
                .map(response -> response);
    }

    @Override
    public Single<Pair<List<Feedback>, String>> getActualFeedbacks(int accountId, int count, String startFrom) {
        return networker.vkDefault(accountId)
                .notifications()
                .get(count, startFrom, null, null, null)
                .flatMap(response -> {
                    List<VkApiBaseFeedback> dtos = Utils.listEmptyIfNull(response.notifications);
                    List<FeedbackEntity> dbos = new ArrayList<>(dtos.size());

                    VKOwnIds ownIds = new VKOwnIds();

                    for (VkApiBaseFeedback dto : dtos) {
                        FeedbackEntity dbo = Dto2Entity.buildFeedbackDbo(dto);
                        populateOwnerIds(ownIds, dbo);
                        dbos.add(dbo);
                    }

                    OwnerEntities ownerEntities = Dto2Entity.mapOwners(response.profiles, response.groups);
                    List<Owner> owners = Dto2Model.transformOwners(response.profiles, response.groups);

                    return cache.notifications()
                            .insert(accountId, dbos, ownerEntities, isEmpty(startFrom))
                            .flatMap(ints -> ownersRepository
                                    .findBaseOwnersDataAsBundle(accountId, ownIds.getAll(), IOwnersRepository.MODE_ANY, owners)
                                    .map(ownersBundle -> {
                                        List<Feedback> feedbacks = new ArrayList<>(dbos.size());

                                        for (FeedbackEntity dbo : dbos) {
                                            feedbacks.add(FeedbackEntity2Model.buildFeedback(dbo, ownersBundle));
                                        }

                                        return Pair.Companion.create(feedbacks, response.nextFrom);
                                    }));
                });
    }

    @Override
    public Completable maskAaViewed(int accountId) {
        return networker.vkDefault(accountId)
                .notifications()
                .markAsViewed()
                .ignoreElement();
    }

    private Single<List<Feedback>> getCachedFeedbacksByCriteria(NotificationsCriteria criteria) {
        return cache.notifications()
                .findByCriteria(criteria)
                .flatMap(dbos -> {
                    VKOwnIds ownIds = new VKOwnIds();

                    for (FeedbackEntity dbo : dbos) {
                        populateOwnerIds(ownIds, dbo);
                    }

                    return ownersRepository.findBaseOwnersDataAsBundle(criteria.getAccountId(), ownIds.getAll(), IOwnersRepository.MODE_ANY)
                            .map(owners -> {
                                List<Feedback> feedbacks = new ArrayList<>(dbos.size());
                                for (FeedbackEntity dbo : dbos) {
                                    feedbacks.add(FeedbackEntity2Model.buildFeedback(dbo, owners));
                                }
                                return feedbacks;
                            });
                });
    }
}