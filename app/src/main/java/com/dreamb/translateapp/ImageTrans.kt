package com.dreamb.translateapp

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun ImageTrans(navController: NavController) {
    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val outputText = remember { mutableStateOf("") }
    val targetLang = remember { mutableStateOf("언어감지") }
    val sourceLanguage = remember { mutableStateOf(TranslateLanguage.KOREAN) }
    val context = LocalContext.current
    val selectedLanguage = remember { mutableStateOf("한국어") }
    val selectedLanguageCode = remember { mutableStateOf(TranslateLanguage.KOREAN) }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
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

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imageUri = uri
            }
        }
    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            val recognizer =
                TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            try {
                val image = InputImage.fromFilePath(context, imageUri!!)
                recognizer.process(image)
                    .addOnSuccessListener { result ->
                        text = result.text
                    }
                    .addOnFailureListener { e ->
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    MakeTranslator(sourceLanguage, selectedLanguageCode.value, text, outputText)
    ScanLanguage(text, targetLang, sourceLanguage, context)

    // Camera Launcher 설정
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "카메라 권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                Toast.makeText(context, "사진 저장 완료", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
            }
        }
    )
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
                    scope,
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
                    .background(color = colorResource(R.color.dropdownBackGround))
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = colorResource(R.color.textBackGround)
                    )
            ) {
                Image(
                    rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(
                                color = colorResource(R.color.dropdownBackGround),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = text,
                            fontSize = 20.sp,
                            color = colorResource(R.color.textAndBtnColor),
                            modifier = Modifier
                                .border(
                                    5.dp,
                                    color = colorResource(R.color.dropdownBackGround),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(5.dp)
                                .background(color = colorResource(R.color.textfieldBackGround))
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                shape = RoundedCornerShape(10.dp),
                                color = colorResource(R.color.dropdownBackGround)
                            )
                    ) {
                        OutputTextAndTtsBtn(tts, outputText.value, selectedLanguage.value, context)
                    }
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    BtnContent(R.drawable.gallery_icon, "갤러리", "갤러리", 40)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            // 권한이 허용된 경우 카메라 실행
                            val uri = createImageUri(context)
                            cameraLauncher.launch(uri)
                            imageUri = uri
                        } else {
                            // 권한 요청
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 10.dp)
                        .weight(1f)
                ) {
                    BtnContent(R.drawable.camera, "카메라", "촬영하기", 50)
                }
                Button(
                    onClick = {
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 20.dp)
                        .weight(1f)
                ) {
                    BtnContent(R.drawable.undo_icon, "뒤로 가기", "뒤로 가기", 40)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}

// 이미지 Uri 생성 함수
private fun createImageUri(context: Context): Uri {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: throw IllegalStateException("Failed to create new MediaStore record.")
}