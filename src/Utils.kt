import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun testFile(day: Int) = File("src", "Day${day.toString().padStart(2, '0')}_test.txt")

fun inputFile(day: Int) = File("src", "Day${day.toString().padStart(2, '0')}.txt")

fun <T> runDay(day: Int, expectedTest: T, function: (File) -> T) {
    val testResult = function(testFile(day))
    check(testResult == expectedTest) { "Instead of $expectedTest was $testResult" }

    println("Result: " + function(inputFile(day)))
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
