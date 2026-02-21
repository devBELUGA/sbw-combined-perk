Build and release pipeline update.

Added:

- Gradle task `publishToModrinth` for Modrinth publishing
- interactive publish confirmation in console: `Y` to publish, `N` to cancel
- local secrets support via `secret.gradle` (ignored by git)
- committed template `secret.gradle.placeholder`
- updated GitHub workflows to Java 21
- dependency graph submission enabled in workflows
