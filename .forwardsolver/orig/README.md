# Building dymamic linked library VES

**All of the scripts must be executed from folders in which they're located**
## macOS (OSX)

On either Apple Silion or 64-bit Intel Mac run this to build x86_64 dynamic library. It's supported by arm64 Macs too.

```
sh build_macos.sh
```

Output file is `libves.dylib`, located in root of this folder.

## Linux

Run on amd64(x86_64) Linux machine.

```
sh build_linux.sh
```

Output file is `libves.so`, located in root of this folder.

## Windows

Run Developer Command Line (x64) shipped with Visual Studio and then run this in it.

```
build_windows.bat
```

# All - Visual Studio Code Build Tasks

You can build **ves** library on any system using VS Code.

- Open `forwardsolver` folder in VS Code
- Press `Ctrl + Shift + B` (or **Terminal - Run build task**) and select `ves`.