package dev.idm.vkp.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.EventListener;
import java.util.List;

import dev.idm.vkp.Constants;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.base.RecyclerBindableAdapter;
import dev.idm.vkp.model.Link;
import dev.idm.vkp.model.PhotoSizes;
import dev.idm.vkp.picasso.PicassoInstance;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class LinksAdapter extends RecyclerBindableAdapter<Link, LinksAdapter.LinkViewHolder> {

    private ActionListener mActionListener;
    private LinkConversationListener linkConversationListener;

    public LinksAdapter(List<Link> data) {
        super(data);
    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }

    public void setLinkConversationListener(LinkConversationListener linkConversationListener) {
        this.linkConversationListener = linkConversationListener;
    }

    public String getImageUrl(Link link) {

        if (link.getPhoto() == null && link.getPreviewPhoto() != null)
            return link.getPreviewPhoto();

        if (nonNull(link.getPhoto()) && nonNull(link.getPhoto().getSizes())) {
            PhotoSizes sizes = link.getPhoto().getSizes();
            return sizes.getUrlForSize(Settings.get().main().getPrefPreviewImageSize(), true);
        }

        return null;
    }

    @Override
    protected void onBindItemViewHolder(LinkViewHolder holder, int position, int type) {
        Link item = getItem(position);

        if (Utils.isEmpty(item.getTitle()))
            holder.tvTitle.setVisibility(View.GONE);
        else {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(item.getTitle());
        }
        if (Utils.isEmpty(item.getDescription()))
            holder.tvDescription.setVisibility(View.GONE);
        else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(item.getDescription());
        }
        if (Utils.isEmpty(item.getUrl()))
            holder.tvURL.setVisibility(View.GONE);
        else {
            holder.tvURL.setVisibility(View.VISIBLE);
            holder.tvURL.setText(item.getUrl());
        }

        String imageUrl = getImageUrl(item);
        if (imageUrl != null) {
            holder.ivEmpty.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.VISIBLE);
            ViewUtils.displayAvatar(holder.ivImage, null, imageUrl, Constants.PICASSO_TAG);
        } else {
            PicassoInstance.with().cancelRequest(holder.ivImage);
            holder.ivImage.setVisibility(View.GONE);
            holder.ivEmpty.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (nonNull(mActionListener)) {
                mActionListener.onLinkClick(holder.getAdapterPosition(), item);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (linkConversationListener != null) {
                linkConversationListener.onGoLinkConversation(item);
                return true;
            }
            return false;
        });
    }

    @Override
    protected LinkViewHolder viewHolder(View view, int type) {
        return new LinkViewHolder(view);
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.item_document_list;
    }

    public interface ActionListener extends EventListener {
        void onLinkClick(int index, @NonNull Link doc);
    }

    public interface LinkConversationListener {
        void onGoLinkConversation(@NonNull Link doc);
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final ImageView ivEmpty;
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvURL;

        private LinkViewHolder(View root) {
            super(root);
            ivImage = root.findViewById(R.id.item_document_image);
            ivEmpty = root.findViewById(R.id.item_document_empty);
            tvTitle = root.findViewById(R.id.item_document_title);
            tvDescription = root.findViewById(R.id.item_document_description);
            tvURL = root.findViewById(R.id.item_document_url);
        }
    }
}
