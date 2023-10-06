import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] arg) {
        System.out.println("Welcome to RegEx Automa");
        long start=0;
        long time_elapsed=0;

        //REGEX
        RegEx regEx;
        if (arg.length!=0) {
            regEx = new RegEx(arg[0]);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            //check scanner next
            if(!scanner.hasNext()) {
                System.err.println("  >> ERROR: empty regEx.");
                return;
            }
            else{
                regEx = new RegEx(scanner.next());
            }
        }
        System.out.println("  >> Parsing regEx \""+ regEx.getRegEx()+"\".");
        System.out.println("  >> ...");

        RegExTree regexTree = null;
        if (regEx.getRegEx().length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: ["+(int) regEx.getRegEx().charAt(0));
            for (int i = 1; i< regEx.getRegEx().length(); i++) System.out.print(","+(int) regEx.getRegEx().charAt(i));
            System.out.println("].");
            try {
                regexTree = regEx.parse();
                System.out.println("  >> Tree result: "+regexTree.toString()+".");
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+ regEx +"\".");
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");

        NFA ndfa = null;
        if(regexTree != null) {
            System.out.println("  >> Converting to NFA...");
            RegExToNFAConverter converter = new RegExToNFAConverter();
            try{
                ndfa = converter.convert(regexTree);
                System.out.println("  >> NFA: \n"+ndfa.toString()+"\n");

                ndfa = ndfa.rename(ndfa);
                System.out.println("  >> RENAMED NFA: \n"+ndfa.toString()+"\n");

            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+ regEx.getRegEx()+"\".");
            }
            System.out.println("  >> ...");
            System.out.println("  >> NFA completed.");

        }
        if(ndfa!=null){
            System.out.println("  >> Converting to DFA...");
            NFAtoDFAConverter converter = new NFAtoDFAConverter();
            DFA dfa = converter.convert(ndfa);
            System.out.println("  >> DFA: \n"+dfa.toString()+"\n");
            System.out.println("  >> Minimize DFA...");
            dfa = dfa.mergeStates(dfa);
            System.out.println("  >> DFA with merged states: \n"+dfa.toString()+"\n");
            dfa= dfa.deleteUnreachableStates(dfa);
            System.out.println("  >> DFA without unreachable states: \n"+dfa.toString()+"\n");
            System.out.println("  >> ...");
            System.out.println("  >> DFA completed.");

            System.out.println("  >> Converting to DFA...");
            System.out.print("  >> Please enter a file name: ");
            //check scanner next
            String filename = null;
            Scanner scanner = new Scanner(System.in);
            if(!scanner.hasNext()) {
                System.err.println("  >> ERROR: empty string name.");
                return;
            }
            else{
                filename = scanner.next();
                // open filename
                File file = new File(filename);
                if(!file.exists()) {
                    System.err.println("  >> ERROR: file not found.");
                    return;
                }
                else{
                   // read file
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String st;
                        start = System.currentTimeMillis();
                        while ((st = br.readLine()) != null) {
                            System.out.println(dfa.egrepPrintMatchWords(st));
                        }
                        time_elapsed += System.currentTimeMillis()-start;
                    } catch (Exception e) {
                        System.err.println("  >> ERROR: file not found.");
                    }
                }
            }
        }
        System.out.println("Author @RootLeo.");
        System.out.println("Code available at: https://github.com/RootLeo00/gutenberg-app/tree/master/regex-engine");
    }

}
