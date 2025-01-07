package com.dreamb.translateapp

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.mlkit.nl.translate.TranslateLanguage

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val targetLang = remember { mutableStateOf("언어감지") }
    var text by remember { mutableStateOf("") }
    val outputText = remember { mutableStateOf("") }
    val sourceLanguage = remember { mutableStateOf("") }
    val selectedLanguage = remember { mutableStateOf("한국어") }
    val selectedLanguageCode = remember { mutableStateOf(TranslateLanguage.KOREAN) }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
            }
        }
    }

    // Dispose TTS when no longer needed
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    MakeTranslator(sourceLanguage, selectedLanguageCode.value, text, outputText)
    ScanLanguage(text, targetLang, sourceLanguage, context)

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xffdddddd))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(color = Color(0xffdddddd))
        ) {
            UpperNavBar(
                text,
                targetLang,
                sourceLanguage,
                context,
                outputText = outputText.value,
                selectedLanguage,
                selectedLanguageCode,
                navController
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
                .background(color = Color.White)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.LightGray)
                .fillMaxWidth()
                .weight(4f)
                .background(color = Color.White)
        ) {
            OutputTextAndTtsBtn(tts, outputText.value, selectedLanguage.value, context)
        }
        SpeechToText(modifier = Modifier.weight(1f)) { spokentext ->
            text = spokentext
        }
        ButtonMoveToImage(navController, Modifier.weight(1f))
    }
}

@Composable
fun ButtonMoveToImage(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = {
            navController.navigate("imageTranslate") {
                popUpTo("MainScreen") { inclusive = false } // 이전 스택 유지하지 않음
                launchSingleTop = true // 중복된 화면 방지
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff8888ff)),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
    ) {
        BtnContent(R.drawable.photo_svgrepo_com, "이미지 번역", "이미지 번역", 40)
    }
}
