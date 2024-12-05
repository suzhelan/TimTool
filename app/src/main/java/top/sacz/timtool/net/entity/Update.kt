package top.sacz.timtool.net.entity

import java.util.Date

data class HasUpdate(
    val hasUpdate: Boolean,
    val isForceUpdate: Boolean,
    val version: Int
)

data class UpdateInfo(
    val fileName: String,
    val forceUpdate: Boolean,
    val id: Int,
    val time: Date,
    val updateLog: String,
    val versionCode: Int,
    val versionName: String
)