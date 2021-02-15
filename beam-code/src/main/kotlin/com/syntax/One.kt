package com.syntax

import org.slf4j.LoggerFactory


object One {
    val LOGGER = LoggerFactory.getLogger(javaClass)
    @JvmStatic
    fun main(args: Array<String>): Unit {
        LOGGER.info("One is running")
    }
}