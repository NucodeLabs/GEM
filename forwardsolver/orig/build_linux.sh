/usr/bin/gcc \
src/VESER.c \
-fPIC \
-shared \
-fvisibility=default \
-I src \
-o \
libves.so