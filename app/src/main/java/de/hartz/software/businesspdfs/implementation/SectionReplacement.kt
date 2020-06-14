package de.hartz.software.businesspdfs.implementation

import android.app.Activity
import de.hartz.software.businesspdfs.SingleEntityReplacement
import de.hartz.software.businesspdfs.implementation.model.Section

class SectionReplacement(datasource: Section, activity: Activity) :
    SingleEntityReplacement<Section>(datasource, activity) {

    override fun getSingleReplacement(datasource: Section) {
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

    override fun getTemplate(): String {
        return "section"
    }

}