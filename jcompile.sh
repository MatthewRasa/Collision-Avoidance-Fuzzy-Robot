#!/bin/bash
javac -source 1.7 -target 1.7 -d . gui_src/*.java
jar -cf gui.jar gui/*.class
rm -rf gui
