package dev.idm.vkp.model.criteria;

import dev.idm.vkp.db.DatabaseIdRange;
import dev.idm.vkp.model.Commented;

public class CommentsCriteria {

    private final Commented commented;

    private final int accountId;
    public DatabaseIdRange range;

    public CommentsCriteria(int accountId, Commented commented) {
        this.accountId = accountId;
        this.commented = commented;
    }

    public Commented getCommented() {
        return commented;
    }

    public int getAccountId() {
        return accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public CommentsCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }
}