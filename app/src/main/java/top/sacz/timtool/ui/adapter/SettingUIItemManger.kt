package top.sacz.timtool.ui.adapter

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.ui.bean.Category
import top.sacz.timtool.ui.bean.ItemUI
import top.sacz.timtool.ui.bean.ParentCategory

class SettingUIItemManger {
    private val result = mutableListOf<ParentCategory>()

    fun parseHookItemAsUI(): List<ParentCategory> {
        result.clear()
        val target = HookItemFactory.getAllSwitchFunctionItemList()
        target.forEach { item ->
            //解析路径和内容
            val path = item.path
            val names = path.split("/")
            val parentTitle = names[0]
            val categoryTitle = names[1]
            val name = names[2]

            val parentCategory = findOrCreateParentCategory(parentTitle)
            val category = findOrCreateCategory(parentCategory, categoryTitle)
            val itemUI = findOrCreateItemUI(category, item)
        }
        return result
    }

    /**
     * 查找父分类
     */
    private fun findOrCreateParentCategory(title: String): ParentCategory {
        val titleUI = result.filter {
            it.parentTitle == title
        }
        if (titleUI.isEmpty()) {
            val newParentCategory = ParentCategory(title)
            result.add(newParentCategory)
        }
        return result.first { it.parentTitle == title }
    }

    /**
     * 查找功能所属的具体分类
     */
    private fun findOrCreateCategory(parentCategory: ParentCategory, title: String): Category {
        val category = parentCategory.categoryList.filter { it.title == title }
        if (category.isEmpty()) {
            val newCategory = Category(title)
            parentCategory.categoryList.add(newCategory)
        }
        return parentCategory.categoryList.first { it.title == title }
    }

    /**
     * 查找具体的功能ui (其实没必要查找了 因为到这已经具有了完全不重复性
     */
    private fun findOrCreateItemUI(
        category: Category,
        functionHookItem: BaseSwitchFunctionHookItem
    ): ItemUI {
        val itemUI = category.items.filter { it.title == functionHookItem.itemName }
        if (itemUI.isEmpty()) {
            val newItemUI =
                ItemUI(functionHookItem.itemName, functionHookItem.tip, functionHookItem)
            category.items.add(newItemUI)
        }
        return category.items.first { it.title == functionHookItem.itemName }
    }
}