package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.TravelDatabase
import com.example.data.database.TravelRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TravelViewModel
import com.example.ui.viewmodel.TravelViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room DB, Dao, and Repository
        val database = TravelDatabase.getDatabase(this)
        val repository = TravelRepository(database.visitedRegionDao())
        
        // Instantiate ViewModel
        val viewModelFactory = TravelViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TravelViewModel::class.java]

        setContent {
            MyApplicationTheme {
                // Intercept hardware/gesture system back pressed to return from country detail screen to home screen!
                val selectedCountryId by viewModel.selectedCountryId.collectAsStateWithLifecycle()
                if (selectedCountryId != null) {
                    BackHandler {
                        viewModel.selectCountry(null)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
