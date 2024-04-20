package com.aanchal.sensorapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.aanchal.sensorapp.ui.theme.SensorAppTheme
import java.io.OutputStream


class MainActivity : ComponentActivity() {
    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            DatabaseSensor::class.java,
            "sensorvalues.db"
        ).build()
    }

    private val viewModel by viewModels<SensorVM>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SensorVM(db.orientationDao()) as T
                }
            }
        }
    )
    private lateinit var orientationSensor: OrientationSensor
    private lateinit var orientationDao: OrientationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseSensor.getDatabase(this)
        orientationDao = database.orientationDao()

        setContent {
            SensorAppTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "start"
                ) {
                    composable("start") {
                        MyApp(viewModel,navController)
                    }
                    composable("history") {
                        GraphClass(viewModel)
                    }
                }
            }
        }

        orientationSensor = OrientationSensor(this) { roll, pitch, yaw ->
            viewModel.updateOrientation(roll, pitch, yaw)

        }
    }

    override fun onResume() {
        super.onResume()
        orientationSensor.start()
    }

    override fun onPause() {
        super.onPause()
        orientationSensor.stop()
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun MyApp(viewModel: SensorVM, navController: NavHostController) {
    val viewModel: SensorVM = viewModel
    var context = LocalContext.current
    var contentResolver = LocalContext.current.contentResolver
    val orientationData by viewModel.orientation.collectAsState()
    var isLoading = mutableStateOf(false)


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var checked by remember { mutableStateOf(false) }

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            thumbContent = if (checked) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }
        )
        if(checked){
            viewModel.updateIntoDatabase(
                orientationData.roll,
                orientationData.pitch,
                orientationData.yaw
            )
        }
        Text(text = "Roll: ${orientationData.roll}")
        Text(text = "Pitch: ${orientationData.pitch}")
        Text(text = "Yaw: ${orientationData.yaw}")
        val saveFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            if (uri != null) {
                val outputStream = uri.let { contentResolver.openOutputStream(it) }
                outputStream?.use { stream ->
                    writeDatabaseToFile(viewModel.databaseSet, stream,context)
                }
            }
        }
        var openHistory by remember { mutableStateOf(false) }

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                viewModel.copyDataintoDataset(isLoading) { isLoading.value = false }
                navController.navigate("history")
            }
        ) {
            Text("Show History Graph", color = Color.Black)
        }
        Button(modifier = Modifier
            .fillMaxWidth()
            ,colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                saveFileLauncher.launch("data_orient.txt")
            },
            content ={
                Text("Load Databse in your device", color = Color.Black)
            }
        )
    }

}

private fun writeDatabaseToFile(
    databaseSet: MutableSet<OrientationData>,
    outputStream: OutputStream,
    context: Context
) {
    if(databaseSet.isNotNull()){
        val stringBuilder = StringBuilder()
        databaseSet.forEachIndexed { index, orientationData ->
            stringBuilder.append("${orientationData.pitch}, ${orientationData.roll}, ${orientationData.yaw}, ${orientationData.time}\n")
        }
        outputStream.write(stringBuilder.toString().toByteArray())
    }
    else{
        Toast.makeText(context, "The database is empty", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun GraphClass(viewModel: SensorVM) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (viewModel.databaseSet.size>0) {

            Column {
                LineChartScreen(viewModel.databaseSet.map { it.pitch }, "Pitch")
                LineChartScreen(viewModel.databaseSet.map { it.roll }, "Roll")
                LineChartScreen(viewModel.databaseSet.map { it.yaw }, "Yaw")
            }
        }
    }
}

//picked from yml library to get graphs
@Composable
fun LineChartScreen(data: List<Float>, angleName: String) {
    val steps = 5

    val xAxisData = AxisData.Builder()
        .axisStepSize((data.size / 10).toFloat().dp)
        .backgroundColor(Color.Transparent)
        .steps(data.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps * 2)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = 100 / steps
            ((i - steps) * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val pointsData = data.mapIndexed { index, value ->
        Point(index.toFloat(), value)
    }

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = Color.Blue,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(color = Color.Red),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Cyan,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
    )
    Text(
        text = angleName,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Color.Black
    )
    LineChart(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        lineChartData = lineChartData
    )

}
