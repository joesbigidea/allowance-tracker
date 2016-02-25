import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY

if ("true".equalsIgnoreCase(System.getProperty("DEVELOPMENT"))) {
    appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%level] [%thread] %logger{10} %msg%n"
        }
    }
    root(DEBUG, ["STDOUT"])
}
else {
    appender("INFO", RollingFileAppender) {
        file = "logs/info.log"
        filter(LevelFilter) {
            level = DEBUG
            onMatch = DENY
            onMismatch = ACCEPT
        }
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "info-%d{yyyy-MM-dd}.%i.log"
            maxHistory = 5
            timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
                maxFileSize = "10MB"
            }
        }
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%level] [%thread] %logger{10} %msg%n"
        }
    }

    appender("DEBUG", RollingFileAppender) {
        file = "logs/debug.log"
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "debug-%d{yyyy-MM-dd}.%i.log"
            maxHistory = 2
            timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
                maxFileSize = "10MB"
            }
        }
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%level] [%thread] %logger{10} %msg%n"
        }
    }

    appender("ACCESS", RollingFileAppender) {
        file = "logs/access.log"
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "accesss-%d{yyyy-MM-dd}.%i.log"
            maxHistory = 5
            timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
                maxFileSize = "10MB"
            }
        }
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] %msg%n"
        }
    }

    logger("access", INFO, ["ACCESS"], false)
    root(DEBUG, ["INFO", "DEBUG"])
}