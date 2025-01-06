package com.dreamb.translateapp

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var targetLang by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }


    val languageIdentifier = remember {
        LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build()
        )
    }
    languageIdentifier.identifyLanguage(text)
        .addOnSuccessListener {
            languageIdentifier.identifyPossibleLanguages(text)
                .addOnSuccessListener { identifiedLanguages ->
                    for (identifiedLanguage in identifiedLanguages) {
                        val language = identifiedLanguage.languageTag
                        targetLang = when (language) {
                            "ko" -> "한국어"
                            "en" -> "영어"
                            "ja" -> "일본어"
                            "ja-Latn" -> "일본어"
                            "zh" -> "중국어"
                            "zh-Latn" -> "중국어"
                            "de" -> "독일어"
                            "fr" -> "프랑스어"
                            else -> "자동 감지"
                        }
                    }
                }
        }
        .addOnFailureListener {
            val toast = Toast.makeText(context, "모델을 불러올 수 없음.", Toast.LENGTH_SHORT)
            toast.show()
        }


    val engilshKoreanTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()
        Translation.getClient(options)
    }
    LaunchedEffect(engilshKoreanTranslator) {
        var conditions = DownloadConditions.Builder()
            .build()
        engilshKoreanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }


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
            Image(
                painter = painterResource(R.drawable.menu_btn),
                contentDescription = "메뉴 열기",
                modifier = Modifier
                    .size(40.dp)
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = targetLang,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth())
            }
            Button(
                onClick = {},
                border = BorderStroke(5.dp, Color.LightGray),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .wrapContentWidth()
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
            ) {
                TransLanguageMenu()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
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
                textStyle = TextStyle(fontSize = 20.sp),
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
            engilshKoreanTranslator.translate(text)
                .addOnSuccessListener { translatedText ->
                    outputText = translatedText
                }
            Text(
                outputText,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(10.dp)
            )
        }

        Button(
            onClick = {},
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff77dd66)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.mic_svgrepo_com),
                    contentDescription = "음성 인식",
                    modifier = Modifier
                        .size(40.dp)
                )
                Text("음성")
            }
        }
        Button(
            onClick = {
                navController.navigate("imageTranslate")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff8888ff)),
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.photo_svgrepo_com),
                    contentDescription = "이미지",
                    modifier = Modifier
                        .size(40.dp)
                )
                Text("이미지")
            }

        }
    }
}
