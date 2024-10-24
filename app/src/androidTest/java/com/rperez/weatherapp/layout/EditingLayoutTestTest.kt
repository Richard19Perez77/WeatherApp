package com.rperez.weatherapp.layout

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rperez.weatherapp.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class EditingLayoutTestTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testEnterNewSearchText() {
        composeTestRule.onNodeWithTag("search_text")
            .performTextClearance()

        composeTestRule.onNodeWithTag("search_text")
            .performTextInput("New York")

        composeTestRule.onNodeWithTag("search_text")
            .assert(hasText("New York"))
    }

    // test temperature zoom button is clickable to new screen
    @Test
    fun testTempZoomButtonClickedGoesToNewScreen() {
        composeTestRule.onNodeWithTag("zoom_button")
            .performClick()

        composeTestRule.onNodeWithTag("temp_zoom_text", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun testTempZoomButtonClickedGoesToNewScreenShowingOnlyTempFromCall() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        // store temp
        var tempString = ""
        composeTestRule.onNodeWithTag("temp_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text =
                    semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                tempString = text ?: ""
            }

        composeTestRule.onNodeWithTag("zoom_button")
            .performClick()

        tempString = tempString.filter { it.isDigit() || it == '.'}.plus("°C")
        composeTestRule.onNodeWithTag("temp_zoom_text")
            .assertIsDisplayed()
            .assert(hasText(tempString))

        composeTestRule.onAllNodesWithTag("temp_zoom_text").assertCountEquals(1)
    }

    // test can go to zoom screen and back
    @Test
    fun testTempZoomButtonClickedGoesToNewScreenBackPressWorks() {
        composeTestRule.onNodeWithTag("zoom_button")
            .performClick()

        composeTestRule.onNodeWithTag("temp_zoom_text", useUnmergedTree = true)
            .assertIsDisplayed()

        runBlocking {
            withContext(Dispatchers.Main) {
                composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
            }
        }

        composeTestRule.onNodeWithTag("zoom_button")
            .assertIsDisplayed()
    }
}
