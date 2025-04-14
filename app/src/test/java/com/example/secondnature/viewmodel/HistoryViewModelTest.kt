import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.repository.PostRepository
import com.example.secondnature.viewmodel.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.mockito.kotlin.atLeastOnce

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HistoryViewModelTest {

    // Rule for testing LiveData
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var historyViewModel: HistoryViewModel
    private val postRepository: PostRepository = mock()
    private val testDispatcher = TestCoroutineDispatcher()

    // Mock observers for LiveData
    private val postsObserver: Observer<List<Post>> = mock()
    private val loadingObserver: Observer<Boolean> = mock()
    private val errorObserver: Observer<String?> = mock()

    @Before
    fun setup() {
        // Set the main dispatcher for testing
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel with mocked repository
        historyViewModel = HistoryViewModel(postRepository)

        // Observe the LiveData
        historyViewModel.posts.observeForever(postsObserver)
        historyViewModel.isLoading.observeForever(loadingObserver)
        historyViewModel.error.observeForever(errorObserver)
    }

    @After
    fun tearDown() {
        // Remove observers to prevent leaks
        historyViewModel.posts.removeObserver(postsObserver)
        historyViewModel.isLoading.removeObserver(loadingObserver)
        historyViewModel.error.removeObserver(errorObserver)

        // Reset the main dispatcher
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `init calls fetchUserPosts and updates state correctly`() = runBlocking {
        // Reset observers first to clear any previous interactions
        org.mockito.Mockito.reset(loadingObserver, postsObserver, errorObserver)

        // Prepare mock data
        val mockPosts = listOf(
            Post(postId = "123", username = "user1"),
            Post(postId = "456", username = "user1")
        )

        // Configure the mock repository to return success
        whenever(postRepository.getUserPosts()).thenReturn(Result.success(mockPosts))

        // Create a new ViewModel instance which will call init
        val newViewModel = HistoryViewModel(postRepository)
        newViewModel.posts.observeForever(postsObserver)
        newViewModel.isLoading.observeForever(loadingObserver)
        newViewModel.error.observeForever(errorObserver)

        // Verify the final states
        verify(postsObserver).onChanged(mockPosts)
        verify(errorObserver).onChanged(null)
        verify(loadingObserver).onChanged(false)

        // Clean up
        newViewModel.posts.removeObserver(postsObserver)
        newViewModel.isLoading.removeObserver(loadingObserver)
        newViewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `fetchUserPosts updates state correctly`() = runBlocking {
        // Reset observers and repository first
        org.mockito.Mockito.reset(loadingObserver, postsObserver, errorObserver, postRepository)

        // Prepare mock data
        val mockPosts = listOf(
            Post(postId = "123", username = "user1"),
            Post(postId = "456", username = "user1")
        )

        // Configure the mock repository to return success
        whenever(postRepository.getUserPosts()).thenReturn(Result.success(mockPosts))

        // Explicitly call fetchUserPosts
        historyViewModel.fetchUserPosts()

        // Verify loading states with ordering
        val inOrder = org.mockito.Mockito.inOrder(loadingObserver)
        inOrder.verify(loadingObserver).onChanged(true)
        inOrder.verify(loadingObserver).onChanged(false)

        // Verify posts were updated
        verify(postsObserver).onChanged(mockPosts)

        // Verify error is null
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun `fetchUserPosts should handle failure`() = runBlocking {
        // Reset observers and repository first
        org.mockito.Mockito.reset(loadingObserver, postsObserver, errorObserver, postRepository)

        // Configure the mock repository to return failure
        val errorMessage = "Network error"
        whenever(postRepository.getUserPosts()).thenReturn(Result.failure(RuntimeException(errorMessage)))

        // Explicitly call fetchUserPosts
        historyViewModel.fetchUserPosts()

        // Use inOrder with atLeastOnce to verify the loading state sequence
        val inOrder = org.mockito.Mockito.inOrder(loadingObserver)
        inOrder.verify(loadingObserver).onChanged(true)
        inOrder.verify(loadingObserver, atLeastOnce()).onChanged(false)

        // Verify empty list is set
        verify(postsObserver).onChanged(emptyList())

        // Verify error message is set
        verify(errorObserver).onChanged(errorMessage)
    }

    @Test
    fun `init should call fetchUserPosts`() = runBlocking {
        // Since fetchUserPosts is called in init, we just need to verify it was called once
        // when we create a new instance

        // Prepare mock data
        val mockPosts = listOf(Post(postId = "123", username = "user1"))
        whenever(postRepository.getUserPosts()).thenReturn(Result.success(mockPosts))

        // Create a new ViewModel instance (which will call init)
        val newViewModel = HistoryViewModel(postRepository)
        newViewModel.posts.observeForever(postsObserver)

        // Verify the observer received the mock posts
        verify(postsObserver).onChanged(mockPosts)
    }
}