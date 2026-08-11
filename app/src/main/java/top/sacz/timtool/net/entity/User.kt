package top.sacz.timtool.net.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class RequestLogin(
    @SerialName("uin") val uin: String,
)

@Serializable
data class User(
    /** QQ */
    @SerialName("uin") val uin: String = "",
    /** 昵称 */
    @SerialName("nickname") val nickname: String = "",

    /** 创建时间 */
    @SerialName("createTime") @Serializable(with = LocalDateTimeSerializer::class) val createTime: LocalDateTime = LocalDateTime.now(),
    /** 更新时间 */
    @SerialName("updateTime") @Serializable(with = LocalDateTimeSerializer::class) val updateTime: LocalDateTime = LocalDateTime.now()
)
