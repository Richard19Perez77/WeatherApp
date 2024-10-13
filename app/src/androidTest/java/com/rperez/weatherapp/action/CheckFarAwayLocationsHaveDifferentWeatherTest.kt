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
class CheckFarAwayLocationsHaveDifferentWeatherTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testChangeSearch() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading...").fetchSemanticsNodes().isEmpty()
        }

        var descriptionString = ""
        composeTestRule.onNodeWithTag("description_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                descriptionString = text ?: ""
            }

        var tempString = ""
        composeTestRule.onNodeWithTag("temp_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                tempString = text ?: ""
            }

        composeTestRule.onNodeWithTag("search_text").performTextClearance()
        composeTestRule.onNodeWithTag("search_text").performTextInput("Paris")
        composeTestRule.onNodeWithTag("search_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading...").fetchSemanticsNodes().isEmpty()
        }

        var tempStringCurr = ""
        composeTestRule.onNodeWithTag("temp_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                tempStringCurr = text ?: ""
            }

        var descriptionStringCurr = ""
        composeTestRule.onNodeWithTag("description_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                descriptionStringCurr = text ?: ""
            }

        var oldPair = Pair(descriptionStringCurr, descriptionStringCurr)
        var newPair = Pair(tempStringCurr, tempString)
        assertNotEquals(oldPair, newPair)
    }
}