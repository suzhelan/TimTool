package top.sacz.timtool.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import top.sacz.timtool.R
import top.sacz.timtool.hook.core.HookItemLoader
import top.sacz.timtool.ui.bean.Category
import top.sacz.timtool.ui.bean.ItemUI
import top.sacz.timtool.ui.bean.ParentCategory

class CategoryAdapter(data: List<Any>) : BaseMultiItemAdapter<Any>(data) {
    private val TYPE_PARENT = 3
    private val TYPE_CATEGORY = 2
    private val TYPE_ITEM = 1

    init {
        //规范中可以使用自定义ViewHolder 但是我懒 用BaseRecyclerViewAdapterHelper自带的QuickViewHolder了
        addItemType(TYPE_ITEM, object : OnMultiItemAdapterListener<Any, QuickViewHolder> {
            override fun onBind(holder: QuickViewHolder, position: Int, item: Any?) {
                item as ItemUI
                val itemView = holder.itemView
                val tvName = holder.getView<TextView>(R.id.tv_item_name)
                val tvTip = holder.getView<TextView>(R.id.tv_item_tip)
                val switchItem = holder.getView<Switch>(R.id.switch_item)
                tvName.text = item.title
                tvTip.text = item.desc
                tvTip.visibility = if (item.desc == null) View.GONE else View.VISIBLE
                switchItem.isChecked = item.switchFunctionHookItem.isEnabled
                switchItem.setOnCheckedChangeListener { _, isChecked ->
                    item.switchFunctionHookItem.isEnabled = isChecked
                    item.switchFunctionHookItem.startLoad()
                    HookItemLoader().saveConfig()
                }
                item.switchFunctionHookItem.onClickListener?.let {
                    itemView.setOnClickListener(it)
                }
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): QuickViewHolder {
                return QuickViewHolder(R.layout.item_hook_function, parent)
            }
        })
        addItemType(TYPE_PARENT, object : OnMultiItemAdapterListener<Any, QuickViewHolder> {
            override fun onBind(holder: QuickViewHolder, position: Int, item: Any?) {
                item as ParentCategory
                holder.setText(R.id.tv_parent_category, item.parentTitle)
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): QuickViewHolder {
                return QuickViewHolder(R.layout.item_parent_category, parent)
            }
        })

        addItemType(TYPE_CATEGORY, object : OnMultiItemAdapterListener<Any, QuickViewHolder> {
            override fun onBind(holder: QuickViewHolder, position: Int, item: Any?) {
                item as Category
                holder.setText(R.id.tv_parent_category, item.title)
            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): QuickViewHolder {
                return QuickViewHolder(R.layout.item_item_category, parent)
            }
        })

        onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
            val item = list[position]
            when (item) {
                is Category -> TYPE_CATEGORY
                is ItemUI -> TYPE_ITEM
                is ParentCategory -> TYPE_PARENT
                else -> 0
            }
        }
    }

}