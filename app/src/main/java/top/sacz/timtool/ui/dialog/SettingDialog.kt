package top.sacz.timtool.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.util.FixContextUtil
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.ui.adapter.CategoryAdapter
import top.sacz.timtool.ui.adapter.SettingUIItemManger
import top.sacz.timtool.ui.bean.Category
import top.sacz.timtool.ui.bean.ParentCategory

class SettingDialog {
    private lateinit var dialog: MessageDialog

    private var isCategory = true

    @SuppressLint("InflateParams")
    fun show(activity: Context) {
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
                    onBindView(p1 as ViewGroup, p0)
                }
            })
            .setMessage(messageText)
            .show()

        dialog.dialogImpl.apply {
            txtDialogTitle.setOnClickListener {
                onGithubClick(it)
            }
        }
    }

    private fun onGithubClick(view: View) {
        val url = "https://github.com/suzhelan/TimTool"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        view.context.startActivity(intent)
    }

    @SuppressLint("InflateParams")
    private fun onBindView(rootView: View, dialog: MessageDialog) {
        val context = rootView.context
        
        val boxView = rootView.findViewById<FrameLayout>(R.id.box_list)
        val categoryRv = FixContextUtil.getFixInflater(context)
            .inflate(R.layout.layout_setting_category_list, null, false) as RecyclerView
        val rvAdapter = CategoryAdapter(getInitCategory())
        rvAdapter.setOnItemClickListener { adapter, view, position ->
            val category = adapter.getItem(position)
            if (category is Category) {
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