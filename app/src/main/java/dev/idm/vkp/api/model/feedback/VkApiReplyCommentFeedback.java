package dev.idm.vkp.api.model.feedback;

import dev.idm.vkp.api.model.Commentable;
import dev.idm.vkp.api.model.VKApiComment;

public class VkApiReplyCommentFeedback extends VkApiBaseFeedback {

    public Commentable comments_of;

    public VKApiComment own_comment;

    public VKApiComment feedback_comment;
}
