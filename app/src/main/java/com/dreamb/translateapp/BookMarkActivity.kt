package com.dreamb.translateapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.room.Room
import com.dreamb.translateapp.model.AppDatabase
import com.dreamb.translateapp.model.MyDb
import com.dreamb.translateapp.model.SentenceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BookMarkList(navController: NavController) {
    val context = LocalContext.current
    val db = remember { MyDb.getDatabase(context) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = colorResource(R.color.textBackGround))
        ) {
            Text(
                "목록",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.textAndBtnColor),
            )
        }
        Column(
            modifier = Modifier
                .background(color = colorResource(R.color.dropdownBackGround))
                .weight(8.5f)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            val list by db.sentenceDao().getAll()
                .collectAsStateWithLifecycle(emptyList())
            list.forEach { user ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = colorResource(R.color.upperNavBackGround))
                        .border(2.dp,colorResource(R.color.textAndBtnColor), RoundedCornerShape(10.dp))
                )
                {
                    Text(
                        user.oriSentence.toString(),
                        color = colorResource(R.color.textAndBtnColor),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Text(
                        user.transSentence.toString(),
                        color = colorResource(R.color.textAndBtnColor),

                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val latestSentence = db.sentenceDao().getLatestSentence() // 최신 데이터 가져오기
                        latestSentence?.let { db.sentenceDao().delete(it) } // 데이터가 존재하면 삭제
                    }
                    Toast.makeText(context,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.upperNavBackGround)),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BtnContent(R.drawable.delete_icon, "삭제", "최근 목록 삭제", 40)
            }
            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.textBackGround)),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BtnContent(R.drawable.undo_icon, "뒤로 가기", "뒤로 가기", 40)
            }

        }
    }
}