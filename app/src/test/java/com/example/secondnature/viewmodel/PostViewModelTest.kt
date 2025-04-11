import android.os.Looper
import androidx.lifecycle.Observer
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.model.User
import com.example.secondnature.data.repository.PostRepository
import com.example.secondnature.data.repository.UserRepository
import com.example.secondnature.viewmodel.PostViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.times


@RunWith(RobolectricTestRunner::class)  // Use RobolectricTestRunner for Android-specific tests
@Config(sdk = [28])  // Specify the Android SDK version you want to simulate (e.g., SDK 28)
class PostViewModelTest {

    private lateinit var postViewModel: PostViewModel
    private val postRepository: PostRepository = mock() // Mock the PostRepository
    private val userRepository: UserRepository = mock() // Mock the UserRepository

    @Before
    fun setup() {
        // Initialize the ViewModel with mocked repositories
        postViewModel = PostViewModel(postRepository, userRepository)
    }


    @Test
    fun testGetPost() = runBlocking {
        // Mock the observer for LiveData
        val postObserver: Observer<Post?> = mock()
        postViewModel.post.observeForever(postObserver)

        // Prepare mock data for getPost()
        val mockPost = Post(postId = "123", username = "mockUser")
        whenever(postRepository.getPost("123")).thenReturn(Result.success(mockPost))

        // Call the getPost function within a coroutine context
        postViewModel.getPost("123")

        // Verify that the observer's onChanged was called with the mock post
        verify(postObserver).onChanged(mockPost)

        // Additional checks to verify the LiveData state (if needed)
        assert(postViewModel.post.value == mockPost)
    }


}
