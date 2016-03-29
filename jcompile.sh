#!/bin/bash
src_dir="jsrc"
bin_dir="jbin"
jar_dir="jars"

if [ "$1" == "clean" ]; then
	rm -rf "$bin_dir" "$jar_dir"
	exit
fi

mkdir -p "$jar_dir"
for target in "$src_dir"/*; do
	tname=$(basename "$target")
	mkdir -p "$bin_dir/$tname"
	javac -source 1.7 -target 1.7 -d "$bin_dir/$tname" "$target"/*.java
	jar -cf "$tname".jar -C "$bin_dir/$tname" "."
done
mv *.jar "$jar_dir"
rm -rf "$bin_dir"
