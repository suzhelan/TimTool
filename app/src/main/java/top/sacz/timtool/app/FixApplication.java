package top.sacz.timtool.app;

import android.app.Application;

import com.kongzue.dialogx.DialogX;

public class FixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化dialogx
        DialogX.init(this);
    }
}
