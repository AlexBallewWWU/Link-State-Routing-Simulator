CLASSPATH = .
TESTCLASSPATH = $(CLASSPATH):Test/:Test/junit4.jar
JFLAGS = -g -cp $(TESTCLASSPATH)
.SUFFIXES: .java .class
.java.class:
	javac $(JFLAGS) $*.java

TESTSRC = Test/TestRunner.java Test/LinkStateTest.java

SRC = \
	Advertisement.java \
	KeepAlive.java \
	Link.java \
	Message.java \
	Network.java \
	Node.java

default: classes

classes: $(SRC:.java=.class)

clean:
	rm -f *.class Test/*.class

run: classes
	java -cp $(CLASSPATH) Network

debug: classes
	jdb -classpath $(CLASSPATH) Network

test-build: classes Test/TestRunner.class Test/LinkStateTest.class

test-single: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testSingle

test-three: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testThree

test-single-ad-state: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testSingleAdState

test-double-hop-ad-state: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testDoubleHopAdState

test-multi-hop-ad-state: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testMultiHopAdState

test-going-offline: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testGoingOffline

test-coming-back-online: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testComingBackOnline

test-poisoning: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest#testPoisoning

test-style:
	java -cp JavaStyle/checkstyle-8.24-all.jar:. com.puppycrawl.tools.checkstyle.Main -c JavaStyle/google_checks.xml *.java

test: test-build
	java -cp $(TESTCLASSPATH) TestRunner LinkStateTest

debug-test: test-build
	jdb -classpath $(TESTCLASSPATH) TestRunner LinkStateTest
