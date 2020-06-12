package de.hartz.software.businesspdfs

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.github.lucasfsc.html2pdf.Html2Pdf

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment.getExternalStorageDirectory
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        fab.setOnClickListener { view ->
            createPdfFromTemplate()
        }
    }

    private fun createPdfFromTemplate() {
        // TODO: Request Permission external storatge!!
        val replacments = mutableMapOf<String, String>()
        replacments["{NAME}"] = "The Company"
        replacments["{ADDRESS}"] = "an avenue Washingt 1234"
        replacments["{EMAIL}"] = "email@company.com"
        replacments["{HEADLINE}"] = "Dear Customer, "
        replacments["{TEXT}"] = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."

        var htmlContent = assets.open("html/index.html").bufferedReader().use { it.readText() }
        for (replacement in replacments) {
            htmlContent = htmlContent.replace(replacement.key, replacement.value)
        }

        // TODO: Split html in different files and generate some strings representing table rows and so.

        val target = File(Environment.getExternalStorageDirectory().toString(), "file.pdf")
        // val target = File.createTempFile("BusinessPdfPrefix-", ".pdf")
        Log.e(javaClass.simpleName, "Pdf saved as: " + target.absolutePath)

        val converter = Html2Pdf.Companion.Builder()
                .context(this)
                .html(htmlContent)
                .file(target)
                .build()

        converter.convertToPdf(object: Html2Pdf.OnCompleteConversion {
            override fun onFailed() {
                Toast.makeText(this@MainActivity, "Failed creating pdf!", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess() {
                openPdf(target)
            }

        })
    }

    private fun openPdf(file: File) {
        // TODO: File needs to shared via ContentProvider or shared memory!
        val target = Intent(Intent.ACTION_VIEW)
        target.setDataAndType(Uri.fromFile(file), "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        val intent = Intent.createChooser(target, "Open File")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Instruct the user to install a PDF reader here, or something
        }

    }
}