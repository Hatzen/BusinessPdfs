package de.hartz.software.businesspdfs

import android.app.Activity

class ReplacementFactory {

    companion object {
        fun <T: Replacement, MainEntity: SingleEntityReplacement<T>> createReplacement(dataSource: T, activity: Activity) {
            return MainEntity()
        }
    }

}