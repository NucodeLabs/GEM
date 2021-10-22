/usr/bin/clang \
src/VESER.c \
-arch x86_64 \
-fPIC \
-shared \
-fvisibility=default \
-I src \
-o \
../src/main/lib/libves.dylib