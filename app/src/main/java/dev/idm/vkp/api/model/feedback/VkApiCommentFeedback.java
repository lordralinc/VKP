package dev.idm.vkp.api.model.feedback;

import dev.idm.vkp.api.model.Commentable;
import dev.idm.vkp.api.model.VKApiComment;

public class VkApiCommentFeedback extends VkApiBaseFeedback {
    public Commentable comment_of;
    public VKApiComment comment;
}