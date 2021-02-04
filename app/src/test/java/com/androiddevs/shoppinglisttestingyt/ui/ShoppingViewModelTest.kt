package com.androiddevs.shoppinglisttestingyt.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.MainCoroutineRule
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValueTest
import com.androiddevs.shoppinglisttestingyt.other.Constants
import com.androiddevs.shoppinglisttestingyt.other.Constants.TEST_IMAGE_URL
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRespository
import com.androiddevs.shoppinglisttestingyt.respositories.DefaultShoppingRespository
import com.androiddevs.shoppinglisttestingyt.respositories.ShoppingRespository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRespository())

    }

    @Test
    fun `insert shopping item with empty field, returns error`() {

        viewModel.insertShoppingItem("name","","3.0")

        val value = viewModel.insertShopingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, returns error`() {

        /**
         * Like this it wont matter if MAX_NAME_LENGTH changes other than 20.
         * It will still work for any length
         */
        val string = buildString {
            for(i in 1..Constants.MAX_NAME_LENGTH + 1){
                append(1)
            }
        }

        viewModel.insertShoppingItem(string,"","3.0")

        val value = viewModel.insertShopingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }


    @Test
    fun `insert shopping item with too long price, returns error`() {

        /**
         * Like this it wont matter if MAX_PRICE_LENGTH changes other than 20.
         * It will still work for any length
         */
        val string = buildString {
            for(i in 1..Constants.MAX_PRICE_LENGTH + 1){
                append(1)
            }
        }

        viewModel.insertShoppingItem("name","5",string)

        val value = viewModel.insertShopingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() {

        viewModel.insertShoppingItem("name","99999999999999999999999999999999999999","3.0")

        val value = viewModel.insertShopingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with valid input, returns success`() {

        viewModel.insertShoppingItem("name","5","3.0")

        val value = viewModel.insertShopingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    /**
     * https://github.com/subind/TestingBabySteps/blob/testViewModel/app/src/test/java/com/bridgetree/testingbabysteps/ui/ShoppingViewModelTest.kt
     */
    @Test
    fun `check currentImageUrl is empty after inserting item in db`()
    {
        viewModel.insertShoppingItem("some name","4","5.0")

        val value = viewModel.currentImageUrl.getOrAwaitValueTest()

        assertThat(value).isEmpty()
    }

    @Test
    fun `check currentImageUrl emits same url passed to it`()
    {
        viewModel.setCurrentImageUrl(TEST_IMAGE_URL)

        val value = viewModel.currentImageUrl.getOrAwaitValueTest()

        assertThat(value).isEqualTo(TEST_IMAGE_URL)

    }
}

