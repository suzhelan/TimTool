package top.sacz.timtool.ui.bean

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem

// 辅助功能/聊天/复读

//辅助功能
data class CategoryTitleUI(var title: String, var dir: List<Category>)

//聊天
data class Category(var title: String, var items: List<ItemUI>)

//具体的功能名称
data class ItemUI(
    var title: String,
    var desc: String,
    var switchFunctionHookItem: BaseSwitchFunctionHookItem
)