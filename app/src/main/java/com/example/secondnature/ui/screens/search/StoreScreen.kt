import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.StoreDetailsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.secondnature.ui.components.StarRating
import com.example.secondnature.ui.components.PriceRating
import com.google.common.io.Files.append

@Composable
fun StoreScreen(
    navController: NavController,
    storeDetailsViewModel: StoreDetailsViewModel = viewModel()
) {
    val placeId = navController.currentBackStackEntry?.arguments?.getString("placeId")
    val store by storeDetailsViewModel.store.observeAsState()

    LaunchedEffect(placeId) {
        placeId?.let { id ->
            storeDetailsViewModel.fetchStoreDetails(id)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        store?.let { store ->
            // Store Name
            Text(
                text = store.storeName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Store Ratings and Price Rating Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                store.storeRating?.let { rating ->
                    StarRating(rating = rating.toInt()) // Ensure StarRating is properly defined and accepts Int
                } ?: Text(text = "Not rated", color = Color.Gray)

                store.priceRating?.let { price ->
                    PriceRating(rating = price.toInt()) // Ensure PriceRating is properly defined and accepts Int
                } ?: Text(text = "Not rated", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Store Details (Address, Phone, Website)
            Column(modifier = Modifier.fillMaxWidth()) {
                // Address
                Text(
                    text = "${store.address}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Make phone number clickable
                store.phoneNumber?.let { phoneNumber ->
                    val annotatedString = buildAnnotatedString {
                        pushStringAnnotation(tag = "PHONE", annotation = phoneNumber)
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(phoneNumber)
                        }
                        pop()
                    }

                    Text(
                        text = annotatedString,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                            navController.context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Make website clickable
                store.website?.let { websiteUrl ->
                    val annotatedString = buildAnnotatedString {
                        pushStringAnnotation(tag = "URL", annotation = websiteUrl)
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(websiteUrl)
                        }
                        pop()
                    }

                    Text(
                        text = annotatedString,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                            navController.context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opening Hours
                Text(
                    text = "Opening Hours:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = store.hours?.weekdayText?.joinToString("\n") ?: "N/A",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Post Button
            Button(
                onClick = {
                    placeId?.let {
                        navController.navigate("createPost/$it")
                    } ?: navController.navigate("createPost")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Create Post",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

        } ?: run {
            // Loading state
            Text(
                text = "Loading store details...",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}