package com.androiddevs.shoppinglisttestingyt.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Constants
import com.androiddevs.shoppinglisttestingyt.other.Event
import com.androiddevs.shoppinglisttestingyt.other.Resource
import com.androiddevs.shoppinglisttestingyt.respositories.ShoppingRespository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

/**
 * https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-survey#3
 * for testing
 */

class ShoppingViewModel @ViewModelInject constructor(

    private val repository: ShoppingRespository //interface

) : ViewModel() {

    val shoppingItems = repository.observeAllShoppingItems()

    val totalPrice = repository.observeTotalPrice()

    private val _images = MutableLiveData<Event<Resource<ImageResponse>>>() //call the constructor
    val images: LiveData<Event<Resource<ImageResponse>>> = _images


    private val _currentImageUrl = MutableLiveData<String>() //call the constructor
    val currentImageUrl: LiveData<String> = _currentImageUrl

    private val _insertShopingItemStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShopingItemStatus: LiveData<Event<Resource<ShoppingItem>>> = _insertShopingItemStatus

    fun setCurrentImageUrl(url: String) = _currentImageUrl.postValue(url)

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun insertShoppingItem(name: String, amountString: String, priceString: String) {

        if (name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()) {
            _insertShopingItemStatus.postValue(
                Event(
                    Resource.error(
                        "The fields must not be empty",
                        null
                    )
                )
            )
            return
        }
        if (name.length > Constants.MAX_NAME_LENGTH) {
            _insertShopingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Name of item" +
                                " must not exceed ${Constants.MAX_NAME_LENGTH} characters", null
                    )
                )
            )
            return
        }
        if (priceString.length > Constants.MAX_PRICE_LENGTH) {
            _insertShopingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Price of the item" +
                                " must not exceed ${Constants.MAX_PRICE_LENGTH} characters", null
                    )
                )
            )
            return
        }

        val amount = try {
            amountString.toInt()
        } catch (e: Exception) {
            _insertShopingItemStatus.postValue(
                Event(
                    Resource.error(
                        "Please enter valid amount",
                        null
                    )
                )
            )
            return
        }

        val shoppingItem =
            ShoppingItem(name, amount, priceString.toFloat(), _currentImageUrl.value ?: "")
        insertShoppingItemIntoDb(shoppingItem)

        setCurrentImageUrl("") // to not to show previous image as we pop the backstack to the shopping list.

        _insertShopingItemStatus.postValue(Event(Resource.success(shoppingItem)))

    }

    fun searchForImage(imageQuery: String) {

        if(imageQuery.isEmpty())
        {
            return
        }

        _images.value = Event(Resource.loading(null)) // actually setValue()
        /**
         * Since we are using Resource, we should get the loading status also before we get the success status. This is important for testing,
         * that we dont get the success status here directly which is why we use setValue/value.
         * TODO : Read more about setValue and PostValue differences.
         */

        /**
         * Why images.value and not images.postValue ?
         *
         * _images.value -> Will always notify all observers.
         *
         * _images.postValue() -> several times in short time frame, then only last value would be dispatched.
         *
         * See : https://developer.android.com/reference/androidx/lifecycle/LiveData#postValue(T)
         *
         */

        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = Event(response)
        }
    }
}