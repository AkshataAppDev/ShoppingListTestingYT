package com.androiddevs.shoppinglisttestingyt

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.experimental.Experimental
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.fragment.navArgs
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.core.internal.deps.guava.base.Preconditions
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
//inline fun compiler replaces code.
//reified means we can access the class information of the T generic type in this function so known at compile time.
inline fun <reified T : Fragment> launchFragmentInHiltContainer(

    fragmentArgs: Bundle? = null,
    themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    crossinline action: T.() -> Unit = {}
) {

    /**
     * Intent to make the activity as a MainActivity
     */
    val mainActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY,
        themeResId
    )

    /**
     * We launch main activity with our mainActivityIntent and get a reference to that activity
     *
     */
    ActivityScenario.launch<HiltTestActivity>(mainActivityIntent).onActivity { activity ->

        // if we have afragment factory we attach it to activity.
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory= it
        }

            // Instantiate the fragment define its argument
            val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
                Preconditions.checkNotNull(T::class.java.classLoader)!!,
                T::class.java.name
            )


            fragment.arguments = fragmentArgs

        // launch the fragment
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()

        // have a references to the fragment object as T
            (fragment as T).action()


        }
}
