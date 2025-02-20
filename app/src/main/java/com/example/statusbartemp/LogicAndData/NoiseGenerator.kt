package com.example.statusbartemp.LogicAndData

import com.example.statusbartemp.LogicAndData.Constants.Companion.latDegreesOf100Meters
import com.example.statusbartemp.LogicAndData.Constants.Companion.lonDegreesOf100Meters
import kotlin.math.floor
import kotlin.random.Random

fun generateSmoothNoise(input: Double, grainSize : Double, secretSeed : Long, min: Double = 0.0, max: Double = 1.0): Double {
    // Ensure the range is valid
    require(min <= max) { "min should be less than or equal to max" }
    
    // Get the integer and fractional parts of the input
    val inputGrainy = input/grainSize
    val intPart = floor(inputGrainy).toLong()
    val fracPart = inputGrainy - intPart
    
    // Generate two noise values based on the integer and the next integer
    val noise1 = deterministicNoise(intPart + secretSeed, min, max)
    val noise2 = deterministicNoise(intPart + 1 + secretSeed, min, max)
    
    // Interpolate between the two noise values using the fractional part
    return lerp(noise1, noise2, fracPart)
}

private fun deterministicNoise(seed: Long, min: Double, max: Double): Double {
    val random = Random(seed)
    return min + random.nextDouble() * (max - min)
}

private fun lerp(start: Double, end: Double, t: Double): Double {
    return start + t * (end - start)
}

fun calculateNoisyValue(
    input: Double,
    grainSize: Double,
    secretSeed: Long,
    noiseMin: Double = 0.0,
    noiseMax: Double = 1.0
) : Double {
    return input + generateSmoothNoise(
        input = input,
        grainSize = grainSize,
        secretSeed = secretSeed,
        min = noiseMin,
        max = noiseMax
    )
    
}

fun calculateNoisyDegrees(
    input: Double,
    secretSeed: Long,
    latitude : Boolean,
) : Double {
    val degrees100Meters = when(latitude){
        true -> latDegreesOf100Meters
        false -> lonDegreesOf100Meters
    }
    return input + generateSmoothNoise(
        input = input,
        grainSize = 1.0*degrees100Meters,
        secretSeed = secretSeed,
        min = -3.0*degrees100Meters,
        max = 3.0*degrees100Meters
    )
    
}

fun calculateNoiseOffsetDegrees(
    input: Double,
    secretSeed: Long,
    latitude : Boolean,
) : Double {
    val degrees100Meters = when(latitude){
        true -> latDegreesOf100Meters
        false -> lonDegreesOf100Meters
    }
    return generateSmoothNoise(
        input = input,
        grainSize = 1.0*degrees100Meters,
        secretSeed = secretSeed,
        min = -3.5*degrees100Meters,
        max = 3.5*degrees100Meters
    )
    
}