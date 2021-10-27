# How to build forwardsolver dynamic library for JNI

## `Windows | Linux | macOS (x86_64)`

1. Install **Visual Studio Code**.
2. Install **OpenJDK 17.0.1** (via **IntelliJ IDEA** or from download from site).
3. On Windows: Install LLVM `clang` compiler.
4. In **VSCode** menu: **_File_** - **_Open Folder_** - Choose `ForwardSolver` folder.
5. Open `.vscode/settings.json` and set `java.home` to path to installed JDK home folder.

```json
// example for macOS
"java.home": "~/Library/Java/JavaVirtualMachines/openjdk-17.0.1/Contents/Home",
// example for Windows
"java.home": "C:\\Users\\%username%\\.jdks\\openjdk-17.0.1",
// example for Linux
"java.home": "/usr/lib/jvm/java-17-openjdk-amd64"
```

Note that Windows have `"\\"` file path separator.

5. Press `Shift + Ctrl(Cmd) + B` or in menu: **Terminal** - **Run Build Task**.
6. Choose `forwardsolver (JNI)` task.
7. Library will appear in `build/lib`.

### Naming conventions

```
Windows: forwardsolver.dll
Linux: libforwardsolver.so
macOS: libforwardsolver.dylib
```