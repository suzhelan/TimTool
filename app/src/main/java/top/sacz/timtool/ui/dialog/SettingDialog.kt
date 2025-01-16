package top.sacz.timtool.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.util.FixContextUtil
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.net.UpdateService
import top.sacz.timtool.net.UserCenter
import top.sacz.timtool.ui.adapter.CategoryAdapter
import top.sacz.timtool.ui.adapter.SettingUIItemManger
import top.sacz.timtool.ui.bean.Category
import top.sacz.timtool.ui.bean.ParentCategory

class SettingDialog {
    private lateinit var dialog: MessageDialog

    private lateinit var rootView: ViewGroup

    private var isCategory = true


    @SuppressLint("InflateParams")
    fun show(activity: Context) {
        val updateService = UpdateService()
        //尝试直接获取更新
        if (updateService.hasUpdate()) {
            updateService.showUpdateDialog()

            if (updateService.isForceUpdate()) {
                return
            }
        }
        val messageText = activity.getString(R.string.setting_message)
            .format(BuildConfig.VERSION_NAME, HookEnv.getAppName(), HookEnv.getVersionName())
        dialog = MessageDialog.build()
            .setTitleIcon(R.drawable.ic_github)
            .setTitle(R.string.app_name)
            .setCustomView(object : OnBindView<MessageDialog>(R.layout.layout_setting) {
                override fun onBind(
                    p0: MessageDialog,
                    p1: View
                ) {
                    rootView = p1 as ViewGroup
                    dialog = p0
                    onBindView()
                }
            })
            .setMessage(messageText)
            .show()
        dialog.dialogImpl.apply {
            txtDialogTitle.setOnClickListener {
                onGithubClick(it)
            }
            txtDialogTip.setOnClickListener {
                onTelegramClick(it)
            }
        }
        PayDialog(this).showFirstDialog()
    }

    private fun onTelegramClick(view: View) {
        //跳转到浏览器
        val url = "https://t.me/timtool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        view.context.startActivity(intent)
    }

    private fun onGithubClick(view: View) {
        //跳转到浏览器
        val url = "https://github.com/suzhelan/TimTool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        view.context.startActivity(intent)
    }

    fun refresh() {
        dialog.refreshUI()
    }

    @SuppressLint("InflateParams")
    private fun onBindView() {
        val userInfo = UserCenter.getUserInfo()

        val context = rootView.context
        val ibViewAllUpdateLog = rootView.findViewById<View>(R.id.ib_update_log)
        ibViewAllUpdateLog.setOnClickListener {
            UpdateLogDialog().show()
        }
        val ibTelegram = rootView.findViewById<View>(R.id.ib_telegram)
        ibTelegram.setOnClickListener {
            onTelegramClick(it)
        }
        val tvIdentityName = rootView.findViewById<TextView>(R.id.tv_user_identity_name)
        val tvLabel = rootView.findViewById<TextView>(R.id.tv_user_label)
        tvIdentityName.text = userInfo.identityName
        tvLabel.text = "标签: ${userInfo.label ?: "无"}"
        //提交初始的分类
        val boxView = rootView.findViewById<FrameLayout>(R.id.box_list)
        val categoryRv = FixContextUtil.getFixInflater(context)
            .inflate(R.layout.layout_setting_category_list, null, false) as RecyclerView
        val rvAdapter = CategoryAdapter(getInitCategory())
        rvAdapter.setOnItemClickListener { adapter, view, position ->
            val category = adapter.getItem(position)
            if (category is Category) {
                //隐藏分类view
                isCategory = false
                boxView.getChildAt(0).visibility = View.GONE
                val newItemViewRv = FixContextUtil.getFixInflater(context)
                    .inflate(R.layout.layout_setting_category_list, null, false) as RecyclerView
                val newItemAdapter = CategoryAdapter(category.items)
                if (newItemAdapter.getItem(0) !is ParentCategory) {
                    newItemAdapter.add(0, ParentCategory(category.title))
                }
                newItemViewRv.layoutManager = LinearLayoutManager(context)
                newItemViewRv.adapter = newItemAdapter
                boxView.addView(newItemViewRv)
            }
        }
        categoryRv.layoutManager = LinearLayoutManager(context)
        categoryRv.adapter = rvAdapter
        boxView.addView(categoryRv)
        //设置返回处理
        dialog.setOnBackPressedListener {
            onBack(boxView)
        }
        dialog.setOnBackgroundMaskClickListener { dialog, v ->
            !onBack(boxView)
        }
    }

    private fun onBack(boxView: ViewGroup): Boolean {
        if (isCategory) {
            return true
        } else {
            isCategory = true
            boxView.removeViewAt(boxView.childCount - 1)
            boxView.getChildAt(0).visibility = View.VISIBLE
            return false
        }
    }

    private fun getInitCategory(): List<Any> {
        val result = mutableListOf<Any>()
        val uiItemManger = SettingUIItemManger()
        for (parentCategory in uiItemManger.parseHookItemAsUI()) {
            result.add(parentCategory)
            result.addAll(parentCategory.categoryList)
        }
        return result
    }

}
