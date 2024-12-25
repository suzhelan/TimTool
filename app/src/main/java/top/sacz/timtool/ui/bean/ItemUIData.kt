package top.sacz.timtool.ui.bean

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem

// 辅助功能/聊天/复读

//辅助功能
data class ParentCategory(
    val parentTitle: String,
    var categoryList: MutableList<Category> = mutableListOf()
)

//类别
data class Category(val title: String, var items: MutableList<ItemUI> = mutableListOf())

//具体的功能名称
data class ItemUI(
    var title: String,
    var desc: String,
    var switchFunctionHookItem: BaseSwitchFunctionHookItem
)