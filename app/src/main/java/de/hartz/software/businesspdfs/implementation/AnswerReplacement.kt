package de.hartz.software.businesspdfs.implementation

import android.app.Activity
import de.hartz.software.businesspdfs.SimpleReplacement
import de.hartz.software.businesspdfs.SingleEntityReplacement
import de.hartz.software.businesspdfs.implementation.model.Answer
import de.hartz.software.businesspdfs.implementation.model.Section

class AnswerReplacement : SimpleReplacement<Answer>("{ANSWER}", {datasource -> datasource.answer})