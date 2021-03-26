package dev.idm.vkp.mvp.presenter.search;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.List;

import dev.idm.vkp.domain.IMessagesRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.fragment.search.criteria.MessageSeachCriteria;
import dev.idm.vkp.fragment.search.nextfrom.IntNextFrom;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.mvp.view.search.IMessagesSearchView;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Utils.trimmedNonEmpty;

public class MessagesSearchPresenter extends AbsSearchPresenter<IMessagesSearchView, MessageSeachCriteria, Message, IntNextFrom> {

    private static final int COUNT = 50;
    private final IMessagesRepository messagesInteractor;

    public MessagesSearchPresenter(int accountId, @Nullable MessageSeachCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        messagesInteractor = Repository.INSTANCE.getMessages();

        if (canSearch(getCriteria())) {
            doSearch();
        }
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    Single<Pair<List<Message>, IntNextFrom>> doSearch(int accountId, MessageSeachCriteria criteria, IntNextFrom nextFrom) {
        int offset = Objects.isNull(nextFrom) ? 0 : nextFrom.getOffset();
        return messagesInteractor
                .searchMessages(accountId, criteria.getPeerId(), COUNT, offset, criteria.getQuery())
                .map(messages -> Pair.Companion.create(messages, new IntNextFrom(offset + COUNT)));
    }

    @Override
    MessageSeachCriteria instantiateEmptyCriteria() {
        return new MessageSeachCriteria("");
    }

    @Override
    boolean canSearch(MessageSeachCriteria criteria) {
        return trimmedNonEmpty(criteria.getQuery());
    }

    public void fireMessageClick(Message message) {
        getView().goToMessagesLookup(getAccountId(), message.getPeerId(), message.getId());
    }
}
