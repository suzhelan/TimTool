package top.sacz.timtool.hook.item.chat.emojipanel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;

import java.io.File;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.emojipanel.EmojiInfo;
import top.sacz.timtool.util.ScreenParamUtils;

public class EmojiPanelAdapter extends BaseQuickAdapter<EmojiInfo, QuickViewHolder> {

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int position) {
        return new QuickViewHolder(LayoutInflater.from(context).inflate(R.layout.item_emoji_image, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int position, @Nullable EmojiInfo emojiInfo) {
        ViewGroup parent = (ViewGroup) holder.itemView;

        ViewGroup.LayoutParams params = parent.getLayoutParams();
        params.height = ScreenParamUtils.getScreenWidth(getContext()) / 5 + 20;
        parent.requestLayout();

        ImageView emojiImage = holder.getView(R.id.iv_emoji_img);
        emojiImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(HookEnv.getHostAppContext())
                .load(new File(emojiInfo.getPath()))
                .fitCenter()
                .into(emojiImage);

    }

}
