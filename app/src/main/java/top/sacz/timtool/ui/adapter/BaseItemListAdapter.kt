package top.sacz.timtool.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class BaseItemListAdapter<T> : BaseAdapter() {

    private val itemList = ArrayList<T>()

    override fun getCount(): Int {
        return itemList.size
    }

    fun submitList(list: List<T>?) {
        itemList.clear()
        itemList.addAll(list ?: emptyList())
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): T {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, getItem(position), parent)
    }

    abstract fun createView(position: Int, model: T, parent: ViewGroup): View
}