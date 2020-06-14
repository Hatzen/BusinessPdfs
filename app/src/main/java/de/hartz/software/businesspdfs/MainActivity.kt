package de.hartz.software.businesspdfs

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.github.lucasfsc.html2pdf.Html2Pdf
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private val requestCodeStoragePermission = 1124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (isStoragePermissionGranted()) {
                createPdfFromTemplate()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCodeStoragePermission)
            }
        }
    }
    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(javaClass.simpleName, "Permission is granted")
                return true
            } else {

                Log.v(javaClass.simpleName, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(javaClass.simpleName, "Permission is granted")
            return true
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeStoragePermission) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(javaClass.simpleName, "Permission: " + permissions[0] + "was " + grantResults[0])
                //resume tasks needing this permission
                createPdfFromTemplate()
            }
        }
    }

    private fun createPdfFromTemplate() {
        var htmlContent = assets.open("html/index.html").bufferedReader().use { it.readText() }

        val sections = mutableListOf("A", "B", "C", "D", "E")
        val questions = mutableListOf("1", "2", "3")
        val answers = mutableListOf("I", "II", "III", "IV")
        val replacementSection = replaceSection(sections, questions, answers)
        val replacementKeySection = "{\$SECTION}"
        htmlContent = htmlContent.replace(replacementKeySection, replacementSection)

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

    private fun replaceSection(sections: List<String>, questions: List<String>, answers: List<String>): String {
        val replaceableKey = "{SECTION}"
        val replaceableKeyQuestions = "{\$QUESTION}"
        var result = ""
        var isFirstSection = true
        for (section in sections) {
            val inflate = getHtmlPartFromAssets("section")
            result += inflate
            if (isFirstSection) {
                result = result.replace("{ADDITIONAL_CLASS}", "firstSection")
                isFirstSection = false
            }
            result = result.replace(replaceableKey, "Section " + section)
            result = result.replace(replaceableKeyQuestions, replaceQuestion(questions, answers))
        }
        return result
    }

    private fun replaceQuestion(questions: List<String>, answers: List<String>): String {
        val replaceableKey = "{QUESTION}"
        val replaceableKeyNumberOfQuestions = "{#ANSWER}"
        val replaceableKeyAnswers = "{\$ANSWER}"
        var result = ""
        for (question in questions) {
            val inflate = getHtmlPartFromAssets("question")
            result += inflate
            result = result.replace(replaceableKey, "Question " + question)
            result = result.replace(replaceableKeyNumberOfQuestions, answers.size.toString())
            result = result.replace(replaceableKeyAnswers, replaceAnswer(answers))
        }
        return result
    }

    private fun replaceAnswer(answers: List<String>): String {
        val replaceableKey = "{ANSWER}"
        var result = ""
        for (answer in answers) {
            val inflate = getHtmlPartFromAssets("answer")
            result += inflate
            val constant = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
            val constant2 = "<input type='checkbox'> Answer"
            result = result.replace(replaceableKey, constant2 + constant + answer)
        }
        return result
    }

    private fun getHtmlPartFromAssets(filename: String): String {
        return assets.open("html/parts/" + filename + ".html").bufferedReader().use { it.readText() }
    }

    private fun openPdf(file: File) {
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