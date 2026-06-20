package com.cws.print

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

fun SafeCoroutineScope(context: CoroutineContext) = CoroutineScope(context + Print.coroutineExceptionHandler)