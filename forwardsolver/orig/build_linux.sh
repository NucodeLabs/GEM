/usr/bin/gcc \
src/VESER.c \
-O3 \
-x \
c \
-fPIC \
-shared \
-fvisibility=default \
-I \
src \
-o \
libves.so