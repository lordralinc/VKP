package dev.idm.vkp.api.model.feedback;

import dev.idm.vkp.api.model.Commentable;
import dev.idm.vkp.api.model.VKApiComment;

public class VkApiMentionCommentFeedback extends VkApiBaseFeedback {

    public VKApiComment where;
    public Commentable comment_of;

}
