
JARFILE = tfcat.jar
JSON_JAR = json.jar
UCIDY_JAR = ucidy-1.2-beta.jar
UNITY_JAR = unity-1.0.jar
JLIBS = $(JSON_JAR) $(UCIDY_JAR) $(UNITY_JAR)
CLASSPATH = $(JSON_JAR):$(UCIDY_JAR):$(UNITY_JAR)
JAVAC = javac
JAVADOC = javadoc -Xdoclint:all,-missing
JAR = jar
MAIN_CLASS = TfcatParser

JSRC = \
       BasicReporter.java \
       Bbox.java \
       Crs.java \
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
       RefPosition.java \
       Reporter.java \
       SpectralCoords.java \
       TfcatObject.java \
       TfcatParser.java \
       TimeCoords.java \

build: jar javadocs

jar: $(JARFILE)

javadocs: $(JSRC) $(JLIBS)
	rm -rf javadocs
	mkdir javadocs
	$(JAVADOC) -classpath $(CLASSPATH) -quiet -d javadocs $(JSRC)

test: build
	java -classpath $(JARFILE):$(CLASSPATH) $(MAIN_CLASS) example.tfcat
	java -classpath $(JARFILE):$(CLASSPATH) $(MAIN_CLASS) jupiter-obs.tfcat
	java -classpath $(JARFILE):$(CLASSPATH) $(MAIN_CLASS) doc-example.tfcat

playtest: build
	java -classpath $(JARFILE):$(CLASSPATH) $(MAIN_CLASS) play.tfcat

$(JARFILE): $(JSRC) $(JLIBS)
	rm -rf tmp
	mkdir -p tmp
	$(JAVAC) -classpath $(CLASSPATH) -Xlint:unchecked -d tmp $(JSRC) \
           && $(JAR) cfe $@ $(MAIN_CLASS) -C tmp .
	rm -rf tmp

$(JSON_JAR):
	cp /mbt/starjava/source/feather/src/lib/$@ ./

$(UNITY_JAR) $(UCIDY_JAR):
	cp /mbt/starjava/source/ttools/src/lib/$@ ./

clean:
	rm -rf $(JARFILE) $(JLIBS) tmp javadocs

