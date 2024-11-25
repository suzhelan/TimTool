package top.sacz.timtool.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.util.FixContextUtil
import top.sacz.timtool.R
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.ui.adapter.ItemListAdapter

class SettingDialog {


    @SuppressLint("InflateParams")
    fun show(activity: Context) {
        val rootView =
            FixContextUtil.getFixLayoutInflater(activity).inflate(R.layout.layout_setting, null)
        onBindView(rootView)
        MessageDialog.build()
            .setTitleIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.app_name)
            .setMessage(R.string.setting_message)
            .show()
            .dialogImpl.apply {
                boxList.addView(
                    rootView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
    }

    private fun onBindView(rootView: View) {
        val itemViewList = rootView.findViewById<ListView>(R.id.lv_item_list)
        val itemList = HookItemFactory.getAllSwitchFunctionItemList()
        val adapter = ItemListAdapter()
        adapter.submitList(itemList)
        itemViewList.adapter = adapter
    }
}