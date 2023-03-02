import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.lang.StringBuilder

fun main(args: Array<String>) {
    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "Repl.it uses operational transformations.",
        "[{\"op\": \"skip\", \"count\": 40}, {\"op\": \"delete\", \"count\": 47}]"
    )) // true

    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "Repl.it uses operational transformations.",
        "[{\"op\": \"skip\", \"count\": 45}, {\"op\": \"delete\", \"count\": 47}]"
    )) // false, delete past end

    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "Repl.it uses operational transformations.",
        "[{\"op\": \"skip\", \"count\": 40}, {\"op\": \"delete\", \"count\": 47}, {\"op\": \"skip\", \"count\": 2}]"
    )) // false, skip past end

    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "We use operational transformations to keep everyone in a multiplayer repl in sync.",
        "[{\"op\": \"delete\", \"count\": 7}, {\"op\": \"insert\", \"chars\": \"We\"}, {\"op\": \"skip\", \"count\": 4}, {\"op\": \"delete\", \"count\": 1}]"
    )) // true

    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "We can use operational transformations to keep everyone in a multiplayer repl in sync.",
        "[{\"op\": \"delete\", \"count\": 7}, {\"op\": \"insert\", \"chars\": \"We\"}, {\"op\": \"skip\", \"count\": 4}, {\"op\": \"delete\", \"count\": 1}]"
    )) // false

    println(isValid(
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "Repl.it uses operational transformations to keep everyone in a multiplayer repl in sync.",
        "[]"
    )) // true
}

@Serializable
data class Operation(val op: String, val count: Int? = null, val chars: String? = null)

fun isValid(original: String, modified: String, otjson: String): Boolean {
    val ops = Json.decodeFromString<List<Operation>>(otjson)
    var cursor = 0
    val originalToChange = StringBuilder(original)
    ops.forEach {
        when (it.op) {
            "skip" -> {
                if (it.count == null) return false
                cursor += it.count
                if (cursor > originalToChange.length) return false
            }
            "delete" -> {
                if (it.count == null) return false
                if (cursor + it.count > originalToChange.length) return false
                originalToChange.delete(cursor, cursor + it.count)
            }
            "insert" -> {
                if (it.chars == null) return false
                originalToChange.insert(cursor, it.chars)
                cursor += it.chars.length
            }
            else -> return false
        }
    }
    return originalToChange.toString() == modified
}

