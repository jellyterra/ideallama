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

import java.awt.Component
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.filechooser.FileFilter

fun ShowWindow(w: Window) {
    val dimemsion: Dimension = Toolkit.getDefaultToolkit().screenSize
    w.setLocation(
        dimemsion.width / 2 - w.size.width / 2,
        dimemsion.height / 2 - w.size.height / 2
    )
    w.isVisible = true
}

fun NewVBox(vararg components: Component): Box {
    val box = Box.createVerticalBox()!!
    for (component in components) box.add(component)
    return box
}

fun NewHBox(vararg components: Component): Box {
    val box = Box.createHorizontalBox()!!
    for (component in components) box.add(component)
    return box
}

fun NewButton(text: String, f: (b: JButton) -> Unit): JButton {
    val button = JButton(text)
    button.addActionListener(ActionListener { f(button) })
    return button
}

fun <E> NewComboBox(data: Array<E>, f: (JComboBox<E>) -> Unit): JComboBox<E> {
    val comboBox = JComboBox(data)
    comboBox.addActionListener { f(comboBox) }
    return comboBox
}

fun NewSpinner(model: SpinnerModel, f: (JSpinner) -> Unit): JSpinner {
    val spinner = JSpinner(model)
    spinner.addChangeListener { f(spinner) }
    return spinner
}

fun NewTextField(default: String = "", f: (JTextField) -> Unit): JTextField {
    val textField = JTextField(default)
    textField.addActionListener { f(textField) }
    return textField
}

fun NewMenuBar(vararg menus: JMenu): JMenuBar {
    val menuBar = JMenuBar()

    for (menu in menus) {
        menuBar.add(menu)
    }

    return menuBar
}

fun NewMenu(label: String, vararg menuItems: JComponent): JMenu {
    val menu = JMenu(label)

    for (menuItem in menuItems) {
        menu.add(menuItem)
    }

    return menu
}

fun NewMenuItem(
    label: String,
    actionListener: ActionListener,
): JMenuItem {
    val menuItem = JMenuItem(label)
    menuItem.addActionListener {
        try {
            actionListener.actionPerformed(null)
        } catch (e: Exception) {
            ExceptionDialog(e)
        }
    }
    return menuItem
}

fun NewMenuItem(
    label: String,
    keyStroke: KeyStroke,
    actionListener: ActionListener,
): JMenuItem {
    val menuItem = JMenuItem(label)
    menuItem.addActionListener {
        try {
            actionListener.actionPerformed(null)
        } catch (e: Exception) {
            ExceptionDialog(e)
        }
    }
    menuItem.accelerator = keyStroke
    return menuItem
}

fun MessageDialog(title: String, message: String, level: Int = JOptionPane.INFORMATION_MESSAGE) {
    JOptionPane.showMessageDialog(null, message, title, level)
}

fun ConfirmDialog(title: String, message: String, onOk: ActionListener, onCancel: ActionListener) {
    JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION).let {
        if (it == JOptionPane.OK_OPTION) {
            onOk.actionPerformed(null)
        } else {
            onCancel.actionPerformed(null)
        }
    }
}

fun OpenFileDialog(filters: Array<FileFilter> = arrayOf()): JFileChooser {
    val fileChooser = JFileChooser()
    for (filter in filters) {
        fileChooser.addChoosableFileFilter(filter)
    }
    fileChooser.showOpenDialog(null)
    return fileChooser
}

fun SaveFileDialog(): JFileChooser {
    val fileChooser = JFileChooser()
    fileChooser.showSaveDialog(null)
    return fileChooser
}

fun <T : Component> BindWidth(parent: Component, component: T): T {
    component.addPropertyChangeListener { component.size.width = parent.width }
    return component
}

fun ExceptionDialog(e: Exception, w: Window? = null) {
    val stackTrace = e.stackTraceToString()
    println(stackTrace)

    val textPane = JTextPane()
    textPane.isEditable = false
    textPane.text = stackTrace
    JOptionPane.showMessageDialog(w, JScrollPane(textPane), "Error", JOptionPane.ERROR_MESSAGE)
}

fun <E> UpdateListModel(a: List<E>, m: DefaultListModel<E>) {
    m.removeAllElements()
    for (e in a) {
        m.addElement(e)
    }
}

fun NewDocumentListener(a: ActionListener): DocumentListener {
    return object : DocumentListener {
        override fun insertUpdate(p0: DocumentEvent?) {
            a.actionPerformed(null)
        }

        override fun removeUpdate(p0: DocumentEvent?) {
            a.actionPerformed(null)
        }

        override fun changedUpdate(p0: DocumentEvent?) {
            a.actionPerformed(null)
        }
    }
}
