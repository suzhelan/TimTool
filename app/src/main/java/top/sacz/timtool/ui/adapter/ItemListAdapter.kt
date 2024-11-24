package top.sacz.timtool.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.HookItemLoader

class ItemListAdapter : BaseItemListAdapter<BaseSwitchFunctionHookItem>() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun createView(
        position: Int,
        model: BaseSwitchFunctionHookItem,
        parent: ViewGroup
    ): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_hook_function, null)
        val tvName = itemView.findViewById<TextView>(R.id.tv_item_name)
        val tvTip = itemView.findViewById<TextView>(R.id.tv_item_tip)
        val switchItem = itemView.findViewById<Switch>(R.id.switch_item)
        tvName.text = model.itemName.substring(model.itemName.lastIndexOf("/") + 1)
        tvTip.text = model.tip
        tvTip.visibility = if (model.tip == null) View.GONE else View.VISIBLE
        switchItem.isChecked = model.isEnabled
        switchItem.setOnCheckedChangeListener { _, isChecked ->
            model.isEnabled = isChecked
            model.startLoad()
            HookItemLoader().saveConfig()
        }
        return itemView
    }

}