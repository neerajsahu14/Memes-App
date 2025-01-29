package com.neerajsahu14.memesapp

import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neerajsahu14.memesapp.model.Meme
import com.neerajsahu14.memesapp.screens.DetailsScreen
import com.neerajsahu14.memesapp.screens.HomeScreen
import com.neerajsahu14.memesapp.ui.theme.MemesAppTheme
import com.neerajsahu14.memesapp.util.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemesAppTheme {

                val navController = rememberNavController()
                var memeList by remember {
                    mutableStateOf( listOf<Meme>()
                    )
                }
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = true){
                    scope.launch(Dispatchers.IO) {
                        val response = try {
                            RetrofitInstance.api.getMemeList()
                        }catch (e: IOException){
                            Toast.makeText(
                                this@MainActivity,
                                "app error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }catch (e : HttpException){
                            Toast.makeText(
                                this@MainActivity,
                                "HTTP error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null){
                            withContext(Dispatchers.Main){
                                memeList = response.body()!!.data.memes
                            }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "HomeScreen") {
                            composable("HomeScreen") {
                                HomeScreen(memeList, navController)
                            }
                            composable("DetailsScreen?name={name}&url={url}",
                                arguments = listOf(navArgument(name = "name"){
                                    type = NavType.StringType
                                },
                                    navArgument(name = "url"){
                                    type = NavType.StringType
                                })
                            ) {

                                DetailsScreen(
                                    name = it.arguments?.getString("name"),
                                    url = it.arguments?.getString("url")
                                )
                            }
                        }
            }
        }
    }
}
