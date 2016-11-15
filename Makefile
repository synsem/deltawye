# Simple Makefile for building this project on linux systems.

SRCDIR = ./src
DOCDIR = ./doc/api
BINDIR = ./bin
JARFILE = $(BINDIR)/deltawye.jar
TITLE = 'Wye-Delta-Wye Reduction Algorithms'

.PHONY: all help clean doc jar run

all: help

help:
	@echo Wye-Delta-Wye Reduction Algorithms
	@echo
	@echo Prerequisites: Java 8 SE JDK
	@echo
	@echo make doc: Generate API documentation
	@echo make jar: Generate executable JAR file
	@echo make run: Execute JAR file
	@echo make clean: Remove generated files

doc:
	-@mkdir -p $(DOCDIR)
	javadoc -d $(DOCDIR) -subpackages deltawye \
	  -sourcepath $(SRCDIR) -classpath $(SRCDIR) \
	  -header $(TITLE) \
	  -doctitle $(TITLE) \
	  -windowtitle $(TITLE) \
	  -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 \
	  -Xdoclint:all

$(JARFILE):
	-@mkdir -p $(BINDIR)
	javac -d $(BINDIR) -cp $(SRCDIR) $(SRCDIR)/deltawye/app/Main.java
	jar cfe $(JARFILE) deltawye.app.Main -C $(BINDIR) deltawye

jar: $(JARFILE)

run: $(JARFILE)
	java -jar $(JARFILE)

clean:
	-rm -rf $(DOCDIR)
	-rm -rf $(BINDIR)
