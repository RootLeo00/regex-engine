#!/bin/bash

# Check if at least three arguments are provided
if [ $# -lt 3 ]; then
    echo "Usage: $0 <regex_engine> <input_filename> <regex> [test]"
    echo "Available regex engines:"
    echo "1 - automa"
    echo "2 - kmp"
    echo "3 - radixtree"
    exit 1
fi

# Determine which regex engine to use based on the first argument
case $1 in
    "automa" | "kmp" | "radixtree")
        regex_engine="$1"
        ;;
    *)
        echo "Invalid regex engine: $1"
        echo "Available regex engines: automa, kmp, radixtree"
        exit 1
        ;;
esac

input_filename="$2"

# Check if input file exists
if [ ! -f "$input_filename" ]; then
    echo "Input file does not exist: $input_filename"
    exit 1
fi

regex="$3"

is_test=false

# Check if is_test argument is provided (default to empty string if not)
if [ $# -eq 4 ]; then
    if $4 == "test"; then is_test=true
    else
        echo "Invalid argument: $4"
        echo "Usage: $0 <regex_engine> <input_filename> <regex> [test]"
        exit 1
    fi
else
    is_test=false
fi


# run the regex engine
case $regex_engine in
    "automa")
        if [ $is_test = true ]; then
            chmod +x ./out/artifacts/regex_engine_jar/regex-engine.jar
            java -jar "./out/artifacts/regex_engine_jar/regex-engine.jar" "$input_filename" "$regex" "test"
        else
            chmod +x ./out/artifacts/regex_engine_jar/regex-engine.jar
            java -jar "./out/artifacts/regex_engine_jar/regex-engine.jar" "$input_filename" "$regex"
        fi
        ;;
    "kmp")
        if [ $is_test = true ]; then
            chmod +x ./kmp/kmp.py
            python3 ./kmp/kmp.py "$input_filename" "$regex" "test"
        else
            chmod +x ./kmp/kmp.py
            python3 ./kmp/kmp.py "$input_filename" "$regex"
        fi
        ;;
    "radixtree")
        if [ $is_test = true  ]; then
            chmod +x ./radixtree/radixtree.py
            python3 ./radixtree/radixtree.py "$input_filename" "$regex" "test"
        else
            chmod +x ./radixtree/radixtree.py
            python3 ./radixtree/radixtree.py "$input_filename" "$regex"
        fi
        ;;
esac