package com.androiddevs.shoppinglisttestingyt.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.shoppinglisttestingyt.R
import com.androiddevs.shoppinglisttestingyt.adapter.ShoppingItemAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_shopping.*
import javax.inject.Inject

class ShoppingFragment @Inject constructor(
    val shoppingItemAdapter: ShoppingItemAdapter,
    var viewModel: ShoppingViewModel? = null
) : Fragment(R.layout.fragment_shopping) {


    /**
     * Problem : When we want to test, we want to use viewmodeltest and fake repository.
     * Once we call subscribeToObservers, fragment will subscribe to the observers from the viewmodel.
     *  viewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
     *  So in the above line it will be the real viewmodel through which fragment will subscribe to the observers and not the testviewmodel.
     *
     *  So even if we set viewModel = testViewModel  like in AddShoppingfragmentTest, the change will only happen
     *  after all lifecycle functions are called.
     *  So after we already  observed on viewmodel live data from our real viewmodel and not fake viewmodel.
     *  This will not work.
     *
     *  It works for AddShoppingFragment, because it is a user triggered event when we added shopping item. Not immediately triggered in onviewCreated.
     *  so we must have a way to pass viewmodel in the constructor of the fragment.
     *  Once we construct the fragment, we want to tell at that time of construction we want to have fake viewmodel.
     *
     *  Since we use fragment factory, we need to create custom fragment factory in which we can pass the viewmodel in the constructor.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModel
            ?: ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java) // in our tests, we pass as parameter with a fake viewmodel

        subscribeToObservers()
        setupRecyclerView()

        fabAddShoppingItem.setOnClickListener {
            findNavController().navigate(ShoppingFragmentDirections.actionShoppingFragmentToAddShopppingItemFragment())
        }

    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, LEFT or RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.layoutPosition
            val item = shoppingItemAdapter.shoppingItems[pos]

            viewModel?.deleteShoppingItem(item)

            Snackbar.make(requireView(), "Successfully deleted item", Snackbar.LENGTH_LONG).apply {
                setAction("Undo")
                {
                    viewModel?.insertShoppingItemIntoDb(item)
                }
                show()
            }
        }

    }

    private fun subscribeToObservers() {

        viewModel?.shoppingItems?.observe(viewLifecycleOwner, Observer {
            shoppingItemAdapter.shoppingItems = it
        })

        viewModel?.totalPrice?.observe(viewLifecycleOwner, Observer {
            val price = it ?: 0f
            val priceText = "Total Price : $price£"
            tvShoppingItemPrice.text = priceText
        })

    }

    private fun setupRecyclerView() {
        rvShoppingItems.apply {
            adapter = shoppingItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)

        }
    }
}