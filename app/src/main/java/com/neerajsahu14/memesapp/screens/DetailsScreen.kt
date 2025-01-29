package com.neerajsahu14.memesapp.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun DetailsScreen(modifier: Modifier = Modifier, name: String?, url: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier
            .background(Color(0xffffc107))
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 45.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (url != null) {
                AsyncImage(
                    model = url, contentDescription = name,
                    modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            if (name != null) {
                Text(
                    text = name,
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 45.sp
                )
            }
        }
        item {
            Button(onClick = {
                if (url != null) {
                    scope.launch {
                        downloadImage(context, url, name ?: "image")
                    }
                }
            }) {
                Text(text = "Download Image")
            }
        }
        item {
            Button(onClick = {
                if (url != null) {
                    scope.launch {
                        shareImage(context, url, name ?: "image")
                    }
                }
            }) {
                Text(text = "Share Image")
            }
        }
    }
}

private suspend fun downloadImage(context: Context, url: String, name: String): File? {
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()

    val result = (imageLoader.execute(request) as SuccessResult).drawable
    val bitmap = (result as BitmapDrawable).bitmap

    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File(path, "$name.jpg")
    val outputStream: OutputStream

    return try {
        path.mkdirs()
        outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        withContext(Dispatchers.IO) {
            outputStream.flush()
        }
        withContext(Dispatchers.IO) {
            outputStream.close()
        }
        Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_SHORT).show()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
        null
    }
}
private suspend fun shareImage(context: Context, url: String, name: String) {
    val file = downloadImage(context, url, name)
    file?.let {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}