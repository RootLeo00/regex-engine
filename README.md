# Cloning egrep command supporting simplified ERE.

# Introduction

## What is a RegEx
Regular Expressions (RegEx) and ERE specifications : A regular expression is the formalisation of an algebraic language that is acceptable by a deterministic finite automaton (DFA), in the sense of formal language theory. We will only address regular expressions defined by the ERE specifications given in the following link, that we also refer to as RegEx : http://pubs.opengroup.org/onlinepubs/7908799/xbd/re.html

## Pattern recognition by RegEx
Given a text file with l lines, the problem of finding a RegEx in the file is usually decomposed into l times the search for the RegEx in every different line. The general goal of this project is to implement
a solution for such a problem. Several strategies can be taken into account. In this project we refer to these ones:
1. AUTOMA
   
   If the RegEx is not reduced to a series of concatenations, 
   1. we first transform it into a syntax tree  
   2. then into a non-deterministic finite automaton (NDFA) with epsilon-transitions according to Aho-Ullman algorithm 
   3. then into a deterministic finite automaton (DFA) with the subset method 
   4. then into an equivalent automaton with a minimum number of states (4) 
   5. and finally the automaton is used to test whether a suffix of a line of the input text is recognizable by this automaton

2. KMP
   
   If the RegEx is reduced to a series of concatenations, our problem is reduced to substring recognition : Knuth-Morris-Pratt (KMP) algorithm can then be used instead of the above described war machine.

3. RADIX TREE
   
   This is specific to the case when the RegEx is not only reduced to a series of concatenations, but also composed exclusively of alphabetic characters. We assume here that : (forward indexing) the input text file has been pre-processed in order to produce a cache-file containing a table of indices representing the list of all the useful words (in human language) appearing in the text file, these are also called “tokens” ; (inverted indexing) furthermore, the cache-file containing all tokens can be represented by optimised search structures such as a radix tree containing all these words. Then, finding the RegEx is performed by browsing the index table and/or the radix tree.

## Prerequisites

Before you can run the program, ensure that you have the following prerequisites installed on your system:

- Python 3.x
- Required Python packages (install using `pip`):
  - colorama
  - pandas

## Getting Started

You can use the program with different search engines (automata, KMP, Radix Tree) and perform searches on various text inputs. Here's how to use the program:

1. Clone or download this repository to your local machine.

    ```bash
    git clone https://github.com/RootLeo00/regex-engine.git
   ```

2. Run the program:

    ```bash
    ./launch-egrep.sh <regex_engine> <input_filename> [is_test]
    ```

    - ```<regex_engine>```: Choose the search engine:
        automa for Automaton-based search
        kmp for Knuth-Morris-Pratt search
        radixtree for Radix Tree search

    - ```<input_filename>```: Specify the input file containing the text data you want to search within.

    - ```[is_test]``` (Optional): Use this flag to run test scenarios. If specified as "test," the program will perform a series of tests and measure execution times for various input lengths and pattern lengths.
  
# Examples

To perform a search using the Automaton-based search engine:

```bash
./launch-egrep.sh "automa" "path_to_input.txt" "randomword"
```

To run the program with the KMP search engine and perform tests:

```bash
./launch-egrep.sh "kmp" "path_to_input.txt" "randomword" "test"
```

The program will display the matching results in the terminal and, eventually, save the test results to output files located in the "output" directory.


# Additional features

### Plot graphs
You can use this script to generate timing data plots for different search engines and input lengths. It is supposed to be used after you perform the tests, which generates the timing data in pickle or csv format under the ```output``` folder. It saves the plot in .png format into th Here's how to use the script:

```bash
python ./graphs/plot_timing.py
```
The script expects input files in either CSV or pickle format. Of course, it is easy to twick the code in order to accept other formats (it's just Python ...). 
You can specify the input files in the script itself by modifying the file variable in the if ```__name__ == "__main__"```: section. Each file should contain timing data for a specific search engine and input length.


# Files details
```
├──  graphs  
│    └── plot_timing.py  - program to draw timing data plots
│    └── finalgraphs  - good graphs from good tests
|
├──  input   
│    └── *.txt - different text file as input text
│    └── ... 
|
├──  jar   
│    └── main - where I put the regex-engine with the Main class as main
│    └── test - where I put the regex-engine with the Test class as main
|
├──  kmp   
│    └── kmp.py - program to run KMP search
|
├──  radixtree   
│    └── radixtree.py - program to run RadixTree search
|
├──  output   
│    └── kmp.py - program to run KMP search
│    └── finaltests  - good tests outputs
|
├──  lib - folder with library used by Java automata
|
├──  src - folder with the Java project for the Automa search engine
│
├──  launch-egrep.sh - file to launch the main program that selects the different engines
```