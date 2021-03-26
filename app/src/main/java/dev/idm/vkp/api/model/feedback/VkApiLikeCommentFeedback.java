package dev.idm.vkp.api.model.feedback;

import dev.idm.vkp.api.model.Commentable;
import dev.idm.vkp.api.model.VKApiComment;

public class VkApiLikeCommentFeedback extends VkApiBaseFeedback {

    public UserArray users;

    public VKApiComment comment;

    public Commentable commented;
}
