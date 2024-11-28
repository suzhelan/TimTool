package top.sacz.timtool.hook.item.chat.emojipanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiDirAdapter;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiPanelAdapter;
import top.sacz.timtool.ui.view.CustomRecycleView;
import top.sacz.timtool.util.ScreenParamUtils;

public class BottomEmojiPanelDialog {

    private static int currentPosition = 0;

    public static void show() {
        Context context = HookEnv.getHostAppContext();
        int height = (int) (ScreenParamUtils.getScreenHeight(context) * 0.8);
        BottomDialog.build()
                .setMaxHeight(height)
                .setMinHeight(height)
                .setScrollableWhenContentLargeThanVisibleRange(false)//布局整体不触发ScrollView
                .setCustomView(new OnBindView<>(R.layout.layout_emoji_panel_dialog) {
                    @Override
                    public void onBind(BottomDialog dialog, View v) {
                        onBindView(dialog, (ViewGroup) v);
                    }
                }).show();

    }

    @SuppressLint("ClickableViewAccessibility")
    private static void onBindView(BottomDialog dialog, ViewGroup root) {
        //表情文件列表
        CustomRecycleView rvEmoji = root.findViewById(R.id.rv_emoji_image);
        rvEmoji.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
        EmojiPanelAdapter emojiPanelAdapter = new EmojiPanelAdapter();
        rvEmoji.setAdapter(emojiPanelAdapter);
        rvEmoji.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (rvEmoji.getScrollDistance() == 0) {
                    dialog.setAllowInterceptTouch(true);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (rvEmoji.getScrollDistance() != 0) {
                    dialog.setAllowInterceptTouch(false);
                }
            }
            return false;
        });

        //表情目录列表
        CustomRecycleView rvEmojiDir = root.findViewById(R.id.rv_emoji_dir);
        rvEmojiDir.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //文件夹的adapter
        EmojiDirAdapter emojiDirAdapter = new EmojiDirAdapter();
        emojiDirAdapter.submitList(EmojiPanelDataProvider.searchEmojiDirectory());


        emojiDirAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == currentPosition) {
                return;
            }

            currentPosition = position;
            String dirName = emojiDirAdapter.getItem(position);

            emojiPanelAdapter.submitList(new ArrayList<>());
            List<EmojiInfo> emojiInfoList = EmojiPanelDataProvider.searchEmojiFile(dirName);
            for (EmojiInfo emojiInfo : emojiInfoList) {
                emojiPanelAdapter.add(emojiInfo);
            }
        });
        rvEmojiDir.setAdapter(emojiDirAdapter);
    }


}
