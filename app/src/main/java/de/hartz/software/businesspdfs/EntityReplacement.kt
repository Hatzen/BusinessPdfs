package de.hartz.software.businesspdfs

import android.app.Activity


interface Replacement<T> {

    val replacements: Map<String, (T) -> String>

    fun replace(source: String, datasource: T): String {
        return source.replace(getPlaceholderName(), getReplacement(source, datasource))
    }

    /**
     * Returns the real value or nested HTML that should be displayed.
     */
    fun getReplacement(source: String, datasource: T): String

    /**
     * Returns a String representing this class
     */
    fun getPlaceholderName(): String {
        return "{" + javaClass.simpleName + "}"
    }
}

open class SimpleReplacement<T: Any>(val valueSupplier: (T) -> String = {any: T -> any.toString()}): Replacement<T> {
    override val replacements: Map<String, (T) -> String>
        get() = mapOf(Pair(key, valueSupplier))

    override fun getReplacement(source: String, datasource: T): String {
        return replacements.getValue(getPlaceholderName()).invoke(datasource)
    }

}

abstract class SingleEntityReplacement<T>(val datasource: T, val activity: Activity): Replacement<T> {



    override fun getReplacement(source: String): String {
        val template = getTemplateFromAssets()
        val result = replace(template, datasource)
        return result
    }

    abstract fun getSingleReplacement(datasource: T)

    /**
     * Return filename without extension of the HTML-Template File.
     */
    abstract fun getTemplate(): String


    private fun getTemplateFromAssets(): String {
        return activity.assets.open("html/parts/" + getTemplate() + ".html").bufferedReader().use { it.readText() }
    }
}