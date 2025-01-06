package com.dreamb.translateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dreamb.translateapp.ui.theme.TranslateAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(Modifier.safeDrawingPadding()) {
                TranslateAppTheme {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("imageTranslate") {
            ImageTrans()
        }
    }
}



@Composable
fun TransLanguageMenu() {
    var expandedLang by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("한국어") }
    TextButton(
        onClick = { expandedLang = !expandedLang },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            language,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
    DropdownMenu(
        expanded = expandedLang,
        onDismissRequest = { expandedLang = false }
    ) {
        DropdownMenuItem(
            text = { Text("한국어") },
            onClick = {
                language = "한국어"
                expandedLang = false
            }
        )
        DropdownMenuItem(
            text = { Text("영어") },
            onClick = {
                language = "영어"
                expandedLang = false
            }
        )
        DropdownMenuItem(
            text = { Text("일본어") },
            onClick = {
                language = "일본어"
                expandedLang = false
            }
        )
        DropdownMenuItem(
            text = { Text("중국어") },
            onClick = {
                language = "중국어"
                expandedLang = false
            }
        )
        DropdownMenuItem(
            text = { Text("프랑스어") },
            onClick = {
                language = "프랑스어"
                expandedLang = false
            }
        )
        DropdownMenuItem(
            text = { Text("독일어") },
            onClick = {
                language = "독일어"
                expandedLang = false
            }
        )
    }
}

