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
import top.sacz.timtool.ui.view.CustomRecycleView;
import top.sacz.timtool.util.KvHelper;

/**
 * 表情面板View
 */
@SuppressLint("ViewConstructor")
public class StickerPanelView extends BottomPopupView {
    public final static KvHelper kvHelper = new KvHelper("表情面板");
    private final StickerPanelAdapter stickerPanelAdapter = new StickerPanelAdapter();
    private final StickerDirAdapter dirAdapter = new StickerDirAdapter();
    private final BottomStickerPanelDialog dialog;
    private int currentPosition = 0;

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
        String currentSelectionDir = kvHelper.getString("currentSelection", "");
        if (currentSelectionDir.isEmpty()) {
            return;
        }
        updateByDirName(currentSelectionDir);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onBindView(ViewGroup root) {
        //表情文件列表
        CustomRecycleView rvSticker = root.findViewById(R.id.rv_sticker_image);
        rvSticker.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
        rvSticker.setAdapter(stickerPanelAdapter);
        stickerPanelAdapter.setOnItemClickListener((adapter, view, position) -> {
            dialog.dismiss();
            //调用表情包点击事件
            StickerInfo stickerInfo = stickerPanelAdapter.getItem(position);

        });
        //表情目录列表
        CustomRecycleView rvStickerDir = root.findViewById(R.id.rv_sticker_dir);
        rvStickerDir.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //文件夹的adapter
        dirAdapter.submitList(StickerDataProvider.searchStickerDirectory());
        dirAdapter.setOnItemClickListener((adapter, view, position) -> {
            //调用更改表情包文件夹
            if (position == currentPosition) {
                return;
            }
            currentPosition = position;
            String dirName = dirAdapter.getItem(position);
            kvHelper.put("currentSelection", dirName);
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
