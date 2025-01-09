package com.dreamb.translateapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil3.Uri
import com.dreamb.translateapp.model.AppDatabase
import com.dreamb.translateapp.model.MyDb
import com.dreamb.translateapp.model.Sentence
import com.dreamb.translateapp.ui.theme.TranslateAppTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

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
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable("main", enterTransition = {
            slideInHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                // Offsets the content by 1/3 of its width to the left, and slide towards right
                // Overwrites the default animation with tween for this slide animation.
                fullWidth / 1
            }
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                // Offsets the content by 1/3 of its width to the left, and slide towards right
                // Overwrites the default animation with tween for this slide animation.
                fullWidth / 1
            }
        }
        ) {
            MainScreen(navController)
        }
        composable("imageTranslate", enterTransition = {
            slideInHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                -fullWidth / 1
            }
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                fullWidth / -1
            }
        }
        ) {
            ImageTrans(navController)
        }
        composable("bookMarkPage", enterTransition = {
            slideInHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                -fullWidth / 1
            }
        },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                    fullWidth / -1
                }
            }
        ) {
            BookMarkList(navController)
        }
    }
}


@Composable
fun TransLanguageMenu(
    selectedLanguage: String,
    onLanguageChange: (String, String) -> Unit, // 언어 이름과 TranslateLanguage 코드 전달,
    modifier: Modifier = Modifier
) {
    var expandedLang by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = { expandedLang = !expandedLang },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                selectedLanguage,
                fontSize = 20.sp,
                color = colorResource(R.color.textAndBtnColor),
            )
        }
        DropdownMenu(
            expanded = expandedLang,
            onDismissRequest = { expandedLang = false },
            offset = DpOffset(0.dp, 0.dp),
            modifier = Modifier
                .background(color = colorResource(R.color.dropdownBackGround)),
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
                    text = { Text(languageName, color = colorResource(R.color.textAndBtnColor)) },
                    onClick = {
                        onLanguageChange(languageName, languageCode) // 선택된 언어와 코드 전달
                        expandedLang = false
                    }
                )
            }
        }
    }
}

@Composable
fun ScanLanguage(
    text: String,
    targetLang: MutableState<String>,
    sourceLanguage: MutableState<String>,
    context: Context
) {
    val languageIdentifier = remember {
        LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build()
        )
    }
    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    when (languageCode) {
                        "ko" -> {
                            targetLang.value = "한국어"
                            sourceLanguage.value = TranslateLanguage.KOREAN
                        }

                        "en" -> {
                            targetLang.value = "영어"
                            sourceLanguage.value = TranslateLanguage.ENGLISH
                        }

                        "ja", "ja-Latn" -> {
                            targetLang.value = "일본어"
                            sourceLanguage.value = TranslateLanguage.JAPANESE
                        }

                        "zh", "zh-Latn" -> {
                            targetLang.value = "중국어"
                            sourceLanguage.value = TranslateLanguage.CHINESE
                        }

                        "de" -> {
                            targetLang.value = "독일어"
                            sourceLanguage.value = TranslateLanguage.GERMAN
                        }

                        "fr" -> {
                            targetLang.value = "프랑스어"
                            sourceLanguage.value = TranslateLanguage.FRENCH
                        }

                        else -> targetLang.value = "자동 감지"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "모델을 불러올 수 없음.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

@Composable
fun MakeTranslator(
    sourceLanguage: MutableState<String>,
    selectedLanguageCode: String,
    text: String,
    outputText: MutableState<String>,
) {
    val translator = remember(sourceLanguage, selectedLanguageCode, text) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.value)
            .setTargetLanguage(selectedLanguageCode)
            .build()
        Translation.getClient(options)
    }

    // Download translator model
    LaunchedEffect(translator) {
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }

    // Translate text whenever input text changes
    LaunchedEffect(text, selectedLanguageCode) {
        if (text.isNotEmpty()) {
            translator.translate(text)
                .addOnSuccessListener { translatedText ->
                    outputText.value = translatedText
                }
                .addOnFailureListener {
                }
        } else {
            outputText.value = ""
        }
    }
}

@Composable
fun UpperNavBar(
    text: String,
    targetLang: MutableState<String>,
    sourceLanguage: MutableState<String>,
    context: Context,
    outputText: String,
    selectedLanguage: MutableState<String>,
    selectedLanguageCode: MutableState<String>,
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    val db = remember { MyDb.getDatabase(context) }
    ScanLanguage(text, targetLang, sourceLanguage, context)

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.upperNavBackGround))
    ) {
        Image(
            painter = painterResource(R.drawable.menu_btn),
            contentDescription = "메뉴 열기",
            modifier = Modifier
                .size(40.dp)
                .weight(1f)
                .clickable {
                    scope.launch {
                        drawerState.open()
                    }
                }
        )
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = targetLang.value,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.textAndBtnColor),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            onClick = { },
            border = BorderStroke(3.dp, color = colorResource(R.color.textAndBtnColor)),
            colors = ButtonDefaults.buttonColors(disabledContainerColor = colorResource(R.color.upperNavBackGround)),
            enabled = false,
            modifier = Modifier
                .wrapContentWidth()
                .weight(1.5f)
                .padding(horizontal = 10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.switch_horizontal_svgrepo_com),
                contentDescription = "언어 서로 바꾸기",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(20.dp)
            )
        }

        TransLanguageMenu(
            selectedLanguage = selectedLanguage.value,
            onLanguageChange = { languageName, languageCode ->
                selectedLanguage.value = languageName
                selectedLanguageCode.value = languageCode
            },
            modifier = Modifier
                .weight(1.2f)
                .align(Alignment.CenterVertically)
        )
        Image(
            painter = painterResource(R.drawable.bookmark_icon),
            contentDescription = "북마크 저장",
            modifier = Modifier
                .weight(0.8f)
                .fillMaxSize()
                .padding(10.dp)
                .clickable {
                    if (text == "" || outputText == "") {
                        Toast
                            .makeText(context, "저장할 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast
                            .makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                        scope.launch(Dispatchers.IO) {
                            db
                                .sentenceDao()
                                .insertAll(
                                    Sentence(
                                        oriSentence = text,
                                        transSentence = outputText,
                                    )
                                )
                        }
                    }
                }
        )
    }
}

fun RunTts(tts: TextToSpeech, outputText: String, selectedLanguage: String, context: Context) {

    if (outputText.isNotEmpty()) {
        val result = when (selectedLanguage) {
            "한국어" -> tts.setLanguage(Locale.KOREAN)
            "영어" -> tts.setLanguage(Locale.ENGLISH)
            "중국어" -> tts.setLanguage(Locale.SIMPLIFIED_CHINESE)
            "일본어" -> tts.setLanguage(Locale.JAPANESE)
            "독일어" -> tts.setLanguage(Locale.GERMAN)
            "프랑스어" -> tts.setLanguage(Locale.FRENCH)
            else -> TextToSpeech.LANG_NOT_SUPPORTED
        }

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(
                context,
                "선택한 언어를 지원하지 않습니다: $selectedLanguage",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent().apply {
                action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            }
            context.startActivity(intent)
        } else {
            tts.speak(
                outputText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
    }
}

@Composable
fun OutputTextAndTtsBtn(
    tts: TextToSpeech,
    outputText: String,
    selectedLanguage: String,
    context: Context
) {

    Box(
        modifier = Modifier
            .background(
                color = colorResource(R.color.dropdownBackGround),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                5.dp,
                color = colorResource(R.color.dropdownBackGround),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(5.dp)
    ) {
        Text(
            outputText,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            color = colorResource(R.color.textAndBtnColor),
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.textfieldBackGround))
                .padding(5.dp)
        )
        Button(
            onClick = {
                RunTts(tts, outputText, selectedLanguage, context)
                Toast.makeText(context, "음성 기능 실행.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color(0x00ffffff),
                containerColor = Color(0x00ffffff)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Image(
                painter = painterResource(R.drawable.speaker_icon),
                contentDescription = "스피커"
            )
        }
    }
}

@Composable
fun BtnContent(photoId: Int, contentDescription: String, text: String, size: Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(photoId),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size.dp)
                .weight(1f)
        )
        Text(
            text,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun MenuScreen(navController: NavController, scope: CoroutineScope, drawerState: DrawerState) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(color = colorResource(R.color.upperNavBackGround))
            .fillMaxSize()
    ) {
        Text(
            "메뉴", color = colorResource(R.color.textAndBtnColor),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, top = 20.dp)
                .fillMaxSize()
        )
        Button(
            onClick = {
                navController.navigate("bookMarkPage") {
                    popUpTo("MainScreen") { inclusive = false } // 이전 스택 유지하지 않음
                    launchSingleTop = true // 중복된 화면 방지
                }
                scope.launch {
                    drawerState.close() // Drawer 상태를 Open으로 변경
                }

            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                "기록 보기",
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.textAndBtnColor)
            )
        }
        Spacer(modifier = Modifier.weight(8f))
    }
}