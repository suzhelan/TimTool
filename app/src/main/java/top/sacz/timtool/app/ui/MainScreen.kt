package top.sacz.timtool.app.ui

import android.R.attr.onClick
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.util.TextInfo
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.ui.dialog.UpdateLogDialog
import top.sacz.timtool.util.TimeUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical

private const val AUTHOR_HOME_URL = "https://github.com/suzhelan"
private const val APP_HOME_URL = "https://suzhelan.top"
private const val TELEGRAM_URL = "https://t.me/timtool"
private const val REPO_URL = "https://github.com/suzhelan/TimTool"
private const val HELP_URL = "https://deeply-raptor-37d.notion.site/Root-2c0ef1ecbe4a80939e39c3e53cd20525"

@Preview
@Composable
fun MainScreen(
    isHideIcon: Boolean = false,
    onHideIconChange: (Boolean) -> Unit = {}
) {
    MiuixTheme(
        controller = ThemeController(ColorSchemeMode.MonetSystem),
    ) {
        Scaffold {
            HomePager(
                isHideIcon = isHideIcon,
                onHideIconChange = onHideIconChange
            )
        }
    }
}

@Composable
private fun HomePager(
    isHideIcon: Boolean,
    onHideIconChange: (Boolean) -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.module_name),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AboutCard()
                    LinkCard()
                    HideIconCard(
                        isHideIcon = isHideIcon,
                        onHideIconChange = onHideIconChange
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutCard() {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        pressFeedbackType = PressFeedbackType.Tilt,
        showIndication = true,
        onClick = { uriHandler.openUri(AUTHOR_HOME_URL) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.mipmap.ic_launcher_round),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.module_name),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "TIM 功能性 Xposed 模块",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${TimeUtils.formatTime(BuildConfig.BUILD_TIME)})",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LinkCard() {
    val uriHandler = LocalUriHandler.current
    Column {
        Card(modifier = Modifier.fillMaxWidth()) {
            ArrowPreference(
                title = "Telegram",
                onClick = { uriHandler.openUri(TELEGRAM_URL) }
            )
            ArrowPreference(
                title = "下载地址",
                onClick = { uriHandler.openUri(APP_HOME_URL) }
            )
            ArrowPreference(
                title = "LSP仓库",
                onClick = { uriHandler.openUri(REPO_URL) }
            )
            ArrowPreference(
                title = "更新日志",
                onClick = { UpdateLogDialog().show() }
            )
            ArrowPreference(
                title = "使用教程",
                onClick = { uriHandler.openUri(HELP_URL) }
            )
        }
    }
}

@Composable
private fun HideIconCard(
    isHideIcon: Boolean,
    onHideIconChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("timtool_prefs", Context.MODE_PRIVATE)
    var isChecked by remember(isHideIcon) { mutableStateOf(isHideIcon) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "隐藏桌面图标",
                    fontSize = 17.sp
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = { newValue ->
                    if (newValue) {
                        MessageDialog.show(
                            "确认要隐藏图标吗",
                            "这样会让模块在桌面上消失,但是你可以在框架打开模块"
                        )
                            .setOkButton("确定")
                            .setOkTextInfo(TextInfo().setFontColor(Color(0xFFEB5545).toArgb()).setBold(true))
                            .setOkButton { _, _ ->
                                isChecked = true
                                prefs.edit().putBoolean("hide_icon", true).apply()
                                onHideIconChange(true)
                                false
                            }
                            .setCancelButton("取消") { _, _ ->
                                false
                            }
                    } else {
                        isChecked = false
                        prefs.edit().putBoolean("hide_icon", false).apply()
                        onHideIconChange(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFFFFF),
                    uncheckedThumbColor = Color(0xFFAAAAAA),
                    checkedTrackColor = Color(0xB5FFFB03),
                    uncheckedTrackColor = Color(0xB35B6775),
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}
