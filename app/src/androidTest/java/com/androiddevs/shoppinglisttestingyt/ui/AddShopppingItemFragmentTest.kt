package com.androiddevs.shoppinglisttestingyt.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.androiddevs.shoppinglisttestingyt.*
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.repo.FakeAndroidShoppingRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import com.google.common.truth.Truth.assertThat
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class AddShopppingItemFragmentTest {
    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineAndroidRule()

    @Inject
    lateinit var fragmentFactory: ShoppingFragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun clickInsertIntoDb_shoppingItemInsertedIntoDb()
    {
        val testViewModel = ShoppingViewModel(FakeAndroidShoppingRepository())
        launchFragmentInHiltContainer<AddShopppingItemFragment>(fragmentFactory= fragmentFactory) {

            viewModel = testViewModel
        }

        onView(withId(R.id.etShoppingItemName)).perform(replaceText("Apples"))
        onView(withId(R.id.etShoppingItemAmount)).perform(replaceText("5"))
        onView(withId(R.id.etShoppingItemPrice)).perform(replaceText("20"))
        onView(withId(R.id.btnAddShoppingItem)).perform(click())

        assertThat(testViewModel.shoppingItems.getOrAwaitValue())
            .contains(ShoppingItem("Apples",5,20.0f,""))

    }

    @Test
    fun pressBackButton_popBackStack() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShopppingItemFragment>(fragmentFactory= fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
        pressBack()

        verify(navController).popBackStack()
    }


    @Test
    fun clickImagePickButton_navigateToImagePickFragment() {
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<AddShopppingItemFragment>(fragmentFactory= fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

        }

        onView(withId(R.id.ivShoppingImage)).perform(click())

        verify(navController).navigate(
            AddShopppingItemFragmentDirections.actionAddShopppingItemFragmentToImagePickFragment()
        )

    }

    @Test
    fun pressBackButton_verifyCurrentImageUrlIsBlank() {
        val navController = mock(NavController::class.java)
        var fakeviewModel = ShoppingViewModel(FakeAndroidShoppingRepository())

        launchFragmentInHiltContainer<AddShopppingItemFragment>(fragmentFactory= fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            fakeviewModel = this.viewModel
        }
        pressBack()

        val currentImageUrl = fakeviewModel.currentImageUrl.getOrAwaitValue()

        assertThat(currentImageUrl).isEmpty()

    }
}