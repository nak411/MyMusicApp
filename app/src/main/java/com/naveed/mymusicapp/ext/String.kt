package com.naveed.mymusicapp.ext

/**
 * Converts the given string to exception object
 */
fun String.toException() = Exception(this)