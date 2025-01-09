package com.dreamb.translateapp

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.launch

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
                Toast.makeText(context,"음성 기능 사용 실패",Toast.LENGTH_SHORT).show()
            }
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    // Dispose TTS when no longer needed
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    MakeTranslator(sourceLanguage, selectedLanguageCode.value, text, outputText)
    ScanLanguage(text, targetLang, sourceLanguage, context)
    ModalNavigationDrawer(
        modifier = Modifier
            .background(color = colorResource(R.color.upperNavBackGround))
            .fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MenuScreen(navController,scope,drawerState)
            }
        },
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.dropdownBackGround))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) {
                UpperNavBar(
                    text,
                    targetLang,
                    sourceLanguage,
                    context,
                    outputText = outputText.value,
                    selectedLanguage,
                    selectedLanguageCode,
                    drawerState,
                    scope
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
                    .padding(10.dp)
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = colorResource(R.color.textAndBtnColor),
                        focusedContainerColor = colorResource(R.color.textBackGround),
                        unfocusedContainerColor = colorResource(R.color.textBackGround)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        color = colorResource(R.color.textAndBtnColor)
                    ),
                    label = {
                        Text(
                            "번역할 내용을 입력하세요.",
                            color = colorResource(R.color.textAndBtnColor)
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(R.color.dropdownBackGround))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
                    .padding(5.dp)
                    .background(color = colorResource(R.color.dropdownBackGround))
            ) {
                OutputTextAndTtsBtn(tts,outputText.value, selectedLanguage.value, context)
            }
            Spacer(modifier = Modifier.height(3.dp))
            SpeechToText(modifier = Modifier.weight(1f)) { spokentext ->
                text = spokentext
            }
            Spacer(modifier = Modifier.height(10.dp))
            ButtonMoveToImage(navController, Modifier.weight(1f))
            Spacer(modifier = Modifier.height(10.dp))
        }
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
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        BtnContent(R.drawable.photo_svgrepo_com, "이미지 번역", "이미지 번역", 40)
    }
}
