package de.hartz.software.businesspdfs.implementation

import android.app.Activity
import de.hartz.software.businesspdfs.SimpleReplacement
import de.hartz.software.businesspdfs.SingleEntityReplacement
import de.hartz.software.businesspdfs.implementation.model.Question
import de.hartz.software.businesspdfs.implementation.model.Section

class QuestionReplacement(datasource: Question, activity: Activity) :
    SingleEntityReplacement<Question>(datasource, activity) {
    override val replacements: Map<String, (Question) -> String>
        get() = mapOf(


        )

    override fun getReplacement(source: String, datasource: Question): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSingleReplacement(source: String, datasource: Question): String {
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


        result = SimpleReplacement<Int>(replaceableKeyNumberOfQuestions).replace(result, datasource.answers.size)
        result = AnswerReplacement().replace(result, datasource.answers[0])

        return result
    }

    override fun getTemplate(): String {
        return "section"
    }

}