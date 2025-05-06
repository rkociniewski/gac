package rk.gac

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import rk.gac.ui.PericopeScreen
import rk.gac.ui.theme.GospelACasoTheme
import rk.gac.viewModel.PericopeViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GospelACasoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    val viewModel: PericopeViewModel = viewModel()
                    PericopeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
