package top.sacz.timtool.hook.item.chat.emojipanel.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;

import top.sacz.timtool.R;

public class EmojiDirAdapter extends BaseQuickAdapter<String, QuickViewHolder> {

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.item_emoji_dir, viewGroup);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder quickViewHolder, int i, @Nullable String s) {
        quickViewHolder.setText(R.id.tv_dir_name, s);
    }
}
