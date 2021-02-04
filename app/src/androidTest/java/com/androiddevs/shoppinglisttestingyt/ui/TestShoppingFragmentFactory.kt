package com.androiddevs.shoppinglisttestingyt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.androiddevs.shoppinglisttestingyt.adapter.ImageAdapter
import com.androiddevs.shoppinglisttestingyt.adapter.ShoppingItemAdapter
import com.androiddevs.shoppinglisttestingyt.repo.FakeAndroidShoppingRepository
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class TestShoppingFragmentFactory @Inject constructor(
    private val imageAdapter: ImageAdapter,
    private val glide: RequestManager,
    private val shoppingItemAdapter: ShoppingItemAdapter, // use trailing commas so this line remains unchanged to keep version control history clear.
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ImagePickFragment::class.java.name -> ImagePickFragment(imageAdapter)
            AddShopppingItemFragment::class.java.name -> AddShopppingItemFragment(glide)
            ShoppingFragment::class.java.name -> ShoppingFragment(shoppingItemAdapter,
            ShoppingViewModel(FakeAndroidShoppingRepository())
            )
            else -> super.instantiate(classLoader, className)
        }

    }


}