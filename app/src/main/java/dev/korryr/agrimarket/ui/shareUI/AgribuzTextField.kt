package dev.korryr.agrimarket.ui.shareUI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import dev.korryr.agrimarket.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.korryr.agrimarket.ui.theme.AgriCustomColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AgribuzTextField - A customized input field for the Agribusiness app
 * 
 * This component is designed to provide a consistent text input experience across
 * the application with support for various states like focus, error, and custom styling
 * based on the app's agricultural theme.
 *
 * @param value Current text input value
 * @param onValueChange Callback when text changes
 * @param label Label text for the field
 * @param hint Placeholder/hint text
 * @param isPassword Whether this field is for password input
 * @param showPassword Initial password visibility state
 * @param leadingIcon Optional icon to show before the text input
 * @param trailingIcon Optional icon to show after the text input (overridden by password visibility toggle for password fields)
 * @param enabled Whether the input is enabled
 * @param readOnly Whether the input is read-only
 * @param error Error message (empty string means no error)
 * @param modifier Modifier for the text field
 * @param showStrength Whether to show password strength indicator (for password fields)
 * @param strength Password strength value from 0-4 (0: none, 1: weak, 2: medium, 3: strong, 4: very strong)
 * @param onDone Callback when done action is triggered
 * @param keyboardOptions KeyboardOptions for customizing keyboard behavior
 * @param keyboardActions KeyboardActions for customizing keyboard action behavior
 * @param maxLines Maximum number of lines for the text field (default: 1 for single line)
 * @param textStyle Custom text style for the input text
 */
@Composable
fun AgribuzTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String = "",
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    error: String = "",
    modifier: Modifier = Modifier,
    showStrength: Boolean = false,
    strength: Int = 0,
    onDone: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions? = null,
    maxLines: Int = 1,
    textStyle: TextStyle = TextStyle.Default,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by rememberSaveable { mutableStateOf(showPassword) }

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scope = rememberCoroutineScope()
    var shouldShake by remember { mutableStateOf(false) }
    val isError = error.isNotEmpty()

    // Define colors based on state (normal, focused, error)
    val iconColor = when {
        isError -> AgriCustomColors.error
        isFocused -> MaterialTheme.colorScheme.primary
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            shouldShake = true
            delay(500)
            shouldShake = false
        }
    }

    // Custom colors for the text field
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        // Border colors
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        errorBorderColor = AgriCustomColors.error,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),

        // Container colors
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        errorContainerColor = AgriCustomColors.error.copy(alpha = 0.05f),
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),

        // Text colors
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        errorTextColor = AgriCustomColors.error,
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

        // Label colors
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor = AgriCustomColors.error,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

        // Icon colors
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLeadingIconColor = AgriCustomColors.error,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorTrailingIconColor = AgriCustomColors.error,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

        // Cursor and selection colors
        cursorColor = MaterialTheme.colorScheme.primary,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            placeholder = if (hint.isNotEmpty()) {
                { Text(text = hint, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) }
            } else null,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            textStyle = textStyle.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            interactionSource = interactionSource,
            colors = textFieldColors,
            modifier = modifier
                .fillMaxWidth()
                .testTag("agribuz_textfield_$label")
                .focusRequester(focusRequester)
                .animateContentSize()
                .then(
                    if (shouldShake) {
                        Modifier.shake()
                    } else Modifier
                ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = when {
                isPassword && passwordVisible -> keyboardOptions.copy(keyboardType = KeyboardType.Text) 
                isPassword -> keyboardOptions.copy(keyboardType = KeyboardType.Password)
                else -> keyboardOptions
            },
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardActions = keyboardActions ?: KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onDone = {
                    keyboardController?.hide()
                    onDone()
                }
            ),
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                            scope.launch {
                                delay(100)
                                focusRequester.requestFocus()
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = if (passwordVisible) {
                                painterResource(id = R.drawable.hide_icon)
                            } else {
                                trailingIcon ?: painterResource(id = R.drawable.hide_icon) // Replace with your show password icon
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = iconColor
                        )
                    }
                }
            } else if (trailingIcon != null) {
                {
                    Image(
                        painter = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            } else null,
            leadingIcon = leadingIcon?.let {
                {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }
        )

        // Error message with animation
        AnimatedVisibility(
            visible = isError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = error,
                color = AgriCustomColors.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        // Password strength indicator
        if (showStrength && isPassword && value.isNotEmpty()) {
            PasswordStrengthIndicator(strength = strength)
        }
    }
}

/**
 * Password strength indicator that shows the relative strength of a password
 * with a visual bar and appropriate agricultural-themed colors
 */
@Composable
fun PasswordStrengthIndicator(strength: Int) {
    val strengthText = when(strength) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Medium"
        3 -> "Strong"
        else -> "Very Strong"
    }
    
    val strengthColor = when(strength) {
        0 -> AgriCustomColors.error
        1 -> AgriCustomColors.warning
        2 -> AgriCustomColors.info
        3 -> AgriCustomColors.crops
        else -> MaterialTheme.colorScheme.primary
    }
    
    val fillPercentage = when(strength) {
        0 -> 0.2f
        1 -> 0.4f
        2 -> 0.6f
        3 -> 0.8f
        else -> 1f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = strengthText,
                style = MaterialTheme.typography.bodySmall,
                color = strengthColor
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillPercentage)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = when(strength) {
                                0 -> listOf(AgriCustomColors.error, AgriCustomColors.error)
                                1 -> listOf(AgriCustomColors.error, AgriCustomColors.warning)
                                2 -> listOf(AgriCustomColors.warning, AgriCustomColors.info)
                                3 -> listOf(AgriCustomColors.info, AgriCustomColors.crops)
                                else -> listOf(AgriCustomColors.crops, MaterialTheme.colorScheme.primary)
                            }
                        )
                    )
            )
        }
    }
}

/**
 * Extension function to provide a shake animation modifier for error states
 */
fun Modifier.shake() = composed {
    val transition = rememberInfiniteTransition(label = "shake")
    val offsetX by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake animation"
    )
    this.offset(x = offsetX.dp)
}