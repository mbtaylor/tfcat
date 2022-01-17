
JARFILE = tfcat.jar
JSON_JAR = json.jar
JAVAC = javac
JAR = jar
CLASSPATH = $(JSON_JAR)
MAIN_CLASS = TfcatParser

JSRC = \
       BasicReporter.java \
       Bbox.java \
       Datatype.java \
       Decoder.java \
       Decoders.java \
       Feature.java \
       FeatureCollection.java \
       Field.java \
       Geometry.java \
       JsonTool.java \
       LinearRing.java \
       Position.java \
       Reporter.java \
       TfcatObject.java \
       TfcatParser.java \

build: jar

jar: $(JARFILE)

test: build
	java -classpath $(JARFILE):$(JSON_JAR) $(MAIN_CLASS) example.tfcat
	java -classpath $(JARFILE):$(JSON_JAR) $(MAIN_CLASS) jupiter-obs.tfcat
	java -classpath $(JARFILE):$(JSON_JAR) $(MAIN_CLASS) doc-example.tfcat

$(JARFILE): $(JSRC) $(JSON_JAR)
	rm -rf tmp
	mkdir -p tmp
	$(JAVAC) -classpath $(JSON_JAR) -Xlint:unchecked -d tmp $(JSRC) \
           && $(JAR) cfe $@ $(MAIN_CLASS) -C tmp .
	rm -rf tmp

$(JSON_JAR):
	cp /mbt/starjava/source/feather/src/lib/$@ ./

clean:
	rm -rf $(JARFILE) $(JSON_JAR) tmp

