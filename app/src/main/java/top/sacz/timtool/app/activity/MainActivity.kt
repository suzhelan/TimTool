package top.sacz.timtool.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        requestTransparentStatusBar()
        initBuildInfo()

    }

    fun onGithubClick(view: View) {
        //跳转到浏览器
        val url = "https://github.com/suzhelan/TimTool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun initBuildInfo() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault())
        val buildTime = dateFormat.format(Date(BuildConfig.BUILD_TIMESTAMP))
        val buildInfo = getString(R.string.build_info).format(BuildConfig.VERSION_NAME, buildTime)
        bind.tvBuildInfo.text = buildInfo
    }

    private fun requestTransparentStatusBar() {
        //状态栏透明
        enableEdgeToEdge()
        //设置布局和状态栏的适配和间隔
        ViewCompat.setOnApplyWindowInsetsListener(bind.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}