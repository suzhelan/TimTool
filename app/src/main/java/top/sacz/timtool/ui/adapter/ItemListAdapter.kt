package top.sacz.timtool.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.HookItemLoader

class ItemListAdapter : BaseQuickAdapter<BaseSwitchFunctionHookItem, QuickViewHolder>() {


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_hook_function, parent)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: BaseSwitchFunctionHookItem?
    ) {
        item!!
        val itemView = holder.itemView
        val tvName = holder.getView<TextView>(R.id.tv_item_name)
        val tvTip = holder.getView<TextView>(R.id.tv_item_tip)
        val switchItem = holder.getView<Switch>(R.id.switch_item)
        tvName.text = item.itemName
        tvTip.text = item.tip
        tvTip.visibility = if (item.tip == null) View.GONE else View.VISIBLE
        switchItem.isChecked = item.isEnabled
        switchItem.setOnCheckedChangeListener { _, isChecked ->
            item.isEnabled = isChecked
            item.startLoad()
            HookItemLoader().saveConfig()
        }
        item.onClickListener?.let {
            itemView.setOnClickListener(it)
        }
    }

}