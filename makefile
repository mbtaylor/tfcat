
JARFILE = tfcat.jar
JSON_JAR = json.jar
JAVAC = javac
JAR = jar
CLASSPATH = $(JSON_JAR)

JSRC = \
       Bbox.java \
       Level.java \
       Point.java \
       Position.java \
       Report.java \
       TfcatFactory.java \
       TfcatObject.java \
       TfcatParser.java \

build: jar

jar: $(JARFILE)

$(JARFILE): $(JSRC) $(JSON_JAR)
	rm -rf tmp
	mkdir -p tmp
	$(JAVAC) -classpath $(JSON_JAR) -Xlint:unchecked -d tmp $(JSRC) \
           && $(JAR) cf $@ -C tmp .
	rm -rf tmp

$(JSON_JAR):
	cp /mbt/starjava/source/feather/src/lib/$@ ./

clean:
	rm -rf $(JARFILE) $(JSON_JAR) tmp

