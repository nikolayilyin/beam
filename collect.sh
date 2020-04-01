#!/bin/bash

function scopy() {
	parentname="$(basename "$(dirname "$1")")"
	filename="$parentname-stopwatch.txt"
	# cp $1 output/$filename
	awk -F'\t' '{print $NF}' $1  > "output/$filename.column.txt"
}

export -f scopy

find output/*/*/stopwatch.txt -exec /bin/bash -c 'scopy "$0"' {} \;

