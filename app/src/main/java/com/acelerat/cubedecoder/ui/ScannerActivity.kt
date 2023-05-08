package com.acelerat.cubedecoder.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.acelerat.cubedecoder.R
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.*

class ScannerActivity : CameraActivity() {

    lateinit var cameraBridgeViewBase: CameraBridgeViewBase

    private lateinit var colorDetector: ColorDetector

    private val rubiksCubeColors = mapOf(

        //V1
//        "white" to arrayOf(Scalar(0.0, 0.0, 200.0), Scalar(180.0, 50.0, 255.0)),
//        "blue" to arrayOf(Scalar(5.0, 100.0, 100.0), Scalar(15.0, 255.0, 255.0)),
//        "red" to arrayOf(Scalar(0.0, 100.0, 100.0), Scalar(10.0, 255.0, 255.0)),
//        "red2" to arrayOf(Scalar(170.0, 100.0, 100.0), Scalar(180.0, 255.0, 255.0)),
//        "green" to arrayOf(Scalar(45.0, 100.0, 100.0), Scalar(75.0, 255.0, 255.0)),
//        "orange" to arrayOf(Scalar(100.0, 100.0, 100.0), Scalar(130.0, 255.0, 255.0)),
//        "yellow" to arrayOf(Scalar(20.0, 39.0, 64.0), Scalar(40.0, 255.0, 255.0)),

        //V2
//        "white" to arrayOf(Scalar(0.0, 0.0, 231.0), Scalar(180.0, 18.0, 255.0)),
//        "blue" to arrayOf(Scalar(90.0, 50.0, 70.0), Scalar(128.0, 255.0, 255.0)),
//        "red1" to arrayOf(Scalar(159.0, 50.0, 70.0), Scalar(180.0, 255.0, 255.0)),
//        "red2" to arrayOf(Scalar(0.0, 50.0, 70.0), Scalar(9.0, 255.0, 255.0)),
//        "green" to arrayOf(Scalar(36.0, 50.0, 70.0), Scalar(89.0, 255.0, 255.0)),
//        "orange" to arrayOf(Scalar(10.0, 50.0, 70.0), Scalar(24.0, 255.0, 255.0)),
//        "yellow" to arrayOf(Scalar(25.0, 50.0, 70.0), Scalar(35.0, 255.0, 255.0)),
//        "purple" to arrayOf(Scalar(129.0, 50.0, 70.0), Scalar(158.0, 255.0, 255.0)),
//        "black" to arrayOf(Scalar(0.0, 0.0, 0.0), Scalar(180.0, 255.0, 30.0)),
//        "gray" to arrayOf(Scalar(0.0, 0.0, 40.0), Scalar(180.0, 18.0, 230.0)),

        //V3 the closest one so far
//        "white" to arrayOf(Scalar(0.0, 0.0, 200.0), Scalar(180.0, 50.0, 255.0)),
//        "blue" to arrayOf(Scalar(5.0, 100.0, 100.0), Scalar(15.0, 255.0, 255.0)),
//        "red" to arrayOf(Scalar(0.0, 100.0, 20.0), Scalar(10.0, 255.0, 255.0)),
//        "red2" to arrayOf(Scalar(160.0, 100.0, 20.0), Scalar(179.0, 255.0, 255.0)),
//        "green" to arrayOf(Scalar(45.0, 100.0, 100.0), Scalar(75.0, 255.0, 255.0)),
//        "orange" to arrayOf(Scalar(100.0, 100.0, 100.0), Scalar(126.0, 255.0, 255.0)),
//        "yellow" to arrayOf(Scalar(70.0, 50.0, 70.0), Scalar(100.0, 255.0, 255.0)),


        //V4
        "white" to arrayOf(Scalar(0.0, 0.0, 200.0), Scalar(180.0, 50.0, 255.0)),
        "blue" to arrayOf(Scalar(5.0, 100.0, 100.0), Scalar(15.0, 255.0, 255.0)),
//        "red" to arrayOf(Scalar(0.0, 100.0, 20.0), Scalar(10.0, 255.0, 255.0)),
//        "red2" to arrayOf(Scalar(160.0, 100.0, 20.0), Scalar(179.0, 255.0, 255.0)),
        "green" to arrayOf(Scalar(45.0, 100.0, 100.0), Scalar(75.0, 255.0, 255.0)),
        "red_color" to arrayOf(Scalar(100.0, 100.0, 100.0), Scalar(126.0, 255.0, 255.0)),
        "yellow" to arrayOf(Scalar(70.0, 50.0, 70.0), Scalar(100.0, 255.0, 255.0)),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_view)
        val viewModel by viewModels<MainViewModel>()

        val scannerPermissionsView = findViewById<ComposeView>(R.id.scannerPermissions)

        scannerPermissionsView.setContent {
            ScannerPermissions(viewModel = viewModel)
        }

        cameraBridgeViewBase = findViewById(R.id.camera)

        colorDetector = ColorDetector(rubiksCubeColors)

        cameraBridgeViewBase.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {
                //
            }

            override fun onCameraViewStopped() {
                //
            }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat? {
                val frame = inputFrame?.rgba()
                return frame?.let { processFrame(it) }
            }
        })

        if(OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView()
        }

    }

    override fun onResume() {
        super.onResume()
        cameraBridgeViewBase.enableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraBridgeViewBase.disableView()
    }

    override fun onPause() {
        super.onPause()
        cameraBridgeViewBase.disableView()
    }

    override fun getCameraViewList(): MutableList<out CameraBridgeViewBase> {
        return Collections.singletonList(cameraBridgeViewBase)
    }

    private fun processFrame(frame: Mat): Mat {
        val output = frame.clone()
        for ((colorName, _) in rubiksCubeColors) {
            val mask = colorDetector.detect(frame, colorName)
            val contours = colorDetector.findContours(mask)

            for (contour in contours) {
                val area = Imgproc.contourArea(contour)
                if (area > 1000) { // Adjust the threshold based on your requirements
                    val rect = Imgproc.boundingRect(contour)
                    Imgproc.rectangle(output, rect.tl(), rect.br(), Scalar(255.0, 0.0, 0.0), 2)
                    Imgproc.putText(output, colorName, rect.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, Scalar(255.0, 0.0, 0.0), 2)
                }

            }
        }
        return output
    }

    class ColorDetector(private val colors: Map<String, Array<Scalar> >) {

        fun detect(frame: Mat, colorName: String): Mat {
            val hsv = Mat()
            Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV)

            val colorRange = colors[colorName]
            if (colorRange != null) {
                Core.inRange(hsv, colorRange[0], colorRange[1], hsv)
            }
            return hsv
        }

        fun findContours(mask: Mat): List<MatOfPoint> {
            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
            return contours
        }
    }
}