import com.cws.print.LogLevel
import com.cws.print.Print
import com.cws.print.SafeCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

object PrintTests {

    val logTags = listOf("Auth", "Network", "Database", "Cache", "UI", "App", "Profiler", "Service")

    val verboseMessages = listOf(
        "Start profiling function X", "End profiling function X",
        "Entering method Y", "Exiting method Y", "Loop iteration finished"
    )

    val debugMessages = listOf(
        "Checking credentials", "Cache hit for key", "Request headers: {Authorization=...}",
        "Response received", "Parsing JSON", "Updating state", "Coroutine started"
    )

    val infoMessages = listOf(
        "Application started", "User profile loaded", "Connected to database",
        "Service initialized", "Background task finished"
    )

    val warnMessages = listOf(
        "Token about to expire", "Response slow", "Cache miss", "Deprecated API usage",
        "Low memory warning"
    )

    val errorMessages = listOf(
        "Failed to fetch records", "Unhandled exception occurred", "Network unreachable",
        "Database connection failed", "Null pointer exception"
    )

    val exceptions = listOf(
        IllegalArgumentException(), NullPointerException(), IllegalStateException(),
        ConcurrentModificationException(), IndexOutOfBoundsException()
    )

    private val scope = SafeCoroutineScope(Dispatchers.Default)

    fun run(): Job {
        return scope.launch {
            while (isActive) {
                randomPrint()
                delay(1.seconds)
            }
        }
    }

    private fun randomPrint() {
        val random = (1..<LogLevel.entries.size).random()
        val level = LogLevel.entries[random]
        val tag = logTags.random()
        when (level) {
            LogLevel.NONE -> return
            LogLevel.VERBOSE -> {
                val message = verboseMessages.random()
                Print.v(tag, message)
            }
            LogLevel.DEBUG -> {
                val message = debugMessages.random()
                Print.d(tag, message)
            }
            LogLevel.INFO -> {
                val message = infoMessages.random()
                Print.i(tag, message)
            }
            LogLevel.WARNING -> {
                val message = warnMessages.random()
                val exception = exceptions.random()
                Print.w(tag, message, exception)
            }
            LogLevel.ERROR -> {
                val message = errorMessages.random()
                val exception = exceptions.random()
                Print.e(tag, message, exception)
            }
            LogLevel.FATAL -> {
                val exception = exceptions.random()
                throw exception
            }
        }
    }

}