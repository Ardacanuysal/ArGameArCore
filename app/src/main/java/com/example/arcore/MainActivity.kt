package com.example.arcore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.arcore.ui.theme.ARCoreTheme
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ARScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun ARScreen() {
    val nodes = remember { mutableStateListOf<ArModelNode>() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGameAreaFixed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer?.isShadowReceiver = false

                // Oyun alanı için kırmızı zemin oluştur
                if (!isGameAreaFixed) {
                    val groundNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                        loadModelGlbAsync(
                            glbFileLocation = "models/ground.glb", // Kırmızı oyun alanı modelini yükle
                            scaleToUnits = 0.5f // Boyutu 50x50 cm olarak ayarla
                        ) {
                            position.x = 0.0f
                            position.y = 0.0f
                            position.z = -1.0f
                        }
                    }
                    nodes.add(groundNode) // Yüzey algılandığında ekleniyor
                }

                // Mouse modelini oluştur
                val mouseNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/mause.glb", // Mouse modelini yükle
                        scaleToUnits = 0.1f // Mouse modelinin ölçeğini belirle
                    ) {
                        position.x = 0.0f
                        position.y = 0.0f
                        position.z = -2.0f
                    }

                    onTap = { _, _ ->
                        coroutineScope.launch {
                            Toast.makeText(context, "Mouse tıklandı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                nodes.add(mouseNode) // Mouse node'u ekliyoruz

                // Board modelini oluştur
                val boardNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/board.glb", // Board modelini yükle
                        scaleToUnits = 300.0f // Board modelinin ölçeğini belirle
                    ) {
                        position.x = 0.0f // Kameranın orta noktası için X = 0
                        position.y = -0.5f // Yükseklik olarak hafifçe yere yakın bir konum
                        position.z = -1.0f // Kamera görüş açısına yakın, ama orta kısımda bir mesafe
                    }

                    onTap = { _, _ ->
                        coroutineScope.launch {
                            Toast.makeText(context, "Board tıklandı", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                nodes.add(boardNode) // Board node'u ekliyoruz
            }
        )

        // Oyun alanını sabitleme butonu
        Button(
            onClick = {
                coroutineScope.launch {
                    isGameAreaFixed = true // Oyun alanını sabitle
                    Toast.makeText(context, "Oyun alanı sabitlendi!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(0.5f) // Buton genişliği
        ) {
            Text("Oyun Alanını Sabitle")
        }
    }
}
