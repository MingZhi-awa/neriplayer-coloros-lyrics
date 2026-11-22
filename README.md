# 音理音理 锁屏歌词增强

给 NeriPlayer（音理音理）在 ColorOS 上补上实时歌词的最后两块拼图。模块 hook 在播放器进程内，不重建 metadata、不碰封面、不伪造播放状态。

- 宿主：`moe.ouom.neriplayer`
- 依赖：LSPosed（libxposed API 102）
- 可选搭配：[ColorOS Live Lyrics Bridge](https://github.com/Andrea-lyz/ColorOS-Live-Lyrics-Bridge)

## 它解决了什么

音理音理本身已经按协议写出完整的 `lyricInfo`（含逐字 `rawLyric` 与 `translationLyric`），所以这个模块不发明歌词通道，只补两处宿主做不到的事：

1. **声明为 Universal Player Provider**：向 Bridge 发送 `PLAYER_BINDINGS_CHANGED` 绑定广播，Bridge 随即把音理音理纳入受管播放器，放开 RUS 白名单、歌词入口 52、媒体黑名单与 AOD 媒体面板。也就是说，不再需要在音理音理里用 root / Shizuku 改系统配置。
2. **公开翻译切换动作**：在 `PlaybackState` 上追加 `io.github.andrealtb.lockscreenlyrics.action.TOGGLE_TRANSLATION`。Bridge 负责把它绑进收藏槽并自己处理点击，于是逐字卡拉 OK 与翻译按钮都可用。

## 使用

1. 在 LSPosed 中启用模块，作用域勾选「音理音理」。
2. 在音理音理设置里打开 ColorOS 锁屏歌词开关。
3. 安装 Bridge 后重新启动播放器，设置页里的「重新向 Bridge 声明绑定」可手动重发广播。

模块设置页不在桌面显示图标，可从 LSPosed 模块详情进入，或 `adb shell am start -n io.github.mingzhiawa.neriplayerlyrics/.SettingsActivity`。

## 构建

推送即由 GitHub Actions 构建 debug APK；推 `v*` 标签会发布到 Release。

```
./gradlew :app:assembleDebug
```

## 许可证

仅作用于 `moe.ouom.neriplayer`，与音理音理官方无关。

---

Made by 猫羽bot
