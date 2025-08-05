package ru.nucodelabs.gem.config

import org.slf4j.LoggerFactory

fun slf4j(instance: Any) = LoggerFactory.getLogger(instance::class.java)!!
