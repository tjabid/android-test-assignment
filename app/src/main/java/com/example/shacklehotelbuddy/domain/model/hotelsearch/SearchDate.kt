package com.example.shacklehotelbuddy.domain.model.hotelsearch

data class SearchDate(
    val day: String,
    val month: String,
    val year: String
) {
    override fun toString(): String = "${day.ifEmpty { "DD" }}/${month.ifEmpty { "MM" }}/${year.ifEmpty { "YY" }}"
}