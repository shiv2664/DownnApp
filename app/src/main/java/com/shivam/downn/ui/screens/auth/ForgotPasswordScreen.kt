package com.shivam.downn.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shivam.downn.data.network.NetworkResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordRoute(
    onNavigateBack: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(forgotPasswordState) {
        if (forgotPasswordState is NetworkResult.Success) {
            Toast.makeText(context, "Reset email sent!", Toast.LENGTH_SHORT).show()
            onNavigateToResetPassword()
            viewModel.resetForgotPasswordState()
        } else if (forgotPasswordState is NetworkResult.Error) {
             Toast.makeText(context, forgotPasswordState?.message ?: "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    ForgotPasswordContent(
        forgotPasswordState = forgotPasswordState,
        onForgotPassword = { email -> viewModel.forgotPassword(email) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordContent(
    forgotPasswordState: NetworkResult<String?>?,
    onForgotPassword: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your email to receive a password reset link.",
                color = Color(0xFFCBD5E1),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFFA855F7),
                    unfocusedLabelColor = Color(0xFF94A3B8)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onForgotPassword(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA855F7)
                ),
                enabled = email.isNotBlank() && forgotPasswordState !is NetworkResult.Loading
            ) {
                 if (forgotPasswordState is NetworkResult.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Reset Link", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordContent(
        forgotPasswordState = null,
        onForgotPassword = {},
        onNavigateBack = {}
    )
}
