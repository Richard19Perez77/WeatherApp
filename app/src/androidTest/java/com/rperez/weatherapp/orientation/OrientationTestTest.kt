package com.rperez.weatherapp.orientation

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rperez.weatherapp.MainActivity
import org.junit.Assert.assertEquals
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
class OrientationTestTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Test search city persists over orientation change
     */
    @Test
    fun testCityNamePersistsOnOrientationChange() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        var cityString = ""
        composeTestRule.onNodeWithTag("search_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                cityString = text ?: ""
            }

        composeTestRule.onNodeWithTag("search_text").performTextClearance()
        composeTestRule.onNodeWithTag("search_text").performTextInput("Paris")

        var updatedCityString = ""
        composeTestRule.onNodeWithTag("search_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                updatedCityString = text ?: ""
            }

        assertEquals(updatedCityString, cityString)

        composeTestRule.onNodeWithTag("search_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading…").fetchSemanticsNodes().isEmpty()
        }

        var newCityString = ""
        composeTestRule.onNodeWithTag("search_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text = semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                newCityString = text ?: ""
            }

        assertEquals(newCityString, cityString)
    }
}