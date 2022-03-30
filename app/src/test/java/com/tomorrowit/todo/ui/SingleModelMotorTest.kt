package com.tomorrowit.todo.ui

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tomorrowit.todo.MainDispatcherRule
import com.tomorrowit.todo.repo.ToDoModel
import com.tomorrowit.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class SingleModelMotorTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(paused = true)

    private val testModel = ToDoModel("this is a test")

    private val repo: ToDoRepository = mock {
        on { find(testModel.id) } doReturn flowOf(testModel)
    }

    //underTest is a common name in unit tests for “the instance of the class that we are testing”.
    private lateinit var underTest: SingleModelMotor

    //@Before is a JUnit annotation that says “run this function before each of the test functions”
    @Before
    fun setUp() {
        underTest = SingleModelMotor(repo, testModel.id)
    }

    @Test
    fun `initial state`() {
        mainDispatcherRule.dispatcher.runCurrent()
        //we use runBlocking() to say that we want to execute a block of code synchronously even though it uses a suspend function (first())
        runBlocking {
            val item = underTest.states.first().item
            assertEquals(testModel, item)
        }
    }

    @Test
    fun `actions pass through to repo`() {
        val replacement = testModel.copy("whatevs")

        underTest.save(replacement)
        mainDispatcherRule.dispatcher.runCurrent()

        runBlocking { verify(repo).save(replacement) }

        underTest.delete(replacement)
        mainDispatcherRule.dispatcher.runCurrent()

        runBlocking { verify(repo).delete(replacement) }
    }
}
