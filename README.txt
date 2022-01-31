Contained in this folder:

                 src/ : Source code for the project
                        (main entry point is src/VisualAssembly.java)
         src/Makefile : To build with 'make' on linux
      src/Manual.html : A short HTML manual for the program, describing the
                        machine, interface and instructions.
               demos/ : Folder containing demo assemblies
                        (can be opened with File -> Open)
 majorproject-UML.odg : UML diagram in the original OpenOffice format
 majorproject-UML.png : UML diagram in PNG format
   ProjectOutline.pdf : Outline document describing the project.

The main class is VisualAssembly. I believe with the Oracle JRE javafx is
included as part of the default classpath, so you should be able to build
and launch it with:

    javac VisualAssembly.java
    java VisualAssembly

With OpenJRE you will need to specify where the javafx libraries are,
see the Makefile for commands that work with Ubuntu and Arch Linux.
