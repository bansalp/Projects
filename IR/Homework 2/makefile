JFLAGS = -cp
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) :./PageRank/src/ $*.java

CLASSES = \
	./PageRank/src/PageRankApp.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) ./PageRank/src/*.class
