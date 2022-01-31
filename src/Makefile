# arguments for ubuntu
#JARGS = --module-path /usr/lib/jvm/java-11-openjdk/lib --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web
#JARGS = --module-path /usr/share/openjfx/lib --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web
# arguments for arch linux
JARGS = --module-path /usr/lib/jvm/java-17-openjdk/lib/javafx.base.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.controls.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.fxml.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.graphics.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.media.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.swing.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx-swt.jar:/usr/lib/jvm/java-17-openjdk/lib/javafx.web.jar --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

#RUNARGS = 

RUNARGS = -Dglass.gtk.uiScale=200%  \
          -Dglass.gtk.uiScale=192dpi \
          -Dglass.gtk.uiScale=2.0   

%.class: %.java
	javac -Xlint:unchecked ${JARGS} $<

.PHONY: test-VisualAssembly
test-VisualAssembly: VisualAssembly.class
	java ${JARGS} ${RUNARGS} VisualAssembly

.PHONY: clean
clean:
	rm -f *.class x.txt
