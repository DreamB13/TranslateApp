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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

@Preview(showBackground = true)
@Composable
fun ImageTrans() {
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
                Text(text = "한국어",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }
            Button(
                onClick = {},
                border = BorderStroke(5.dp, Color.LightGray),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
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
        ){

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
                .background(color = Color.White)
        ){

        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff7777dd)),
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
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff4444cc)),
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
                    painter = painterResource(R.drawable.camera),
                    contentDescription = "촬영",
                    modifier = Modifier
                        .size(50.dp)
                )
                Text("카메라")
            }

        }

    }
}