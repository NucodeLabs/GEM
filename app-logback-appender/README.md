# Application Log File Logback Appender

Extended logback FileAppender that resolves OS dependent logs directory for application
and writes logs to file in this directory.

## Logs Location

- **on Linux:** `~/.config/{appName}/logs/application.log`
- **on macOS:** `~/Library/Logs/{appName}/application.log`
- **on Windows:** `%USERPROFILE%\AppData\Roaming\{appName}\logs\application.log`

## Configuration

Note that: property `appName` is required

Default log file name is `application.log`.
You can override it by setting `logFileName` property.

```xml

<appender name="APP_FILE" class="ru.nucodelabs.logback.appender.ApplicationLogFileAppender">
    <appName>ru.nucodelabs.gem</appName>
    <logFileName>debug.log</logFileName>
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```