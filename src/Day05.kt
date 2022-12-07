import java.io.File
import java.util.Stack

private fun part1(input: File): String {
    val (initialState, steps) = input.readText().split("\n\n")
    val tower = parseTower(initialState)
    tower.runSteps(steps)
    return String(tower.map { it.pop() }.toCharArray())
}

private fun part2(input: File): String {
    val (initialState, steps) = input.readText().split("\n\n")
    val tower = parseTower(initialState)
    tower.runSteps2(steps)
    return String(tower.map { it.pop() }.toCharArray())
}

private fun parseTower(initialState: String): Array<Stack<Char>> {
    val lines = initialState.lines().reversed().iterator()
    val last = lines.next()
    val stackCount = last.length/4 + 1
    val result = Array(stackCount) { Stack<Char>() }
    lines.forEach {
        it.asSequence().chunked(4).forEachIndexed { index, chars ->
            when(chars[1]) {
                ' ' -> {}
                else -> result[index].push(chars[1])
            }
        }
    }
    return result
}

private fun Array<Stack<Char>>.runSteps(steps: String) {
    steps.lineSequence().forEach { line ->
        val (times, origin, target) = line.split("move ", " from ", " to ").drop(1).map(String::toInt)
        for (i in 0 until times) {
            get(target - 1).push(get(origin - 1).pop())
        }
        println("Step $line moves $times from $origin to $target. Result: ${humanReadable()}")
    }
}

private fun Array<Stack<Char>>.runSteps2(steps: String) {
    steps.lineSequence().forEach { line ->
        val (times, origin, target) = line.split("move ", " from ", " to ").drop(1).map(String::toInt)
        val intermediateStack = Stack<Char>()
        for (i in 0 until times) {
            intermediateStack.push(get(origin - 1).pop())
        }
        for (i in 0 until times) {
            get(target - 1).push(intermediateStack.pop())
        }
        println("Step $line moves $times from $origin to $target. Result: ${humanReadable()}")
    }
}

private fun Array<Stack<Char>>.humanReadable(): String = joinToString(separator = ";") { String(it.toCharArray()) }

fun main() {
    runDay(5, "CMZ", ::part1)

    runDay(5, "MCD", ::part2)
}
