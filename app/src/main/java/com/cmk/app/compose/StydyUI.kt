package com.cmk.app.compose

import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmk.app.R
import com.cmk.app.ui.activity.WebActivity
import com.cmk.app.vo.ArticleVo

@Composable
fun FlexibleComposable() {
    Row(Modifier.width(400.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(200.dp)
                .background(Color.Yellow)
        )
        Box(
            modifier = Modifier
                .weight(2f)
                .height(200.dp)
                .background(Color.Green)
        )
    }
}


@Composable
fun HomeScreen() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {

        item {
            Text(text = "First item")
        }

        // Add 5 items
        items(5) { index ->
            Text(text = "Item: $index")
        }

        // Add another single item
        item {
            Text(text = "Last item")
        }
    }
}

@Composable
fun CenterText() {
//    Text("Hello World", textAlign = TextAlign.Center,
//        modifier = Modifier.width(150.dp))
    Text(buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append("Hello\n")
        }
        withStyle(style = SpanStyle(color = Color.Green)) {
            append("World\n")
        }
        append("android")
    })
}


@Composable
fun SimpleFilledTextFieldSample() {
    var text = remember { mutableStateOf("") }
}


@Composable
fun CanvasDemo() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        drawLine(
            start = Offset(x = width, y = 0f),
            end = Offset(x = 0f, y = height),
            color = Color.Blue,
            strokeWidth = 5F
        )
        drawCircle(
            color = Color.Blue,
            center = Offset(width / 2, height / 2),
            radius = size.minDimension / 4
        )
    }
}


@Composable
fun OffsetComposable() {
    Box(
        modifier = Modifier
            .background(Color.Yellow)
            .width(120.dp)
            .height(60.dp)
    ) {
        Text(text = "hello world", Modifier.offset(x = 30.dp, y = 10.dp))
    }
}

@Composable
fun ItemViewCompose(context: Context, data: ArticleVo.DataX) {
    val holderImg =
        ImageBitmap.imageResource(
            id = if (data.collect) R.mipmap.ic_favour_s
            else R.mipmap.ic_favour_n
        )
    val originImg = ImageBitmap.imageResource(id = data.getIcon())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(96.dp)
            .clickable {
                Toast
                    .makeText(context, "-----", Toast.LENGTH_SHORT)
                    .show()
                context.startActivity(Intent(context, WebActivity::class.java))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = holderImg, contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(8.dp)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    bitmap = originImg,
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(14.dp)
                        .height(14.dp)
                )
                Text(
                    text = data.title,
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1
                )
            }
//            Text(
//                text = data.link,
//                fontSize = 16.sp,
//                color = Color(0xFF999999),
//                modifier = Modifier.padding(top = 4.dp),
//                maxLines = 3
//            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = data.superChapterName,
                    fontSize = 10.sp,
                    color = Color(0xFF43a047),
                    modifier = Modifier
                        .border(
                            0.5.dp,
                            color = Color(0xFF43a047),
                            shape = RoundedCornerShape(2.dp),
                        )
                        .padding(4.dp, 1.dp, 4.dp, 1.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.chapterName, fontSize = 10.sp, color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color(0xFFF45A8D),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .padding(start = 8.dp, 1.dp, 8.dp, 1.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "分享人：${data.author}",
                    color = Color(0xFF999999),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = data.niceShareDate,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun EditText() {
    val text = remember { mutableStateOf("") }
    Column {
        OutlinedTextField(
            value = text.value,
            onValueChange = { text.value = it },
            label = { Text(text = "account") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color(0xFF80C2FF),
                textColor = Color(0xFFF45A8D),
                placeholderColor = Color(0xFF99999),
                focusedBorderColor = Color(0xFF43a047),
                focusedLabelColor = Color(0xFFc6a700),
            ),
            placeholder = { Text(text = "default", color = Color(0xFF333333)) },
        )
    }
}