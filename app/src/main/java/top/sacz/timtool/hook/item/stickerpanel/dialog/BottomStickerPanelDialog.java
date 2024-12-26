package top.sacz.timtool.hook.item.stickerpanel.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnBindView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.item.stickerpanel.StickerDataProvider;
import top.sacz.timtool.hook.item.stickerpanel.StickerInfo;
import top.sacz.timtool.hook.item.stickerpanel.adapter.StickerDirAdapter;
import top.sacz.timtool.hook.item.stickerpanel.adapter.StickerPanelAdapter;
import top.sacz.timtool.hook.qqapi.ContactUtils;
import top.sacz.timtool.hook.qqapi.CreateElement;
import top.sacz.timtool.hook.qqapi.QQSendMsgTool;
import top.sacz.timtool.ui.view.CustomRecycleView;
import top.sacz.timtool.ui.view.FollowRecycleViewLinearLayout;
import top.sacz.timtool.util.ScreenParamUtils;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.util.ActivityTools;

public class BottomStickerPanelDialog {
    private final StickerPanelAdapter stickerPanelAdapter = new StickerPanelAdapter();
    private final StickerDirAdapter dirAdapter = new StickerDirAdapter();

    private boolean isShowing = false;
    public void show() {
        initData();
        Context context = ActivityTools.getTopActivity();
        int height = (int) (ScreenParamUtils.getScreenHeight(context) * 0.8);
        BottomDialog.build()
                .setDialogLifecycleCallback(new DialogLifecycleCallback<BottomDialog>() {
                    @Override
                    public void onShow(BottomDialog dialog) {
                        isShowing = true;
                    }

                    @Override
                    public void onDismiss(BottomDialog dialog) {
                        isShowing = false;
                    }
                })
                .setMaxHeight(height)
                .setMinHeight(height)
                .setScrollableWhenContentLargeThanVisibleRange(false)
                .setCustomView(new OnBindView<>(R.layout.layout_sticker_panel_dialog) {
                    @Override
                    public void onBind(BottomDialog dialog, View v) {
                        onBindView(dialog, (FollowRecycleViewLinearLayout) v);
                    }
                }).show();

    }

    private void loadFirstStickerDir() {
        List<String> stickerDirectory = StickerDataProvider.searchStickerDirectory();
        if (stickerDirectory.isEmpty()) {
            return;
        }
        String firstDirName = stickerDirectory.get(0);
        StickerDataProvider.setCurrentSelectionDir(firstDirName);
        updateByDirName(firstDirName);
    }

    private void initData() {
        //如果记录为空 那么拿第一个来展示
        String currentSelectionDir = StickerDataProvider.getCurrentSelectionDir();
        if (currentSelectionDir.isEmpty()) {
            loadFirstStickerDir();
            return;
        }
        File file = new File(StickerDataProvider.getStickerStorageDirectory(), currentSelectionDir);
        if (!file.exists()) {
            loadFirstStickerDir();
            return;
        }
        updateByDirName(currentSelectionDir);
    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void onBindView(BottomDialog dialog, FollowRecycleViewLinearLayout root) {
        //表情文件列表
        CustomRecycleView rvSticker = root.findViewById(R.id.rv_sticker_image);
        root.setFollowRecycleView(rvSticker);
        rvSticker.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
        rvSticker.setAdapter(stickerPanelAdapter);
        stickerPanelAdapter.setOnItemClickListener((adapter, view, position) -> {
            dialog.dismiss();
            //调用表情包点击事件
            StickerInfo stickerInfo = stickerPanelAdapter.getItem(position);
            onClickSticker(stickerInfo);
        });
        stickerPanelAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            StickerInfo stickerInfo = stickerPanelAdapter.getItem(position);

            new DeleteStickerDialog().show(stickerInfo, () -> {
                adapter.removeAt(position);
            });
            return true;
        });
        //表情目录列表
        CustomRecycleView rvStickerDir = root.findViewById(R.id.rv_sticker_dir);
        rvStickerDir.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //文件夹的adapter
        dirAdapter.submitList(StickerDataProvider.searchStickerDirectory());
        dirAdapter.setOnItemLongClickListener((adapter, dirView, position) -> {
            String dirName = adapter.getItem(position);
            new ChangeStickerDirDialog().show(dirName, () -> {
                adapter.submitList(StickerDataProvider.searchStickerDirectory());
                initData();
            });
            return true;
        });
        dirAdapter.setOnItemClickListener((adapter, dirView, position) -> {
            //调用更改表情包文件夹
            String dirName = adapter.getItem(position);
            String currentSelection = StickerDataProvider.getCurrentSelectionDir();
            if (currentSelection.equals(dirName)) {
                return;
            }
            //进行一些数据更新操作
            StickerDataProvider.setCurrentSelectionDir(dirName);
            adapter.notifyDataSetChanged();
            updateByDirName(dirName);
        });
        rvStickerDir.setAdapter(dirAdapter);
    }

    private void updateByDirName(String dir) {
        List<StickerInfo> stickerInfoList = StickerDataProvider.searchStickerFile(dir);
        stickerPanelAdapter.submitList(stickerInfoList);
    }

    private void onClickSticker(StickerInfo stickerInfo) {
        Object msgElement = CreateElement.createStickerElement(stickerInfo.getPath());
        Object picElement = FieldUtils.getField(msgElement, "picElement", ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.PicElement"));
        FieldUtils.setField(picElement, "summary", "[动画表情]");
        //下面这两行设不设置都行
        FieldUtils.setField(picElement, "storeID", 1);
        FieldUtils.setField(picElement, "emojiFrom", 0);
        ArrayList<Object> msgElementList = new ArrayList<>();
        msgElementList.add(msgElement);
        QQSendMsgTool.sendMsg(ContactUtils.getCurrentContact(), msgElementList);
    }
}
