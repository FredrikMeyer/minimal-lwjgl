# OpenGL Java Project Guidelines

This document provides guidelines for developers working on the OpenGL Java project.

## Project Structure

The project follows a standard Gradle/Maven structure:

- `app/src/main/java`: Java source code
  - `net.fredrikmeyer.opengl`: Core OpenGL classes
  - `net.fredrikmeyer.opengl.<scene>`: Scene-specific implementations (raymarching, algcurve, etc.)
- `app/src/main/resources`: Shader files and other assets
  - Resources are organized by scene (raymarching, algcurve, etc.)
- `app/src/test/java`: Test code
  - Tests follow the same package structure as the main code

## Tech Stack

- Java 21
- LWJGL 3.3.6 (Lightweight Java Game Library)
- GLFW for window management
- JOML for math operations
- JUnit 5 for testing

## Running the Application

1. The main application class is defined in `build.gradle.kts` (currently `net.fredrikmeyer.opengl.minimal.App`)
2. To run the application:
   ```bash
   ./gradlew run
   ```
3. To run a specific scene, modify the `mainClass` in `build.gradle.kts`

## Running Tests

Run all tests with:
```bash
./gradlew test
```

Test reports are generated in `app/build/reports/tests/test/index.html`

## Scene Development

1. Create a new package under `net.fredrikmeyer.opengl` for your scene
2. Create a corresponding directory in `resources` for shader files
3. Implement the `IScene` interface
4. Create an `App` class that uses your scene

## Best Practices

1. **Code Organization**:
   - Keep scene-specific code in its own package
   - Use the `IScene` interface for new scenes
   - Place shader files in a corresponding resources directory

2. **Testing**:
   - Write unit tests for non-graphical components
   - Use reflection for testing private state when necessary
   - Be aware that testing graphical components requires special handling

3. **Shader Development**:
   - Follow the GLSL version specified in the shader files (#version 330 core)
   - Organize shader files by scene in the resources directory

4. **Input Handling**:
   - Extend the `InputHandler` class for scene-specific input handling
   - Register input callbacks in the scene constructor

5. **Resource Management**:
   - Use the `ResourceLoader` for loading shader files
   - Clean up OpenGL resources in the scene's cleanup method