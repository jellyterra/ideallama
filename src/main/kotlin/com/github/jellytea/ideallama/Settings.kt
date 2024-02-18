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

import com.google.gson.Gson
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.util.ui.FormBuilder
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

val config: Config = try {
    Gson().fromJson(File("${System.getenv("HOME")}/.config/ideallama.json").readText(), Config::class.java)
} catch (e: FileNotFoundException) {
    Config()
}

class Config {
    var bridge = ""
    var model = ""
}

class Settings : SearchableConfigurable {

    val bridgeField = JTextField()
    val modelField = JTextField()

    val form = FormBuilder.createFormBuilder()
        .addLabeledComponent("Bridge", bridgeField)
        .addLabeledComponent("Model", modelField)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    init {
        bridgeField.text = config.bridge
        modelField.text = config.model
    }

    override fun getId(): String {
        return "ideallama.id"
    }

    override fun getDisplayName(): String {
        return "IDEA LLaMA"
    }

    override fun createComponent(): JComponent {
        return form
    }

    override fun isModified(): Boolean {
        return config.bridge != bridgeField.text ||
                config.model != bridgeField.text
    }

    override fun apply() {
        config.bridge = bridgeField.text
        config.model = modelField.text

        File("${System.getenv("HOME")}/.config/ideallama.json").writeText(Gson().toJson(config))
    }
}