package com.tomorrowit.todo.repo

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.*

//If we supply the tableName, Room will not choose the default class name (ToDoEntity).
//The entity class says "this is what my table should look like"
@Entity(tableName = "todos", indices = [Index(value = ["id"])])
data class ToDoEntity(
    val description: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val notes: String = "",
    val createdOn: Instant = Instant.now(),
    val isCompleted: Boolean = false
) {

    /*
    A @Dao class says “this
    is how I want to read and write from that table”. With Room, we define an interface
    or abstract class to describe the API that we want to have for working with the
    database. Room then code-generates an implementation for us, dealing with all of
    the SQLite code for getting our entities to and from our table.
    */
    @Dao
    interface Store {
        @Query("SELECT * FROM todos ORDER BY description")
        fun all(): Flow<List<ToDoEntity>>

        @Query("SELECT * FROM todos WHERE id = :modelId")
        fun find(modelId: String?): Flow<ToDoEntity?>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(vararg entities: ToDoEntity)

        @Delete
        suspend fun delete(vararg entities: ToDoEntity)
    }
}
