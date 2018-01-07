import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.DEBUG

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%level] [%thread] %logger{10} %msg%n"
    }
}
root(DEBUG, ["STDOUT"])
