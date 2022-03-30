package com.tomorrowit.todo.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class FilterMode { ALL, OUTSTANDING, COMPLETED }

class ToDoRepository(
    private val store: ToDoEntity.Store,
    private val appScope: CoroutineScope,
    private val remoteDataSource: ToDoRemoteDataSource
) {

    fun items(filterMode: FilterMode = FilterMode.ALL): Flow<List<ToDoModel>> =
        filteredEntities(filterMode).map { all -> all.map { it.toModel() } }

    fun find(id: String?): Flow<ToDoModel?> = store.find(id).map { it?.toModel() }

    suspend fun save(model: ToDoModel) {
        withContext(appScope.coroutineContext) {
            store.save(ToDoEntity(model))
        }
    }

    suspend fun delete(model: ToDoModel) {
        withContext(appScope.coroutineContext) {
            store.delete(ToDoEntity(model))
        }
    }

    private fun filteredEntities(filterMode: FilterMode) = when (filterMode) {
        FilterMode.ALL -> store.all()
        FilterMode.OUTSTANDING -> store.filtered(isCompleted = false)
        FilterMode.COMPLETED -> store.filtered(isCompleted = true)
    }

    suspend fun importItems(url: String) {
        withContext(appScope.coroutineContext) {
            store.importItems(remoteDataSource.load(url).map { it.toEntity() })
        }
    }
}