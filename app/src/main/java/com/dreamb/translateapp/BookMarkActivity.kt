package com.dreamb.translateapp

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
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
        Column(
            modifier = Modifier
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
                        .border(2.dp, Color.LightGray, RoundedCornerShape(10.dp))
                )
                {
                    Text(
                        user.oriSentence.toString(),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Text(
                        user.transSentence.toString(),
                        fontSize = 20   .sp,
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
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffff8888)),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BtnContent(R.drawable.undo_icon, "뒤로 가기", "뒤로 가기", 40)
            }
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val latestSentence = db.sentenceDao().getLatestSentence() // 최신 데이터 가져오기
                    latestSentence?.let { db.sentenceDao().delete(it) } // 데이터가 존재하면 삭제
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffff4444)),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                BtnContent(R.drawable.delete_icon, "삭제", "최근 목록 삭제", 40)
            }
        }
    }
}