import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.example.secondnature.ui.components.PostItem
import com.google.firebase.Timestamp
import org.junit.Rule
import org.junit.Test

class PostItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun postItem_displaysStoreName() {
        composeTestRule.setContent {
            PostItem(
                imageURL = "https://example.com/image.jpg",
                storeRating = 4,
                priceRating = 3,
                storeName = "Test Store",
                username = "exampleUser",
                date = Timestamp.now()
            )
        }

        composeTestRule.onNodeWithText("Test Store")
            .assertIsDisplayed()
    }
}
