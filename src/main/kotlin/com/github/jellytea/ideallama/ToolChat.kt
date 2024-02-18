// Copyright (C) 2024 JetERA Creative
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

package com.github.jellytea.ideallama

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.BorderLayout
import javax.swing.JTextField
import javax.swing.JTextPane

class ToolChat : ToolWindowFactory {
    val viewChat = JTextPane()
    val fieldPrompt = JTextField()

    val buttonSend = NewButton("Send") {
        val buttonSend = it
        fieldPrompt.isEnabled = false
        buttonSend.isEnabled = false

        LlamaBridge(config.bridge).generate(config.model, fieldPrompt.text).thenAccept {
            viewChat.text += "Prompt: ${fieldPrompt.text}\n"
            viewChat.text += it.body()
            fieldPrompt.text = ""
        }.whenComplete { _, _ ->
            fieldPrompt.isEnabled = true
            buttonSend.isEnabled = true
        }
    }

    val panel = BorderLayoutPanel()

    init {
        viewChat.isEditable = false
        panel.add(viewChat, BorderLayout.CENTER)
        panel.add(NewHBox(fieldPrompt, buttonSend), BorderLayout.SOUTH)
    }

    override fun createToolWindowContent(p0: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        val content = contentFactory.createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}
