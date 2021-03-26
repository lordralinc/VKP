package dev.idm.vkp.model.feedback;

import android.os.Parcel;

import dev.idm.vkp.model.AbsModel;
import dev.idm.vkp.model.Comment;
import dev.idm.vkp.model.ParcelableModelWrapper;

public final class CommentFeedback extends Feedback {

    public static final Creator<CommentFeedback> CREATOR = new Creator<CommentFeedback>() {
        @Override
        public CommentFeedback createFromParcel(Parcel in) {
            return new CommentFeedback(in);
        }

        @Override
        public CommentFeedback[] newArray(int size) {
            return new CommentFeedback[size];
        }
    };
    private AbsModel commentOf;
    private Comment comment;

    public CommentFeedback(@FeedbackType int type) {
        super(type);
    }

    private CommentFeedback(Parcel in) {
        super(in);
        commentOf = ParcelableModelWrapper.readModel(in);
        comment = in.readParcelable(Comment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelableModelWrapper.writeModel(dest, flags, commentOf);
        dest.writeParcelable(comment, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public AbsModel getCommentOf() {
        return commentOf;
    }

    public CommentFeedback setCommentOf(AbsModel commentOf) {
        this.commentOf = commentOf;
        return this;
    }

    public Comment getComment() {
        return comment;
    }

    public CommentFeedback setComment(Comment comment) {
        this.comment = comment;
        return this;
    }
}