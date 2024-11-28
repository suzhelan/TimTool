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

import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiDirAdapter;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiPanelAdapter;
import top.sacz.timtool.ui.view.CustomRecycleView;
import top.sacz.timtool.util.KvHelper;
import top.sacz.timtool.util.ScreenParamUtils;

public class BottomEmojiPanelDialog {
    public final static KvHelper kvHelper = new KvHelper("表情面板");
    private final EmojiPanelAdapter emojiPanelAdapter = new EmojiPanelAdapter();
    private final EmojiDirAdapter dirAdapter = new EmojiDirAdapter();
    private int currentPosition = 0;

    public void show() {
        Context context = HookEnv.getHostAppContext();
        int height = (int) (ScreenParamUtils.getScreenHeight(context) * 0.7);
        BottomDialog.build()
                .setMaxHeight(height)
                .setMinHeight(height)
                .setScrollableWhenContentLargeThanVisibleRange(false)//布局整体不触发ScrollView
                .setCustomView(new OnBindView<>(R.layout.layout_emoji_panel_dialog) {
                    @Override
                    public void onBind(BottomDialog dialog, View v) {
                        if (dialog.getDialogImpl().imgTab != null) {
                            ((ViewGroup) dialog.getDialogImpl().imgTab.getParent()).removeView(dialog.getDialogImpl().imgTab);
                        }
                        onBindView(dialog, (ViewGroup) v);
                    }
                }).show();

    }

    private void initData() {
        String currentSelectionDir = kvHelper.getString("currentSelection", "");
        if (currentSelectionDir.isEmpty()) {
            return;
        }
        updateByDirName(currentSelectionDir);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onBindView(BottomDialog dialog, ViewGroup root) {
        //表情文件列表
        CustomRecycleView rvEmoji = root.findViewById(R.id.rv_emoji_image);
        rvEmoji.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
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
        dirAdapter.submitList(EmojiPanelDataProvider.searchEmojiDirectory());
        dirAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == currentPosition) {
                return;
            }
            currentPosition = position;
            String dirName = dirAdapter.getItem(position);
            kvHelper.put("currentSelection", dirName);
            updateByDirName(dirName);
        });
        rvEmojiDir.setAdapter(dirAdapter);
    }

    private void updateByDirName(String dir) {
        List<EmojiInfo> emojiInfoList = EmojiPanelDataProvider.searchEmojiFile(dir);
        emojiPanelAdapter.submitList(emojiInfoList);
    }
}
