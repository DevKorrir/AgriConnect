package dev.korryr.agrimarket.ui.features.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import dev.korryr.agrimarket.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun AgribuzWelcomeScreen(
    onGetStartedClick: () -> Unit = {}
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Animated floating effect for illustrations
    val infiniteTransition = rememberInfiniteTransition(label = "floatingAnimation")
    val floatAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    // Sun rotation animation
    val sunRotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )

    // Scale animation for Get Started button
    val buttonScale by animateFloatAsState(
        targetValue = if (showButton) 1f else 0.8f,
        animationSpec = tween(500),
        label = "buttonScale"
    )

    // Colorful gradients
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF87CEEB), // Sky blue
            Color(0xFF9EE09E)  // Light green
        )
    )

    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            Color(0xFF4CAF50),  // Green
            Color(0xFFFF9800)   // Orange
        )
    )

    val rainbowTextGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF9800),  // Orange
            Color(0xFF4CAF50),  // Green
            Color(0xFF2196F3),  // Blue
            Color(0xFF9C27B0)   // Purple
        )
    )

    // Start animations after a short delay
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        delay(800)
        showButton = true
    }

    Scaffold() { paddingValues ->


        // Main layout
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            // Sun in the corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .size(60.dp)
                    .rotate(sunRotation.value)
                    .background(Color(0xFFFFC107), CircleShape)
                    .shadow(8.dp, CircleShape)
            )

            // Clouds floating
            Image(
                painter = painterResource(id = R.drawable.clouds), // Replace with your cloud image
                contentDescription = "Clouds",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .fillMaxWidth(0.8f)
                    .offset(y = (-10).dp + (floatAnim.value * 20).dp)
                    .alpha(0.9f),
                contentScale = ContentScale.FillWidth
            )

            // Main content column
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(120.dp))

                // Logo and welcome text with animations
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(initialOffsetY = { -50 }, animationSpec = tween(1000))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App logo with shadow and scale effect
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                //.shadow(16.dp, CircleShape),
                                .scale(1f + (floatAnim.value * 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.agribuzna), // Your app logo
                                contentDescription = "Agribuz Logo",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(80.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Welcome heading with gradient
                        Text(
                            text = "Welcome to Agribuz!",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                brush = rainbowTextGradient
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description text
                        Text(
                            text = "Your friendly farming companion for better harvests and sustainable agriculture",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }

                // Cute farm illustration with floating animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated farm scene
                    Image(
                        painter = painterResource(id = R.drawable.farm_scene), // Replace with your farm illustration
                        contentDescription = "Farm Illustration",
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 16.dp)
                            //.offset(y = (floatAnim.value * 15).dp)
                            .graphicsLayer {
                                shadowElevation = 8f
                                shape = RoundedCornerShape(16.dp)
                                clip = true
                            },
                        contentScale = ContentScale.FillWidth
                    )

//                // Little animated elements (like birds or butterflies)
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 40.dp)
//                        .offset(
//                            x = (floatAnim.value * 30).dp,
//                            y = (floatAnim.value * -20).dp
//                        ),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.butterfly), // Replace with butterfly image
//                        contentDescription = "Butterfly",
//                        modifier = Modifier.size(30.dp)
//                    )
//
//                    Spacer(modifier = Modifier.width(40.dp))
//
//                    Image(
//                        painter = painterResource(id = R.drawable.bird), // Replace with bird image
//                        contentDescription = "Bird",
//                        modifier = Modifier
//                            .size(28.dp)
//                            .offset(y = (floatAnim.value * -10).dp)
//                    )
//                }
                }

                // Colorful Get Started button with animation
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800))
                ) {
                    Button(
                        onClick = onGetStartedClick,
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(bottom = 48.dp, top = 16.dp)
                            .height(60.dp)
                            .fillMaxWidth(0.7f)
                            .scale(buttonScale)
                            .background(
                                brush = buttonGradient,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Get Started",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                painter = painterResource(id = R.drawable.right_arrow),
                                contentDescription = "Arrow",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Bottom decoration - cute wavy grass or plants
                Image(
                    painter = painterResource(id = R.drawable.grass), // Replace with grass/plants image
                    contentDescription = "Grass",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
