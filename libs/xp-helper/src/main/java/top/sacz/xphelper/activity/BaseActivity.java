package top.sacz.xphelper.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.reflect.ClassUtils;


/**
 * 使用前参阅 {@link ActivityProxyManager#initActivityProxyManager(Context)}
 */
public class BaseActivity extends FragmentActivity {
    private final BaseActivityClassLoader mLoader = new BaseActivityClassLoader(BaseActivity.class.getClassLoader());

    @Override
    public ClassLoader getClassLoader() {
        return mLoader;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
        if (windowState != null) {
            windowState.setClassLoader(mLoader);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XpHelper.injectResourcesToContext(this);
    }


    /**
     * 将本 Activity 的状态栏设置为透明状态
     */
    protected void requestTranslucentStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    private static class BaseActivityClassLoader extends ClassLoader {
        private final ClassLoader mBaseReferencer;
        private final ClassLoader mHostReferencer;

        public BaseActivityClassLoader(ClassLoader referencer) {
            mBaseReferencer = referencer;
            mHostReferencer = ClassUtils.getClassLoader();
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                if (name.startsWith("androidx.compose") || name.startsWith("androidx.navigation") || name.startsWith("androidx.activity")) {
                    return mBaseReferencer.loadClass(name);
                }
                return Context.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
            try {
                //start: overloaded
                if (name.equals("androidx.lifecycle.LifecycleOwner") || name.equals("androidx.lifecycle.ViewModelStoreOwner") || name.equals("androidx.savedstate.SavedStateRegistryOwner")) {
                    return mHostReferencer.loadClass(name);
                }
            } catch (ClassNotFoundException ignored) {
            }
            //with ClassNotFoundException
            return mBaseReferencer.loadClass(name);
        }
    }
}

