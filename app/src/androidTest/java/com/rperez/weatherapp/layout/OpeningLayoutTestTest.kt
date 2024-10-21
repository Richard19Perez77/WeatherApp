package com.rperez.weatherapp.layout

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rperez.weatherapp.MainActivity
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
class OpeningLayoutTestTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testDefaultEnterCityNameIsShown() {
        composeTestRule.onNodeWithTag("search_label", useUnmergedTree = true)
            .assert(hasText("Enter City Name"))
            .assertIsDisplayed()
    }

    @Test
    fun testSearchButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("search_button")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testSearchCityButtonTextIsDisplayed() {
        composeTestRule.onNodeWithTag("search_city_button_text", useUnmergedTree = true)
            .assertIsDisplayed()
            .assert(hasText("Search City Weather"))
    }

    @Test
    fun testSearchLocalButtonTextIsDisplayed() {
        composeTestRule.onNodeWithTag("get_local_button_text", useUnmergedTree = true)
            .assertIsDisplayed()
            .assert(hasText("Get Your Local Weather"))
    }

    @Test
    fun testDefaultTokyoTempAppears() {
        // wait for loading to end
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading...").fetchSemanticsNodes().isEmpty()
        }

        var tempString = ""
        composeTestRule.onNodeWithTag("temp_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text =
                    semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                tempString = text ?: ""
            }

        assert(tempString.isNotEmpty())
    }

    @Test
    fun testDefaultTokyoDescAppears() {
        // wait for loading to end
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading...").fetchSemanticsNodes().isEmpty()
        }

        var descriptionString = ""
        composeTestRule.onNodeWithTag("description_text")
            .assertExists()
            .assertIsDisplayed()
            .fetchSemanticsNode().let { semanticsNode ->
                val text =
                    semanticsNode.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text
                descriptionString = text ?: ""
            }

        assert(descriptionString.isNotEmpty())
    }

    // test tokyo icon works
    @Test
    fun testDefaultWeatherIconPopulates() {
        // wait for loading to end
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Loading...").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithTag("icon_image")
            .assertExists()
            .assertIsDisplayed()
    }

    // test temperature zoom button shows
    @Test
    fun testTempZoomButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("zoom_button")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    // test temperature zoom button shows
    @Test
    fun testTempZoomButtonTextIsDisplayed() {
        composeTestRule.onNodeWithTag("zoom_text", useUnmergedTree = true)
            .assertIsDisplayed()
            .assert(hasText("Temperature Zoom"))
    }
}
