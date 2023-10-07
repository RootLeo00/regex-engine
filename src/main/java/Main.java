import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] arg) {

        // REGEX
        RegEx regEx = null;
        String filename = null;

        if (arg.length >= 1) {
            // If a command-line argument is provided, use it as the regular expression.
            regEx = new RegEx(arg[1]);
            filename = arg[0];
        }
        else{
            System.err.println("  >> Usage: java -jar regex-engine.jar <regEx> <filename>");
            return;
        }


        if (regEx.getRegEx().length() < 1) {
            System.err.println("  >> ERROR: empty regEx.");
            return;
        }
        RegExTree regexTree = null;
        try {
            // Parse the regular expression and build a syntax tree.
            regexTree = regEx.parse();
        } catch (Exception e) {
            System.err.println("  >> ERROR: syntax error for regEx \"" + regEx.getRegEx() + "\".");
        }

        NFA nfa = null;
        if (regexTree != null) {
            RegExToNFAConverter converter = new RegExToNFAConverter();
            try {
                // Convert the syntax tree to a Non-Deterministic Finite Automaton (NFA).
                nfa = converter.convert(regexTree);

                // Rename the states in the NFA.
                nfa = nfa.rename(nfa);
            } catch (Exception e) {
                System.err.println("  >> ERROR: cannot convert regextree to dfa for regEx \"" + regEx.getRegEx() + "\".");
            }

        }
        if (nfa != null) {
            DFA dfa = null;
            try {
                NFAtoDFAConverter converter = new NFAtoDFAConverter();
                // Convert the NFA to a Deterministic Finite Automaton (DFA).
                dfa = converter.convert(nfa);
                // Merge equivalent states in the DFA.
                dfa = dfa.mergeStates(dfa);
                // Remove unreachable states from the DFA.
                dfa = dfa.deleteUnreachableStates(dfa);
            } catch (Exception e) {
                System.err.println("  >> ERROR: cannot convert nfa to dfa for regEx \"" + regEx.getRegEx() + "\".");
            }

            if (dfa != null) {

                File file = new File(filename);
                if (!file.exists()) {
                    System.err.println("  >> ERROR: file not found.");
                    return;
                } else {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String st;
                        while ((st = br.readLine()) != null) {
                            // Use the DFA to search for matches in the input file and print them.
                            System.out.println(dfa.egrepPrintMatchWords(st));
                        }
                    } catch (Exception e) {
                        System.err.println("  >> ERROR: file not found.");
                    }
                }

            }
        }
        System.out.println("Author @RootLeo, @mcri00.");
        System.out.println("Code available at: https://github.com/RootLeo00/regex-engine");
    }

    public void debug(String[] arg) {
        System.out.println("Welcome to RegEx Automa");

        // REGEX
        RegEx regEx;
        if (arg.length != 0) {
            // If a command-line argument is provided, use it as the regular expression.
            regEx = new RegEx(arg[0]);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            if (!scanner.hasNext()) {
                System.err.println("  >> ERROR: empty regEx.");
                return;
            } else {
                // If no command-line argument is provided, prompt the user to enter a regular expression.
                regEx = new RegEx(scanner.next());
            }
        }
        System.out.println("  >> Parsing regEx \"" + regEx.getRegEx() + "\".");
        System.out.println("  >> ...");

        RegExTree regexTree = null;
        if (regEx.getRegEx().length() < 1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: [" + (int) regEx.getRegEx().charAt(0));
            for (int i = 1; i < regEx.getRegEx().length(); i++)
                System.out.print("," + (int) regEx.getRegEx().charAt(i));
            System.out.println("].");
            try {
                // Parse the regular expression and build a syntax tree.
                regexTree = regEx.parse();
                System.out.println("  >> Tree result: " + regexTree.toString() + ".");
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");

        NFA nfa = null;
        if (regexTree != null) {
            System.out.println("  >> Converting to NFA...");
            RegExToNFAConverter converter = new RegExToNFAConverter();
            try {
                // Convert the syntax tree to a Non-Deterministic Finite Automaton (NFA).
                nfa = converter.convert(regexTree);
                System.out.println("  >> NFA: \n" + nfa.toString() + "\n");

                // Rename the states in the NFA.
                nfa = nfa.rename(nfa);
                System.out.println("  >> RENAMED NFA: \n" + nfa.toString() + "\n");

            } catch (Exception e) {
                System.err.println("  >> ERROR: cannot convert regextree to dfa for regEx \"" + regEx.getRegEx() + "\".");
            }
            System.out.println("  >> ...");
            System.out.println("  >> NFA completed.");

        }
        if (nfa != null) {
            System.out.println("  >> Converting to DFA...");
            DFA dfa = null;
            try {

                NFAtoDFAConverter converter = new NFAtoDFAConverter();
                // Convert the NFA to a Deterministic Finite Automaton (DFA).
                dfa = converter.convert(nfa);
                System.out.println("  >> DFA: \n" + dfa.toString() + "\n");
                System.out.println("  >> Minimize DFA...");
                // Merge equivalent states in the DFA.
                dfa = dfa.mergeStates(dfa);
                System.out.println("  >> DFA with merged states: \n" + dfa.toString() + "\n");
                // Remove unreachable states from the DFA.
                dfa = dfa.deleteUnreachableStates(dfa);
                System.out.println("  >> DFA without unreachable states: \n" + dfa.toString() + "\n");
            } catch (Exception e) {
                System.err.println("  >> ERROR: cannot convert nfa to dfa for regEx \"" + regEx.getRegEx() + "\".");
            }
            System.out.println("  >> ...");
            System.out.println("  >> DFA completed.");

            if (dfa != null) {
                System.out.println("  >> Let's use our automa!");
                System.out.print("  >> Please enter a file name: ");
                String filename;
                Scanner scanner = new Scanner(System.in);
                if (!scanner.hasNext()) {
                    System.err.println("  >> ERROR: empty string name.");
                    return;
                } else {
                    filename = scanner.next();
                    File file = new File(filename);
                    if (!file.exists()) {
                        System.err.println("  >> ERROR: file not found.");
                        return;
                    } else {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String st;
                            while ((st = br.readLine()) != null) {
                                // Use the DFA to search for matches in the input file and print them.
                                System.out.println(dfa.egrepPrintMatchWords(st));
                            }
                        } catch (Exception e) {
                            System.err.println("  >> ERROR: file not found.");
                        }
                    }
                }
            }
        }
        System.out.println("Author @RootLeo, @mcri00.");
        System.out.println("Code available at: https://github.com/RootLeo00/regex-engine");
    }
}
