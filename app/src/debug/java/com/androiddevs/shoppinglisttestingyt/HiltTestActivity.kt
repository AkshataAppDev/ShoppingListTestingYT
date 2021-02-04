package com.androiddevs.shoppinglisttestingyt

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint


/**
 * When we test fragments, we use fragmebt scenarios where we launch fragments into empty sctivities.
 * While we can annotate fragments as @AndroidEntryPoint when using DI with Hilt, we also need to annotate the empty  activity with @AndroidEntryPoint.
 * Fragment scnerio launches empty activity but this annotation is missing for the empty activities.So it will crash.
 */

//Activity to which we will attach Fragments to be tested.
//Since this activity will be used only for testing, we add this to manifest file in the debug and not prod.
@AndroidEntryPoint
class HiltTestActivity: AppCompatActivity()