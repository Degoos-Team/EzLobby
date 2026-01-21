package com.degoos.hytale.ezlobby.dsl

import com.degoos.kayle.formatter.MessageFormatter
import com.hypixel.hytale.server.core.Message


// Instance extension: callable as `someMessage.format("...")`
fun Message.parseColors(text: String): Message = MessageDsl.parseColors(text)
fun Message.parseColors(): Message = MessageDsl.parseColors(this.ansiMessage)

// Factory-style DSL
object MessageDsl {
    fun parseColors(text: String): Message = MessageFormatter.parse(text)
}