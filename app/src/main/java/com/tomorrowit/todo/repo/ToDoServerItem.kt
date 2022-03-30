package com.tomorrowit.todo.repo

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.time.Instant
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class ToDoServerItem(
    val description: String,
    val id: String,
    val completed: Boolean,
    val notes: String,
    @Json(name = "created_on") val createdOn: Instant
) {
    fun toEntity(): ToDoEntity {
        return ToDoEntity(
            id = id,
            description = description,
            isCompleted = completed,
            notes = notes,
            createdOn = createdOn
        )
    }
}

private val FORMATTER = DateTimeFormatter.ISO_INSTANT

class MoshiInstantAdapter {
    @ToJson
    fun toJson(date: Instant) = FORMATTER.format(date)

    @FromJson
    fun fromJson(dateString: String): Instant =
        FORMATTER.parse(dateString, Instant::from)
}