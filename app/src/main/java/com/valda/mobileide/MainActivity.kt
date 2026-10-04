package com.valda.mobileide

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import java.io.File

class MainActivity : Activity() {

    private lateinit var projectDir: File
    private lateinit var editor: EditText
    private lateinit var currentLabel: TextView
    private lateinit var status: TextView

    private var currentFile: File? = null

    private val starterFiles = linkedMapOf(

        "index.html" to """
<!doctype html>

<html>

<head>

<meta name="viewport"
content="width=device-width, initial-scale=1">

<title>Valda IDE</title>

<link rel="stylesheet"
href="style.css">

</head>

<body>

<h1>Hello from Valda IDE!</h1>

<p>Edit HTML, CSS dan JavaScript
langsung dari HP.</p>

<button onclick="sayHello()">
Click me
</button>

<script src="script.js"></script>

</body>

</html>
""",

        "style.css" to """
body {
    font-family: sans-serif;
    padding: 24px;
    background: #f5f3ff;
    color: #25213a;
}

h1 {
    color: #6750a4;
}

button {
    padding: 10px 16px;
    border: 0;
    border-radius: 8px;
    background: #6750a4;
    color: white;
}
""",

        "script.js" to """
function sayHello() {

    alert("Hello from Valda IDE!");

}
""",

        "README.txt" to """
Valda Mobile IDE

Project editor untuk Android.
"""
    )

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        projectDir =
            File(filesDir, "projects/MyFirstProject")

        if (!projectDir.exists()) {

            projectDir.mkdirs()

            starterFiles.forEach {

                (name, content) ->

                File(projectDir, name)
                    .writeText(content)
            }
        }

        buildUi()

        loadFile(
            File(projectDir, "index.html")
        )
    }

    private fun dp(value: Int): Int {

        return (
            value *
            resources.displayMetrics.density
        ).toInt()
    }

    private fun buildUi() {

        val root = LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            0xFF10131B.toInt()
        )

        val top = LinearLayout(this)

        top.orientation =
            LinearLayout.HORIZONTAL

        top.gravity =
            Gravity.CENTER_VERTICAL

        top.setPadding(
            dp(8),
            dp(8),
            dp(8),
            dp(8)
        )

        top.setBackgroundColor(
            0xFF1B2030.toInt()
        )

        val title = TextView(this)

        title.text =
            "VALDA IDE"

        title.textSize = 18f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            0xFFFFFFFF.toInt()
        )

        top.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
            )
        )

        top.addView(
            button("＋ File") {
                createFile()
            }
        )

        top.addView(
            button("Save") {
                saveFile()
            }
        )

        root.addView(top)

        val actions =
            LinearLayout(this)

        actions.orientation =
            LinearLayout.HORIZONTAL

        actions.setPadding(
            dp(6),
            dp(4),
            dp(6),
            dp(4)
        )

        actions.addView(
            button("Files") {
                showFiles()
            }
        )

        actions.addView(
            button("Preview") {
                showPreview()
            }
        )

        actions.addView(
            button("Info") {
                showProjectInfo()
            }
        )

        root.addView(actions)

        currentLabel =
            TextView(this)

        currentLabel.text =
            "index.html"

        currentLabel.textSize =
            13f

        currentLabel.setTextColor(
            0xFFBDB4FE.toInt()
        )

        currentLabel.setPadding(
            dp(12),
            dp(8),
            dp(12),
            dp(8)
        )

        currentLabel.setBackgroundColor(
            0xFF202638.toInt()
        )

        root.addView(currentLabel)

        editor =
            EditText(this)

        editor.setTextColor(
            0xFFE6E8F0.toInt()
        )

        editor.setHintTextColor(
            0xFF8D95A8.toInt()
        )

        editor.setBackgroundColor(
            0xFF10131B.toInt()
        )

        editor.gravity =
            Gravity.TOP or Gravity.START

        editor.typeface =
            Typeface.MONOSPACE

        editor.textSize =
            14f

        editor.setPadding(
            dp(12),
            dp(12),
            dp(12),
            dp(20)
        )

        editor.setHorizontallyScrolling(
            true
        )

        editor.isSingleLine =
            false

        editor.hint =
            "Tulis kode di sini..."

        root.addView(
            editor,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        status =
            TextView(this)

        status.text =
            "Ready • MyFirstProject"

        status.textSize =
            12f

        status.setTextColor(
            0xFFB8C0D4.toInt()
        )

        status.setPadding(
            dp(10),
            dp(8),
            dp(10),
            dp(8)
        )

        status.setBackgroundColor(
            0xFF1B2030.toInt()
        )

        root.addView(status)

        setContentView(root)
    }

    private fun button(
        text: String,
        action: () -> Unit
    ): Button {

        return Button(this).apply {

            this.text = text

            textSize = 12f

            isAllCaps = false

            setOnClickListener {
                action()
            }
        }
    }

    private fun loadFile(file: File) {

        if (!file.exists()) return

        currentFile = file

        currentLabel.text =
            file.name

        editor.setText(
            file.readText()
        )

        status.text =
            "Opened ${file.name}"
    }

    private fun saveFile() {

        val file =
            currentFile
                ?: return toast(
                    "Pilih file dahulu"
                )

        try {

            file.writeText(
                editor.text.toString()
            )

            status.text =
                "Saved ${file.name}"

            toast(
                "File tersimpan"
            )

        } catch (e: Exception) {

            toast(
                "Gagal menyimpan"
            )
        }
    }

    private fun showFiles() {

        val files =
            projectDir.listFiles()
                ?.filter {
                    it.isFile
                }
                ?.sortedBy {
                    it.name
                }
                ?: emptyList()

        if (files.isEmpty()) {

            toast(
                "Folder kosong"
            )

            return
        }

        AlertDialog.Builder(this)

            .setTitle(
                "Project files"
            )

            .setItems(
                files.map {
                    it.name
                }.toTypedArray()
            ) { _, which ->

                saveFile()

                loadFile(
                    files[which]
                )
            }

            .setNegativeButton(
                "Tutup",
                null
            )

            .show()
    }

    private fun createFile() {

        val input =
            EditText(this)

        input.hint =
            "contoh: app.js"

        AlertDialog.Builder(this)

            .setTitle(
                "Buat file"
            )

            .setView(input)

            .setNegativeButton(
                "Batal",
                null
            )

            .setPositiveButton(
                "Buat"
            ) { _, _ ->

                val name =
                    input.text
                        .toString()
                        .trim()

                if (
                    name.isBlank() ||
                    name.contains("/") ||
                    name.contains("\\")
                ) {

                    toast(
                        "Nama file tidak valid"
                    )

                    return@setPositiveButton
                }

                val file =
                    File(
                        projectDir,
                        name
                    )

                if (file.exists()) {

                    toast(
                        "File sudah ada"
                    )

                } else {

                    file.writeText("")

                    loadFile(file)

                    toast(
                        "File dibuat"
                    )
                }
            }

            .show()
    }

    private fun showPreview() {

        saveFile()

        val html =
            File(
                projectDir,
                "index.html"
            )

        if (!html.exists()) {

            toast(
                "index.html tidak ditemukan"
            )

            return
        }

        val web =
            WebView(this)

        web.settings.javaScriptEnabled =
            true

        web.settings.domStorageEnabled =
            true

        web.webViewClient =
            WebViewClient()

        web.loadUrl(
            "file://${html.absolutePath}"
        )

        AlertDialog.Builder(this)

            .setTitle(
                "HTML Preview"
            )

            .setView(web)

            .setPositiveButton(
                "Tutup",
                null
            )

            .show()
    }

    private fun showProjectInfo() {

        AlertDialog.Builder(this)

            .setTitle(
                "Valda Mobile IDE"
            )

            .setMessage(
                """
Project: MyFirstProject

Editor:
HTML
CSS
JavaScript
XML
Kotlin
Java
JSON
TXT

Build Android project:
GitHub Actions
""".trimIndent()
            )

            .setPositiveButton(
                "OK",
                null
            )

            .show()
    }

    private fun toast(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
