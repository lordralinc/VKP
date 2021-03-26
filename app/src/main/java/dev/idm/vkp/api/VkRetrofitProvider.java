package dev.idm.vkp.api;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dev.idm.vkp.api.adapters.AnswerVKOfficialDtoAdapter;
import dev.idm.vkp.api.adapters.ArticleDtoAdapter;
import dev.idm.vkp.api.adapters.AttachmentsDtoAdapter;
import dev.idm.vkp.api.adapters.AttachmentsEntryDtoAdapter;
import dev.idm.vkp.api.adapters.AudioDtoAdapter;
import dev.idm.vkp.api.adapters.AudioPlaylistDtoAdapter;
import dev.idm.vkp.api.adapters.BooleanAdapter;
import dev.idm.vkp.api.adapters.ChatDtoAdapter;
import dev.idm.vkp.api.adapters.ChatUserDtoAdapter;
import dev.idm.vkp.api.adapters.ChatsInfoAdapter;
import dev.idm.vkp.api.adapters.CommentDtoAdapter;
import dev.idm.vkp.api.adapters.CommunityDtoAdapter;
import dev.idm.vkp.api.adapters.CustomCommentsResponseAdapter;
import dev.idm.vkp.api.adapters.DocsEntryDtoAdapter;
import dev.idm.vkp.api.adapters.FaveLinkDtoAdapter;
import dev.idm.vkp.api.adapters.FeedbackDtoAdapter;
import dev.idm.vkp.api.adapters.FeedbackUserArrayDtoAdapter;
import dev.idm.vkp.api.adapters.GroupSettingsAdapter;
import dev.idm.vkp.api.adapters.JsonStringDtoAdapter;
import dev.idm.vkp.api.adapters.LikesListAdapter;
import dev.idm.vkp.api.adapters.LongpollUpdateAdapter;
import dev.idm.vkp.api.adapters.MarketDtoAdapter;
import dev.idm.vkp.api.adapters.MessageDtoAdapter;
import dev.idm.vkp.api.adapters.NewsAdapter;
import dev.idm.vkp.api.adapters.NewsfeedCommentDtoAdapter;
import dev.idm.vkp.api.adapters.PhotoAlbumDtoAdapter;
import dev.idm.vkp.api.adapters.PhotoDtoAdapter;
import dev.idm.vkp.api.adapters.PostDtoAdapter;
import dev.idm.vkp.api.adapters.PostSourceDtoAdapter;
import dev.idm.vkp.api.adapters.PrivacyDtoAdapter;
import dev.idm.vkp.api.adapters.ProfileInfoResponceDtoAdapter;
import dev.idm.vkp.api.adapters.SchoolClazzDtoAdapter;
import dev.idm.vkp.api.adapters.StoryDtoAdapter;
import dev.idm.vkp.api.adapters.TopicDtoAdapter;
import dev.idm.vkp.api.adapters.UserDtoAdapter;
import dev.idm.vkp.api.adapters.VKApiCatalogLinkDtoAdapter;
import dev.idm.vkp.api.adapters.VideoAlbumDtoAdapter;
import dev.idm.vkp.api.adapters.VideoDtoAdapter;
import dev.idm.vkp.api.adapters.local_json.ChatJsonResponseDtoAdapter;
import dev.idm.vkp.api.model.ChatUserDto;
import dev.idm.vkp.api.model.FaveLinkDto;
import dev.idm.vkp.api.model.GroupSettingsDto;
import dev.idm.vkp.api.model.VKApiArticle;
import dev.idm.vkp.api.model.VKApiAudio;
import dev.idm.vkp.api.model.VKApiAudioPlaylist;
import dev.idm.vkp.api.model.VKApiCatalogLink;
import dev.idm.vkp.api.model.VKApiChat;
import dev.idm.vkp.api.model.VKApiComment;
import dev.idm.vkp.api.model.VKApiCommunity;
import dev.idm.vkp.api.model.VKApiMessage;
import dev.idm.vkp.api.model.VKApiNews;
import dev.idm.vkp.api.model.VKApiPhoto;
import dev.idm.vkp.api.model.VKApiPhotoAlbum;
import dev.idm.vkp.api.model.VKApiPost;
import dev.idm.vkp.api.model.VKApiStory;
import dev.idm.vkp.api.model.VKApiTopic;
import dev.idm.vkp.api.model.VKApiUser;
import dev.idm.vkp.api.model.VKApiVideo;
import dev.idm.vkp.api.model.VKApiVideoAlbum;
import dev.idm.vkp.api.model.VkApiAttachments;
import dev.idm.vkp.api.model.VkApiDoc;
import dev.idm.vkp.api.model.VkApiJsonString;
import dev.idm.vkp.api.model.VkApiMarket;
import dev.idm.vkp.api.model.VkApiPostSource;
import dev.idm.vkp.api.model.VkApiPrivacy;
import dev.idm.vkp.api.model.VkApiProfileInfoResponce;
import dev.idm.vkp.api.model.database.SchoolClazzDto;
import dev.idm.vkp.api.model.feedback.UserArray;
import dev.idm.vkp.api.model.feedback.VkApiBaseFeedback;
import dev.idm.vkp.api.model.local_json.ChatJsonResponse;
import dev.idm.vkp.api.model.longpoll.AbsLongpollEvent;
import dev.idm.vkp.api.model.response.ChatsInfoResponse;
import dev.idm.vkp.api.model.response.CustomCommentsResponse;
import dev.idm.vkp.api.model.response.LikesListResponse;
import dev.idm.vkp.api.model.response.NewsfeedCommentsResponse;
import dev.idm.vkp.model.AnswerVKOfficialList;
import dev.idm.vkp.settings.IProxySettings;
import dev.idm.vkp.settings.Settings;
import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.idm.vkp.util.Objects.isNull;
import static dev.idm.vkp.util.Objects.nonNull;

public class VkRetrofitProvider implements IVkRetrofitProvider {

    private static final Gson VKGSON = new GsonBuilder()
            .registerTypeAdapter(AnswerVKOfficialList.class, new AnswerVKOfficialDtoAdapter())
            .registerTypeAdapter(VkApiAttachments.Entry.class, new AttachmentsEntryDtoAdapter())
            .registerTypeAdapter(VkApiDoc.Entry.class, new DocsEntryDtoAdapter())
            .registerTypeAdapter(VKApiPhoto.class, new PhotoDtoAdapter())
            .registerTypeAdapter(boolean.class, new BooleanAdapter())
            .registerTypeAdapter(VkApiPrivacy.class, new PrivacyDtoAdapter())
            .registerTypeAdapter(VKApiPhotoAlbum.class, new PhotoAlbumDtoAdapter())
            .registerTypeAdapter(VKApiVideoAlbum.class, new VideoAlbumDtoAdapter())
            .registerTypeAdapter(VkApiAttachments.class, new AttachmentsDtoAdapter())
            .registerTypeAdapter(VKApiAudio.class, new AudioDtoAdapter())
            .registerTypeAdapter(VKApiPost.class, new PostDtoAdapter())
            .registerTypeAdapter(VkApiPostSource.class, new PostSourceDtoAdapter())
            .registerTypeAdapter(VKApiUser.class, new UserDtoAdapter())
            .registerTypeAdapter(VKApiCommunity.class, new CommunityDtoAdapter())
            .registerTypeAdapter(VkApiBaseFeedback.class, new FeedbackDtoAdapter())
            .registerTypeAdapter(VKApiComment.class, new CommentDtoAdapter())
            .registerTypeAdapter(VKApiVideo.class, new VideoDtoAdapter())
            .registerTypeAdapter(UserArray.class, new FeedbackUserArrayDtoAdapter())
            .registerTypeAdapter(VKApiMessage.class, new MessageDtoAdapter())
            .registerTypeAdapter(VKApiNews.class, new NewsAdapter())
            .registerTypeAdapter(AbsLongpollEvent.class, new LongpollUpdateAdapter())
            .registerTypeAdapter(ChatsInfoResponse.class, new ChatsInfoAdapter())
            .registerTypeAdapter(VKApiChat.class, new ChatDtoAdapter())
            .registerTypeAdapter(ChatUserDto.class, new ChatUserDtoAdapter())
            .registerTypeAdapter(SchoolClazzDto.class, new SchoolClazzDtoAdapter())
            .registerTypeAdapter(LikesListResponse.class, new LikesListAdapter())
            .registerTypeAdapter(NewsfeedCommentsResponse.Dto.class, new NewsfeedCommentDtoAdapter())
            .registerTypeAdapter(VKApiTopic.class, new TopicDtoAdapter())
            .registerTypeAdapter(GroupSettingsDto.class, new GroupSettingsAdapter())
            .registerTypeAdapter(CustomCommentsResponse.class, new CustomCommentsResponseAdapter())
            .registerTypeAdapter(VKApiAudioPlaylist.class, new AudioPlaylistDtoAdapter())
            .registerTypeAdapter(VKApiStory.class, new StoryDtoAdapter())
            .registerTypeAdapter(FaveLinkDto.class, new FaveLinkDtoAdapter())
            .registerTypeAdapter(VKApiArticle.class, new ArticleDtoAdapter())
            .registerTypeAdapter(VKApiCatalogLink.class, new VKApiCatalogLinkDtoAdapter())
            .registerTypeAdapter(ChatJsonResponse.class, new ChatJsonResponseDtoAdapter())
            .registerTypeAdapter(VkApiJsonString.class, new JsonStringDtoAdapter())
            .registerTypeAdapter(VkApiProfileInfoResponce.class, new ProfileInfoResponceDtoAdapter())
            .registerTypeAdapter(VkApiMarket.class, new MarketDtoAdapter())
            .create();

    private static final GsonConverterFactory GSON_CONVERTER_FACTORY = GsonConverterFactory.create(VKGSON);
    private static final RxJava3CallAdapterFactory RX_ADAPTER_FACTORY = RxJava3CallAdapterFactory.create();

    private final IProxySettings proxyManager;
    private final IVkMethodHttpClientFactory clientFactory;
    private final Object retrofitCacheLock = new Object();
    private final Object serviceRetrofitLock = new Object();
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, RetrofitWrapper> retrofitCache = Collections.synchronizedMap(new HashMap<>(1));
    private volatile RetrofitWrapper serviceRetrofit;

    public VkRetrofitProvider(IProxySettings proxySettings, IVkMethodHttpClientFactory clientFactory) {
        proxyManager = proxySettings;
        this.clientFactory = clientFactory;
        proxyManager.observeActive()
                .subscribe(optional -> onProxySettingsChanged());
    }

    public static Gson getVkgson() {
        return VKGSON;
    }

    private void onProxySettingsChanged() {
        synchronized (retrofitCacheLock) {
            for (Map.Entry<Integer, RetrofitWrapper> entry : retrofitCache.entrySet()) {
                entry.getValue().cleanup();
            }

            retrofitCache.clear();
        }
    }

    @Override
    public Single<RetrofitWrapper> provideNormalRetrofit(int accountId) {
        return Single.fromCallable(() -> {
            RetrofitWrapper retrofit;

            synchronized (retrofitCacheLock) {
                retrofit = retrofitCache.get(accountId);

                if (nonNull(retrofit)) {
                    return retrofit;
                }

                OkHttpClient client = clientFactory.createDefaultVkHttpClient(accountId, VKGSON, proxyManager.getActiveProxy());
                retrofit = createDefaultVkApiRetrofit(client);
                retrofitCache.put(accountId, retrofit);
            }

            return retrofit;
        });
    }

    @Override
    public Single<RetrofitWrapper> provideCustomRetrofit(int accountId, String token) {
        return Single.fromCallable(() -> {
            OkHttpClient client = clientFactory.createCustomVkHttpClient(accountId, token, VKGSON, proxyManager.getActiveProxy());
            return createDefaultVkApiRetrofit(client);
        });
    }

    @Override
    public Single<RetrofitWrapper> provideServiceRetrofit() {
        return Single.fromCallable(() -> {
            if (isNull(serviceRetrofit)) {
                synchronized (serviceRetrofitLock) {
                    if (isNull(serviceRetrofit)) {
                        OkHttpClient client = clientFactory.createServiceVkHttpClient(VKGSON, proxyManager.getActiveProxy());
                        serviceRetrofit = createDefaultVkApiRetrofit(client);
                    }
                }
            }

            return serviceRetrofit;
        });
    }

    @Override
    public Single<OkHttpClient> provideNormalHttpClient(int accountId) {
        return Single.fromCallable(() -> clientFactory.createDefaultVkHttpClient(accountId, VKGSON, proxyManager.getActiveProxy()));
    }

    private RetrofitWrapper createDefaultVkApiRetrofit(OkHttpClient okHttpClient) {
        return RetrofitWrapper.wrap(new Retrofit.Builder()
                .baseUrl("https://" + Settings.get().other().get_Api_Domain() + "/method/")
                .addConverterFactory(GSON_CONVERTER_FACTORY)
                .addCallAdapterFactory(RX_ADAPTER_FACTORY)
                .client(okHttpClient)
                .build());
    }
}
