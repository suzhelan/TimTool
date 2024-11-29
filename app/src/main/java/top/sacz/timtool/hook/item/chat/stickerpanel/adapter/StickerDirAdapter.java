package top.sacz.timtool.hook.item.chat.stickerpanel.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.item.chat.stickerpanel.StickerDataProvider;

public class StickerDirAdapter extends BaseQuickAdapter<String, QuickViewHolder> {

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.item_sticker_dir, viewGroup);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String dirName) {
        quickViewHolder.setText(R.id.tv_dir_name, dirName);

        String currentSelection = StickerDataProvider.getCurrentSelectionDir();
        if (currentSelection.equals(dirName)) {
            quickViewHolder.setBackgroundResource(R.id.tv_dir_name, R.drawable.bg_sticker_dir_select);
        } else {
            int color = getContext().getColor(R.color.transparent);
            quickViewHolder.setBackgroundColor(R.id.tv_dir_name, color);
        }
    }
}
