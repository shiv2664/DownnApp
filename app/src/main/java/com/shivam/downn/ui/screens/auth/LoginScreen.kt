package com.shivam.downn.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.R
import com.shivam.downn.data.models.AuthRequest
import com.shivam.downn.data.models.AuthResponse
import com.shivam.downn.data.models.RegisterRequest
import com.shivam.downn.data.network.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is NetworkResult.Success) {
            onLoginSuccess()
        }
    }

    LoginContent(
        authState,
        onLoginClick = { email, password, isAdmin ->
            val role = if (isAdmin) "ADMIN" else null // Send null if not admin to avoid unnecessary updates
            viewModel.login(AuthRequest(email, password, role))
        },
        onRegisterClick = { name, phone, email, password, isAdmin ->
            val role = if (isAdmin) "ADMIN" else "USER"
            viewModel.register(RegisterRequest(email, password, name, phone, role = role))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    authState: NetworkResult<AuthResponse?>?,
    onLoginClick: (String, String, Boolean) -> Unit,
    onRegisterClick: (String, String, String, String, Boolean) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) } // Admin Checkbox State
    var validationError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Decorative Gradients
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFA855F7).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 150.dp, y = 150.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFEC4899).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899))), CircleShape),
                color = Color(0xFF1E293B),
                shadowElevation = 10.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isRegisterMode) "Create Account" else "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = if (isRegisterMode) "Join Downn and start exploring." else "Sign in to continue your journey.",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(targetState = isRegisterMode, label = "form") { isRegister ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { if (it.length <= 50) name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFFA855F7),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { if (it.length <= 15 && it.all { char -> char.isDigit() }) phoneNumber = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFFA855F7),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 100) email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFFA855F7),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { if (it.length <= 50) password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff

                            val description = if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFFA855F7),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp).clickable { isAdmin = !isAdmin }
                    ) {
                        Checkbox(
                            checked = isAdmin,
                            onCheckedChange = { isAdmin = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFA855F7),
                                uncheckedColor = Color(0xFF94A3B8),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = if (isRegisterMode) "Register as Admin" else "Login as Admin",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))



            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (authState is NetworkResult.Error) {
                authState.message?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            Button(
                onClick = {
                    validationError = null
                    
                    // Validation Logic
                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    
                    if (isRegisterMode) {
                        if (name.isBlank() || phoneNumber.isBlank() || email.isBlank() || password.isBlank()) {
                            validationError = "All fields are required"
                            return@Button
                        }
                    } else {
                         if (email.isBlank() || password.isBlank()) {
                            validationError = "Email and password are required"
                            return@Button
                        }
                    }
                    
                    if (!isEmailValid) {
                        validationError = "Invalid email address"
                        return@Button
                    }
                    
                    if (password.length < 6) {
                        validationError = "Password must be at least 6 characters"
                        return@Button
                    }

                    if (isRegisterMode) {
                        onRegisterClick(name, phoneNumber, email, password, isAdmin)
                    } else {
                        onLoginClick(email, password, isAdmin)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA855F7)
                ),
                enabled = authState !is NetworkResult.Loading
            ) {
                if (authState is NetworkResult.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isRegisterMode) "Create Account" else "Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isRegisterMode) "Already have an account? " else "Don't have an account? ",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
                Text(
                    text = if (isRegisterMode) "Sign In" else "Sign Up",
                    color = Color(0xFFA855F7),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isRegisterMode = !isRegisterMode }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Google Sign-In (Keep existing design pattern)
            Surface(
                onClick = { /* Google Sign In logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("G", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginContent(
        authState = null,
        onLoginClick = { _, _, _ -> },
        onRegisterClick = { _, _, _, _, _ -> }
    )
}




