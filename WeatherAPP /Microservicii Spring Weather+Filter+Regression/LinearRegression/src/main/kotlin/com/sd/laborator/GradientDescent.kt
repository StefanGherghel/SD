package com.sd.laborator

import com.sd.laborator.model.Point
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.pow

@Service
class GradientDescent {
    private fun dX(data: List<Point>, expectedYs: List<Double>, size: Int) =
        (-1.0 / size) * data.mapIndexed { index, point ->  point.x * (point.y - expectedYs[index])}.sum()

    private fun dY(data: List<Point>, expectedYs: List<Double>, size: Int) =
        (-1.0 / size) * data.mapIndexed { index, point -> (point.y - expectedYs[index])}.sum()

    private fun mSE(data: List<Point>, expectedYs: List<Double>, size: Int) =
        data.mapIndexed { index, point -> (point.y - expectedYs[index]).pow(2.0) }.sum()

    fun applyGradient(input: List<Point>, learningRate: Double, epochs: Int, epsilon: Double): Triple<Double, Double, Double> {
        // Initiate coefficients
        var x: Double = 0.0
        var y: Double = 0.0

        // Normalize data
        val xMax = input.maxOfOrNull { it.x }
        val yMax = input.maxOfOrNull { it.y }

        val data = input.map { Point(it.x / xMax!!, it.y / yMax!!)}
        var oldMSE = 0.0

        // Apply gradient descent
        run breaking@ {
            (0..epochs).forEach { it ->
                val yPredictions = data.map { x * it.x + y }

                val dX = dX(data, yPredictions, yPredictions.size)
                val dY = dY(data, yPredictions, yPredictions.size)
                val mSE = mSE(data, yPredictions, yPredictions.size)

                println("Epoch: $it -> dX = $dX, dY = $dY, mSE = $mSE")

                x -= learningRate * dX
                y -= learningRate * dY

                if(abs(mSE - oldMSE) < epsilon)
                    return@breaking
                oldMSE = mSE
            }
        }

        val yPredictions = data.map { x * it.x + y }
        val mSE = mSE(data, yPredictions, yPredictions.size)
        return Triple(x * yMax!! / xMax!!, y * yMax, mSE)
    }
}