package com.rperez.weatherapp.action

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import org.junit.Assert.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.rperez.weatherapp.MainActivity


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InvalidCityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testCitySearchErrorState() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithTag("search_text").performTextClearance()
        composeTestRule.onNodeWithTag("search_text").performTextInput("Tokyo-yoyo-yoyogi")
        composeTestRule.onNodeWithTag("search_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        var size1 = composeTestRule.onAllNodesWithText("No data for city")
            .fetchSemanticsNodes().size

        var size2 = composeTestRule.onAllNodesWithText("Tokyo-yoyo-yoyogi")
            .fetchSemanticsNodes().size

        assertTrue(size1 > 0 && size2 > 0)
    }
}