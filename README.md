# GEM

GEM is a study platform for geologists and geophysicist.

# Building JNI(C/C++) modules

## Dependencies

### All platforms

Install **OpenJDK 17.0.1**

### Windows

- [clang](https://github.com/llvm/llvm-project/releases)
    - Recommended to leave default installation location `C:\Program Files\LLVM`
    - Recommended to check set `PATH` variables while installation
- [Visual Studio](https://visualstudio.microsoft.com/downloads/)
    - Need only **MSVC** and **Windows SDK** components

## How to build

If you have Gradle installed and setup `PATH` variable

```shell
$ gradle forwardSolver
$ gradle misfitFunctions
```

Or using IntelliJ IDEA run `forwardSolver`, `misfitFunctions`, etc tasks in `build.gradle`
