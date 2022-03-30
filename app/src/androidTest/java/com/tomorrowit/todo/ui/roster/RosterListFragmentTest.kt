package com.tomorrowit.todo.ui.roster

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.tomorrowit.todo.R
import com.tomorrowit.todo.repo.ToDoDatabase
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import com.tomorrowit.todo.ui.MainActivity
import org.junit.Test

import androidx.test.platform.app.InstrumentationRegistry
import com.tomorrowit.todo.repo.ToDoRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class RosterListFragmentTest {
    private lateinit var repo: ToDoRepository
    private val items = listOf(
        ToDoModel("this is a test"),
        ToDoModel("this is another test"),
        ToDoModel("this is... wait for it... yet another test")
    )

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = ToDoDatabase.newTestInstance(context)
        val appScope = CoroutineScope(SupervisorJob())

        repo = ToDoRepository(
            db.todoStore(),
            appScope,
            ToDoRemoteDataSource(OkHttpClient())
        )

        loadKoinModules(module {
            single { repo }
        })
        runBlocking { items.forEach { repo.save(it) } }
    }

    @Test
    fun testListContents() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.items)).check(matches(hasChildCount(3)))
    }
}