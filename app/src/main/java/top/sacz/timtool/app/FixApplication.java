package top.sacz.timtool.app;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;

public class FixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化dialogx
        DialogX.init(this);
        DialogX.globalStyle = new MaterialYouStyle();
    }
}
