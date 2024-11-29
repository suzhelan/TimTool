package top.sacz.timtool.hook.item.chat.stickerpanel.adapter;

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
import top.sacz.timtool.hook.item.chat.stickerpanel.StickerInfo;
import top.sacz.timtool.util.ScreenParamUtils;

public class StickerPanelAdapter extends BaseQuickAdapter<StickerInfo, QuickViewHolder> {

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int position) {
        return new QuickViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sticker_image, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int position, @Nullable StickerInfo stickerInfo) {
        ViewGroup parent = (ViewGroup) holder.itemView;

        ViewGroup.LayoutParams params = parent.getLayoutParams();
        params.height = ScreenParamUtils.getScreenWidth(getContext()) / 5 + 20;
        parent.requestLayout();

        ImageView stickerImage = holder.getView(R.id.iv_sticker_img);
        stickerImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(HookEnv.getHostAppContext())
                .load(new File(stickerInfo.getPath()))
                .fitCenter()
                .into(stickerImage);

    }

}
