package com.androiddevs.shoppinglisttestingyt.ui

import android.app.VoiceInteractor
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androiddevs.shoppinglisttestingyt.R
import com.androiddevs.shoppinglisttestingyt.adapter.ImageAdapter
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_shopping_item.*
import kotlinx.android.synthetic.main.item_image.*
import kotlinx.android.synthetic.main.item_image.ivShoppingImage
import javax.inject.Inject

class AddShopppingItemFragment @Inject constructor(
    private val glide: RequestManager
) : Fragment(R.layout.fragment_add_shopping_item) {
    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
        subscribeToObservers()

        btnAddShoppingItem.setOnClickListener{
            viewModel.insertShoppingItem(
                etShoppingItemName.text.toString(),
                etShoppingItemAmount.text.toString(),
                etShoppingItemPrice.text.toString()

            )
        }

        ivShoppingImage.setOnClickListener {
            findNavController().navigate(
                AddShopppingItemFragmentDirections.actionAddShopppingItemFragmentToImagePickFragment()
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setCurrentImageUrl("")
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun subscribeToObservers() {
        viewModel.currentImageUrl.observe(viewLifecycleOwner, Observer {
            glide.load(it).into(ivShoppingImage)
        })

        viewModel.insertShopingItemStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        Snackbar.make(
                            requireActivity().rootLayout,
                            "Added ShopppingItem",
                            Snackbar.LENGTH_LONG
                        ).show()
                        findNavController().popBackStack()
                    }
                    Status.ERROR -> {
                        Snackbar.make(
                            requireActivity().rootLayout,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        )
                            .show()

                    }
                    Status.LOADING -> {
                        /* NO OP */
                    }
                }
            }
        })
    }
}