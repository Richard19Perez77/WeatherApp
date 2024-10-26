package com.rperez.weatherapp.action

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rperez.weatherapp.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device. Must have internet off before running test.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InternetNotAvailableUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testInternetOffUIUpdated() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithTag("search_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        var customText = ""
        composeTestRule.onNodeWithTag("custom_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text =
                    semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                customText = text ?: ""
            }

        assertTrue(customText.contains("No Internet Connection"))
    }
}