package com.androiddevs.shoppinglisttestingyt

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * If you see in shopping viewmodel, we use coroutine that calls insertItemIntoDb which is a suspend function, that uses MAIN  dispatcher which we dont have in our tests.
 * The coroutine MAIN dispatcher relies on the Main LOOPER which is available only in real app scenario.
 * But for tests we dont have real app scenario
 *
 * If we had viewmodel tests in androidTest directory then this would not be a problem.
 *
 * So we define a Junit Rule.
 *
 * Here we will use a special dispatcher which is not a main dispatcher
 */
@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), // test watcher implements test rule and so MainCoroutineRule becomes actual rule for Junit tests.
    TestCoroutineScope by TestCoroutineScope(dispatcher) //
{

    /**
     * When we start a coroutine using this rule using the "dispatcher" in above constructory, then we want to use Dispatcher Main.
     */
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    /**
     * Just undo what we did in starting()
     */
    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}