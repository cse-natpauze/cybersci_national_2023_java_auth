
# jdk install location
JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"

#make java class
echo "compiling java class"
javac Hello.java
# make .h header
echo "making c header"
javac -h . Hello.java
# compile c code, linking against jni libs 
echo "compilling native lib"
gcc -lc -shared -I$JAVA_HOME/include -I$JAVA_HOME/include/linux hello.c -o libHelloImpl.so
# link into a native lib
# gcc -shared -o libnative.so hello.o