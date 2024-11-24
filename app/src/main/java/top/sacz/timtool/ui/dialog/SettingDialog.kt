package top.sacz.timtool.ui.dialog

import android.view.View
import android.widget.ListView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import top.sacz.timtool.R
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.ui.adapter.ItemListAdapter

class SettingDialog {

    fun show() {
        MessageDialog.build()
            .setTitleIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.app_name)
            .setMessage(R.string.setting_message)
            .setCustomView(object : OnBindView<MessageDialog>(R.layout.layout_setting) {
                override fun onBind(dialog: MessageDialog, v: View) {
                    onBindView(v)
                }
            }).show()
    }

    private fun onBindView(rootView: View) {
        val itemViewList = rootView.findViewById<ListView>(R.id.lv_item_list)
        val adapter = ItemListAdapter()
        val itemList = HookItemFactory.getAllSwitchFunctionItemList()
        adapter.submitList(itemList)
        itemViewList.adapter = adapter
    }
}