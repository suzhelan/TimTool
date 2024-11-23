package top.sacz.timtool.app.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import top.sacz.timtool.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MessageDialog.show(
            "这里是标题",
            "此对话框演示的是自定义对话框内部布局的效果",
            "确定",
            "取消"
        )
            .setCustomView(object : OnBindView<MessageDialog?>(R.layout.layout_setting) {
                override fun onBind(dialog: MessageDialog?, v: View) {
                    //View childView = v.findViewById(resId)...
                }
            })
    }
}