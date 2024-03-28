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
// along with this program.  If not, see <https:www.gnu.org/licenses/>.

package com.github.jellytea.ideallama

import com.intellij.collaboration.ui.SimpleHtmlPane
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.addKeyboardAction
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.KeyStroke

class ToolChat : ToolWindowFactory {
    var chatLog = ""

    val panel = BorderLayoutPanel()
    val chatView = SimpleHtmlPane()
    val promptField = JBTextArea()

    val buttonSend = NewButton("Send (Shift+Enter)") { buttonSend ->
        promptField.isEnabled = false
        buttonSend.isEnabled = false

        LlamaBridge(config.bridge).generate(config.model, promptField.text).thenAccept { resp ->
            if (resp.statusCode() != 200) {
                MessageDialog(
                    "Error",
                    "LLaMAsh: Failed to communicate with Ollama.\n${resp.body()}",
                    JOptionPane.ERROR_MESSAGE
                )
                return@thenAccept
            }

            chatLog = "## ${promptField.text}\n\n${resp.body()}\n\n---\n\n$chatLog"

            chatView.text = Markdown2Html(chatLog)

            promptField.text = ""
        }.whenComplete { _, _ ->
            promptField.isEnabled = true
            buttonSend.isEnabled = true
        }.exceptionally { e ->
            MessageDialog(
                "Error",
                "Failed to communicate with LLaMAsh.\n${e.stackTraceToString()}",
                JOptionPane.ERROR_MESSAGE
            )
            throw e
        }
    }

    init {
        buttonSend.toolTipText = "Send message to instance."

        promptField.addKeyboardAction(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER,
                InputEvent.SHIFT_DOWN_MASK
            )
        ) { buttonSend.doClick() }

        val promptPanel = BorderLayoutPanel()
        promptPanel.add(buttonSend, BorderLayout.NORTH)
        promptPanel.add(JBScrollPane(promptField), BorderLayout.CENTER)

        val chatViewPanel = BorderLayoutPanel()
        chatViewPanel.add(JBScrollPane(chatView), BorderLayout.CENTER)
        chatViewPanel.add(
            NewHBox(
                NewButton("Clear All") {
                    chatLog = ""
                    chatView.text = ""
                },
                NewButton("View Markdown") {
                    val w = JFrame("View Markdown")
                    w.add(JBTextArea(chatLog))
                    w.size = Dimension(400, 300)
                    w.isVisible = true
                },
                NewButton("View HTML") {
                    val w = JFrame("View HTML")
                    w.add(JBTextArea(Markdown2Html(chatLog)))
                    w.size = Dimension(400, 300)
                    w.isVisible = true
                },
            ),
            BorderLayout.SOUTH
        )

        val splitter = JBSplitter(true)
        splitter.add(promptPanel)
        splitter.add(chatViewPanel)

        panel.add(splitter, BorderLayout.CENTER)
    }

    override fun createToolWindowContent(p0: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        val content = contentFactory.createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}
