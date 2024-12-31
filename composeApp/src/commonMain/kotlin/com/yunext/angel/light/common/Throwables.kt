package com.yunext.angel.light.common

fun throwableOf(msgBlock: () -> String): Throwable = RuntimeException(msgBlock())

fun throwableOf(msg: String): Throwable = RuntimeException(msg)

fun throwableCaseOf(case: Throwable): Throwable = RuntimeException(case)

fun throwableCaseOf(caseBlock: () -> Throwable): Throwable = RuntimeException(caseBlock())