package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.androiddevs.shoppinglisttestingyt.launchFragmentInHiltContainer
import com.androiddevs.shoppinglisttestingyt.ui.ShoppingFragment
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class) // makes sure all tests run on emulator. To tell junit these are instrumented tests
// we commented this because we after defining HiltTestRunner then we dont need it.
@SmallTest  //https://developer.android.com/reference/androidx/test/filters/SmallTest
@HiltAndroidTest // specifies for hilt that we want to inject dependencies in this test class.
class ShoppingDaoTest {


    /**
     * Rule for Hilt to inject
     */
    @get:Rule
    var hiltRule = HiltAndroidRule(this)



    /**
     * A JUnit Test Rule that swaps the background executor used by the Architecture Components with a different one which executes each task synchronously.

    You can use this rule for your host side tests that use Architecture Components.

    https://developer.android.com/reference/androidx/arch/core/executor/testing/InstantTaskExecutorRule
     */
    @get:Rule
    var instantTaskExecutorRule =
        InstantTaskExecutorRule() // to tell junit to run tests methods one after the other

//    private lateinit var database: ShoppingItemDatabase without HiltTestRunner

    @Inject
    @Named("test_db")
    /**
     *
     * Hilt does not know which database it should inject. Because we provide ShoopingItemDatabase dependency in AppModule too.
     *
     * But we want it from the TestAppModule we defined.
     *
     * Thats why we add @Named()
     */
    lateinit var database: ShoppingItemDatabase


    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        // inMemoryDatabaseBuilder : not a real databse holds inside ram only for test case and not as file on hd since this is for testing
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            ShoppingItemDatabase::class.java
//        ).allowMainThreadQueries() //for testing all tests one after the other.
//            .build()

        hiltRule.inject()

        dao = database.shoppingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

//    @Test
//    fun testlaunchFragmentInHiltContainer()
//    {
//       launchFragmentInHiltContainer<ShoppingFragment> {  }
//
//    }

    @Test
    fun insertShoppingItem() =
        runBlockingTest { // just like runblocking but optimized for testing (suspend method on main thread without coroutine) )

            val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
            dao.insertShoppingItem(shoppingItem)

            /**
             *  observeAllShoppingItems() returns LiveData which is asynchronous but we dont want asynchronous in test case.
             *  To solve this, use LiveDataUtilAndroidTest.kt -
             */
            val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

            assertThat(allShoppingItems).contains(shoppingItem)

        }

    @Test
    fun deleteShoppingItem() = runBlockingTest {

        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)

        dao.deleteShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {

        val shoppingItem1 = ShoppingItem("name 1", 2, 10f, "url", id = 1)
        val shoppingItem2 = ShoppingItem("name 2", 4, 5.5f, "url", id = 2)
        val shoppingItem3 = ShoppingItem("name 3", 0, 100f, "url", id = 3)

        dao.insertShoppingItem(shoppingItem1)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPrice = dao.observeTotalPrice().getOrAwaitValue()

        assertThat(totalPrice).isEqualTo(2*10f + 4*5.5f + 0)


    }
}