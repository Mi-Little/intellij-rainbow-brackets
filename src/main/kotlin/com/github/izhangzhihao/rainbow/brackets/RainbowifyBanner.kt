package com.github.izhangzhihao.rainbow.brackets

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowConfigurable
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.github.izhangzhihao.rainbow.brackets.util.memoizedFileExtension
import com.github.izhangzhihao.rainbow.brackets.visitor.RainbowHighlightVisitor.Companion.checkForBigFile
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.versions.createComponentActionLabel

class RainbowifyBanner(private val project: Project) : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor): EditorNotificationPanel? {

        if (!RainbowSettings.instance.isRainbowEnabled) {
            if (RainbowSettings.instance.suppressDisabledCheck) return null
            return EditorNotificationPanel().apply {
                text("Rainbow Brackets is now disabled.")
                icon(AllIcons.General.Warning)
                createComponentActionLabel("got it, don't show again") {
                    RainbowSettings.instance.suppressDisabledCheck = true
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }

                createComponentActionLabel("open setting") {
                    ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }

        val psiFile = file.toPsiFile(project)
        if (psiFile != null && !checkForBigFile(psiFile)) {
            if (RainbowSettings.instance.suppressBigFileCheck) return null
            return EditorNotificationPanel().apply {
                text("Rainbowify is disabled by default for files > 1000 lines.")
                icon(AllIcons.General.Information)
                createComponentActionLabel("got it, don't show again") {
                    RainbowSettings.instance.suppressBigFileCheck = true
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }

                createComponentActionLabel("open setting") {
                    ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }

        if (
                RainbowSettings.instance.languageBlacklist.contains(file.fileType.name) ||
                RainbowSettings.instance.languageBlacklist.contains(memoizedFileExtension(file.name))
        ) {
            if (RainbowSettings.instance.suppressBlackListCheck) return null
            return EditorNotificationPanel().apply {
                text("This language/file extensions is in the black list, will not be rainbowify")
                icon(AllIcons.General.Information)

                createComponentActionLabel("got it, don't show again") {
                    RainbowSettings.instance.suppressBlackListCheck = true
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }

                createComponentActionLabel("open setting") {
                    ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }

        return null
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("RainbowifyBanner")
    }
}