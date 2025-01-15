package top.sacz.timtool.net

import top.sacz.timtool.net.entity.User
import top.sacz.timtool.net.httpconfig.TokenInfo
import top.sacz.xphelper.util.KvHelper


object UserCenter {

    fun setUserInfo(user: User) {
        getConfig().put("user_info", user)
    }

    fun getUserInfo(): User {
        val user = User()
        user.uin = "0"
        user.nickname = "未同步"
        user.identity = 0
        user.identityName = "未同步"
        return getConfig().getObject("user_info", User::class.java) ?: user
    }

    fun setUpdateToken(token: TokenInfo) {
        getConfig().put("token", token)
    }

    fun removeAll() {
        getConfig().remove("token")
    }

    fun getTokenInfo(): TokenInfo? {
        return getConfig().getObject("token", TokenInfo::class.java)
    }

    private fun getConfig() = KvHelper("user")

}
