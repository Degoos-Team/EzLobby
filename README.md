# Hytale Kotlin Plugin Template

A template project for creating Hytale plugins using Kotlin and Gradle.

## Prerequisites

- Java 23 (configured via toolchain)
- Gradle 8.14 or higher
- Hytale Server installation

## Setup

1. Clone this repository
2. Create a `gradle.properties` file in the project root with the following properties:
   ```properties
   hytale.path=/path/to/your/hytale/installation
   hytale.server=/path/to/your/server/directory
   ```
    - `hytale.path`: Path to your Hytale installation directory
    - `hytale.server`: Path to your server directory where plugins should be deployed

## Project Structure

- `src/main/kotlin`: Kotlin source files for your plugin
- `build.gradle.kts`: Gradle build configuration
- `settings.gradle.kts`: Gradle settings

## Gradle Tasks

### shadowJar

Builds a fat JAR (uber-JAR) containing your plugin and all its dependencies. The resulting JAR is minimized to exclude
unused classes and the Kotlin standard library (which is provided by the Hytale server).

### deployPlugin

Executes the 'shadowJar' tasks and copies the result into the defined server.


