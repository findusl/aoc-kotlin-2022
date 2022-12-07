import java.io.File
import java.util.Stack

private fun part1(input: File): Int {
    val root = input.useLines {
        PredictiveIterator(it.iterator()).parseFileSystem()
    }
    return root.allDirectories().filter { it.size < 100000 }.sumOf { it.size }
}

private fun part2(input: File): Int {
    val root = input.useLines {
        PredictiveIterator(it.iterator()).parseFileSystem()
    }
    val freeSpace = 70000000 - root.size
    val sizeNeeded = 30000000 - freeSpace
    return root.allDirectories().map { it.size }.filter { it > sizeNeeded }.min()
}

private fun FileSystem.Directory.allDirectories(): List<FileSystem.Directory> {
    return children.values.filterIsInstance<FileSystem.Directory>().flatMap { it.allDirectories() } + this
}

private fun PredictiveIterator<String>.parseFileSystem(
    root: FileSystem.Directory = FileSystem.Directory(),
    current: FileSystem.Directory = root
): FileSystem.Directory {
    if (!hasNext()) return root

    return when (val command = next()) {
        "$ cd /" -> {
            parseFileSystem(root, root)
        }
        "$ ls" -> {
            do {
                val subFile = takeNextIf { !it.startsWith("$") } ?: break
                if (subFile.startsWith("dir")) {
                    val fileName = subFile.substring(4)
                    current.children[fileName] = FileSystem.Directory(parent = current)
                } else {
                    val (size, name) = subFile.split(' ')
                    current.children[name] = FileSystem.File(size.toInt(), current)
                }
            } while (true)
            parseFileSystem(root, current)
        }
        "$ cd .." -> {
            parseFileSystem(root, current.parent as FileSystem.Directory)
        }
        else -> {
            assert(command.startsWith("$ cd"))
            val fileName = command.substring(5)
            val file = current.children[fileName] as? FileSystem.Directory
            checkNotNull(file) { "No directory $fileName for command $command" }
            parseFileSystem(root, file)
        }
    }
}

private sealed interface FileSystem {
    val size: Int
    val parent: FileSystem?

    class File(override val size: Int, override val parent: FileSystem): FileSystem

    class Directory(
        val children: MutableMap<String, FileSystem> = mutableMapOf(),
        override val parent: FileSystem? = null
    ): FileSystem {
        override val size: Int by lazy { children.values.sumOf { it.size } }
    }
}

private class PredictiveIterator<T: Any>(private val iterator: Iterator<T>): Iterator<T> {
    private var next: T? = null

    override fun hasNext(): Boolean = next != null || iterator.hasNext()

    override fun next(): T {
        val next = this.next
        return if (next != null) {
            this.next = null
            next
        } else {
            iterator.next()
        }
    }

    fun takeNextIf(predicate: (T) -> Boolean): T? {
        if (next != null) return next
        if (!iterator.hasNext()) return null
        val next = iterator.next()
        return if (predicate(next)) next
        else {
            this.next = next
            null
        }
    }
}

fun main() {
    runDay(7, 95437, ::part1)

    runDay(7, 24933642, ::part2)
}
