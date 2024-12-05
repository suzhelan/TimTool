package top.sacz.timtool.app;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;

import top.sacz.timtool.util.KvHelper;

public class FixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化dialogx
        KvHelper.initialize(this);
        DialogX.init(this);
        DialogX.globalStyle = new MaterialYouStyle();
    }
}
