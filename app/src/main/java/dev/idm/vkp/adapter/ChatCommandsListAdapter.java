package dev.idm.vkp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.fragment.Command;
import dev.idm.vkp.settings.CurrentTheme;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.view.WeakViewAnimatorAdapter;

public class ChatCommandsListAdapter extends RecyclerView.Adapter<ChatCommandsListAdapter.ViewHolder> {

    private final int paddingForFirstLast;
    private List<Command> data;
    private ActionListener actionListener;

    public ChatCommandsListAdapter(Context context, List<Command> commands) {
        data = commands;
        paddingForFirstLast = Utils.is600dp(context) ? (int) Utils.dpToPx(16, context) : 0;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.item_chat_command_ist_second, viewGroup, false
                )
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Command item = data.get(position);
        holder.tvName.setText(item.name);
        holder.tvDescriptiom.setText(item.description);
        holder.itemView.setOnClickListener(view -> {
            if (Objects.nonNull(actionListener)) {
                actionListener.onCommandClick(item);
                holder.startSomeAnimation();
            }
        });

        View view = holder.itemView;

        view.setPadding(view.getPaddingLeft(),
                position == 0 ? paddingForFirstLast : 0,
                view.getPaddingRight(),
                position == getItemCount() - 1 ? paddingForFirstLast : 0);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Command> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        void onCommandClick(Command command);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName;
        final TextView tvDescriptiom;
        final MaterialCardView selectionView;
        final Animator.AnimatorListener animationAdapter;
        ObjectAnimator animator;

        ViewHolder(View root) {
            super(root);
            tvName = root.findViewById(R.id.item_command_name);
            tvDescriptiom = root.findViewById(R.id.item_command_description);
            selectionView = root.findViewById(R.id.item_command_selection);

            animationAdapter = new WeakViewAnimatorAdapter<View>(selectionView) {
                @Override
                public void onAnimationEnd(View view) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationStart(View view) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onAnimationCancel(View view) {
                    view.setVisibility(View.GONE);
                }
            };
        }

        void startSomeAnimation() {
            selectionView.setCardBackgroundColor(CurrentTheme.getColorSecondary(selectionView.getContext()));
            selectionView.setAlpha(0.5f);

            animator = ObjectAnimator.ofFloat(selectionView, View.ALPHA, 0.0f);
            animator.setDuration(500);
            animator.addListener(animationAdapter);
            animator.start();
        }
    }
}
