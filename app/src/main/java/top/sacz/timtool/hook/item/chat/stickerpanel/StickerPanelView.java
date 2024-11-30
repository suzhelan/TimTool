package top.sacz.timtool.hook.item.chat.stickerpanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.item.chat.stickerpanel.adapter.StickerDirAdapter;
import top.sacz.timtool.hook.item.chat.stickerpanel.adapter.StickerPanelAdapter;
import top.sacz.timtool.hook.item.chat.stickerpanel.dialog.BottomStickerPanelDialog;
import top.sacz.timtool.ui.view.CustomRecycleView;

/**
 * 表情面板View
 */
@SuppressLint("ViewConstructor")
public class StickerPanelView extends BottomPopupView {
    private final StickerPanelAdapter stickerPanelAdapter = new StickerPanelAdapter();
    private final StickerDirAdapter dirAdapter = new StickerDirAdapter();
    private final BottomStickerPanelDialog dialog;

    public StickerPanelView(@NonNull Context context, BottomStickerPanelDialog dialog) {
        super(context);
        this.dialog = dialog;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_sticker_panel_dialog;
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * 0.7);
    }

    @Override
    protected int getPopupHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * 0.7f);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initData();
        onBindView(this);
    }

    private void initData() {
        String currentSelectionDir = StickerDataProvider.getCurrentSelectionDir();
        if (currentSelectionDir.isEmpty()) {
            return;
        }
        updateByDirName(currentSelectionDir);
    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void onBindView(ViewGroup root) {
        //表情文件列表
        CustomRecycleView rvSticker = root.findViewById(R.id.rv_sticker_image);
        rvSticker.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
        rvSticker.setAdapter(stickerPanelAdapter);
        stickerPanelAdapter.setOnItemClickListener((adapter, view, position) -> {
            dialog.dismiss();
            //调用表情包点击事件
            StickerInfo stickerInfo = stickerPanelAdapter.getItem(position);
            onClickSticker(stickerInfo);
        });
        //表情目录列表
        CustomRecycleView rvStickerDir = root.findViewById(R.id.rv_sticker_dir);
        rvStickerDir.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //文件夹的adapter
        dirAdapter.submitList(StickerDataProvider.searchStickerDirectory());
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

    }
}
