/usr/bin/clang \
src/VESER.c \
-O3 \
-x \
c \
-arch \
x86_64 \
-fPIC \
-dynamiclib \
-fvisibility=default \
-I \
src \
-install_name libves.dylib \
-o \
libves.dylib