JFLAGS = -cp
JC = javac
JARFILE = "./WebCrawler/lib/jsoup-1.8.3.jar"

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $(JARFILE):./WebCrawler/src/ $*.java

CLASSES = \
	./WebCrawler/src/LinkNode.java \
	./WebCrawler/src/WebCrawlerApp.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) ./WebCrawler/src/*.class
