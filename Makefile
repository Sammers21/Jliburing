CCFLAGS ?= -Wall -O2 -D_GNU_SOURCE -luring -pthread
all_targets = liburing build/jni

.PHONY: liburing test compileJNI

all: $(all_targets)

clean:
	rm -rf $(all_targets)

liburing:
	mkdir -p build 
	cd build && \
	if [ -d "./liburing" ]; \
	then \
		echo "liburing dir exist" ;\
	else \
		git clone https://github.com/axboe/liburing.git \
		&& cd liburing \
		&& git checkout liburing-0.7; \
	fi
	+$(MAKE) -C ./build/liburing;

compileJNI: liburing
	mkdir -p build/jni
	$(CC) -c -fPIC \
		-I${JAVA_HOME}/include \
		-I${JAVA_HOME}/include/linux \
		-I./build/liburing/src/include/ \
		-L./build/liburing/src/ \
		src/jni/Lib.cpp -o build/jni/Lib.o \
		${CCFLAGS}
	$(CC) -shared -fPIC \
	 	-I./build/liburing/src/include/ \
		-L./build/liburing/src/ \
		-o build/jni/liblb.so build/jni/Lib.o \
		-lc  ${CCFLAGS}