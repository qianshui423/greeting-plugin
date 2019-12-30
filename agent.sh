#!/bin/sh

./gradlew build
pwd
cp -r build/classes/java/main/com/jumao/greeting/test/aop src/main/java/target/classes/com/jumao/greeting/test/aop
cd src/main/java/target/classes
jar -cvfm aopAgent.jar META-INF/MANIFEST.MF com/jumao/greeting/test/aop/*.class
# javac -d target/classes  -cp ../../../asm/asm-7.1.jar -sourcepath com/jumao/greeting/test/aop/*.java com/jumao/greeting/test/aop/AopAgentTest.java
