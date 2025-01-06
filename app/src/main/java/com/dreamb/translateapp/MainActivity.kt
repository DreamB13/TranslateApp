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
import com.google.mlkit.nl.translate.TranslateLanguage


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
fun TransLanguageMenu(
    selectedLanguage: String,
    onLanguageChange: (String, String) -> Unit // 언어 이름과 TranslateLanguage 코드 전달
) {
    var expandedLang by remember { mutableStateOf(false) }

    TextButton(
        onClick = { expandedLang = !expandedLang },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            selectedLanguage,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
    DropdownMenu(
        expanded = expandedLang,
        onDismissRequest = { expandedLang = false }
    ) {
        val languageMap = mapOf(
            "한국어" to TranslateLanguage.KOREAN,
            "영어" to TranslateLanguage.ENGLISH,
            "일본어" to TranslateLanguage.JAPANESE,
            "중국어" to TranslateLanguage.CHINESE,
            "프랑스어" to TranslateLanguage.FRENCH,
            "독일어" to TranslateLanguage.GERMAN
        )
        languageMap.forEach { (languageName, languageCode) ->
            DropdownMenuItem(
                text = { Text(languageName) },
                onClick = {
                    onLanguageChange(languageName, languageCode) // 선택된 언어와 코드 전달
                    expandedLang = false
                }
            )
        }
    }
}

