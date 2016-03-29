#!/bin/bash
javac -source 1.7 -target 1.7 -d . gui_src/*.java
jar -cf gui.jar gui/*.class
rm -rf gui

javac -source 1.7 -target 1.7 -d . path_src/com/kevinbohinski/CSC47002/PathGenerator.java
jar -cf paths.jar com/kevinbohinski/CSC47002/*.class
rm -rf com
