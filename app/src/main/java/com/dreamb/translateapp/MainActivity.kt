package com.dreamb.translateapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamb.translateapp.ui.theme.TranslateAppTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslateAppTheme {
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TranslateAppTheme {
        MainScreen()
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var targetLang by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val languageIdentifier = LanguageIdentification.getClient(
        LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    )
    languageIdentifier.identifyLanguage(text)
        .addOnSuccessListener { languageCode ->
            if (languageCode == "und") {
                val toast = Toast.makeText(context, "알 수 없는 언어입니다.", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                val toast = Toast.makeText(context, languageCode, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        .addOnFailureListener {
            val toast = Toast.makeText(context, "모델을 불러올 수 없음.", Toast.LENGTH_SHORT)
            toast.show()
        }
    languageIdentifier.identifyPossibleLanguages(text)
        .addOnSuccessListener { identifiedLanguages ->
            for (identifiedLanguage in identifiedLanguages) {
                val language = identifiedLanguage.languageTag
                targetLang = when {      // languageTag에 따라 "한국어", "중국어", "영어" 등 6개로 조건문써서 문자열 출력
                    language == "en" -> "영어"
                    else -> "알 수 없는 언어"
                }
                val confidence = identifiedLanguage.confidence
            }
        }
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.KOREAN)
        .build()
    val engilshKoreanTranslator = Translation.getClient(options)
    var conditions = DownloadConditions.Builder()
        .build()
    engilshKoreanTranslator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {

        }
        .addOnFailureListener { exception ->

        }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.DarkGray)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentSize()
                    .weight(5f)
            ) {
                Text(targetLang, fontSize = 20.sp)
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
                Text("영어", fontSize = 20.sp)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .weight(1f)
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
                textStyle = TextStyle(fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxSize()
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
            onClick = {},
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