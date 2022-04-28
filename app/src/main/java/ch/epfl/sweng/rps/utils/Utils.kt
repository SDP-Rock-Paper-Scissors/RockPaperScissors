package ch.epfl.sweng.rps.utils

fun consume(block: () -> Any?): () -> Unit = { block() }
