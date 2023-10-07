import com.opencsv.CSVWriter;

import java.io.*;

public class Test {

    public static void main(String[] args) throws IOException {
        System.out.println("TEST AUTOMA");
        test_textlength(args);
        test_patternlength(args);

    }
    public static void test_textlength(String[] args) throws IOException {
        //initialize result
        System.out.println("Test with different number of characters in the text, from 1000 to 100000");
        String filename= "/home/leo/github/gutenberg-app/regex-engine/input/book-about-babylone.txt";
        // open filename
        File file = new File(filename);
        if(!file.exists()) {
            System.err.println("  >> ERROR: file not found.");
            return;
        }

        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(new File("output/output_automa_textlength.csv"));

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        String[] header = { "ncharacters", "pattern_len", "time_elapsed" };
        writer.writeNext(header);

        long time_elapsed = 0;
        RegEx regEx = new RegEx("Babylon");
        // test with different number of characters in the text
        for (int nCharacters = 1000; nCharacters <= 100000; nCharacters += 1000) {
                //create dfa

            try{
                RegExTree regexTree = null;
                regexTree = regEx.parse();
                RegExToNFAConverter converter = new RegExToNFAConverter();
                NFA ndfa = null;
                ndfa = converter.convert(regexTree);
                ndfa = ndfa.rename(ndfa);
                DFA dfa = new NFAtoDFAConverter().convert(ndfa);

                // test with different pattern length
                long start = 0;

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

                } catch (Exception e) {
                    System.err.println("  >> ERROR: syntax error for regEx \""+ regEx +"\".");
                }

            // add data to csv
            String[] res = {String.valueOf(nCharacters), String.valueOf(regEx.getRegEx().length()), String.valueOf(time_elapsed/1000)};
            writer.writeNext(res);
        }

        // closing writer connection
        writer.close();
    }
    public static void test_patternlength(String[] args) throws IOException {
        System.out.println("Test with different number of characters in the pattern, from 5 to 100");
        String filename= "/home/leo/github/gutenberg-app/regex-engine/input/book-about-babylone.txt";
        // open filename
        File file = new File(filename);
        if(!file.exists()) {
            System.err.println("  >> ERROR: file not found.");
            return;
        }
        File text_to_process = readFirstNCharactersFromFile(file, 100000, false, "output/text_to_process.txt");
        File pattern_to_process = readFirstNCharactersFromFile(file, 5000, true, "output/pattern_to_process.txt");


        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(new File("output/output_automa_patternlength.csv"));

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        String[] header = { "ncharacters", "pattern_len", "time_elapsed" };
        writer.writeNext(header);

        long time_elapsed = 0;

        // test with different number of characters in the text
        for (int pattern_len = 5; pattern_len <= 1000; pattern_len += 10) {
            //take the first pattern_len character of the file
            String r=readFirstNCharactersFromFile("output/pattern_to_process.txt", pattern_len);

            RegEx regEx = new RegEx(r);
            try{
                RegExTree regexTree = null;
                regexTree = regEx.parse();
                RegExToNFAConverter converter = new RegExToNFAConverter();
                NFA ndfa = null;
                ndfa = converter.convert(regexTree);
                ndfa = ndfa.rename(ndfa);
                DFA dfa = new NFAtoDFAConverter().convert(ndfa);

                // test with different pattern length
                long start = 0;

                try {
                    BufferedReader br = new BufferedReader(new FileReader(text_to_process));
                    String st;
                    start = System.currentTimeMillis();
                    while ((st = br.readLine()) != null) {
                        System.out.println(dfa.egrepPrintMatchWords(st));
                    }
                    time_elapsed += System.currentTimeMillis()-start;
                } catch (Exception e) {
                    System.err.println("  >> ERROR: file not found.");
                }

            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+ regEx +"\".");
            }

            double time_elapsed_sec = (double)time_elapsed/1000;
            // add data to csv
            String[] res = {String.valueOf(file.length()), String.valueOf(regEx.getRegEx().length()), String.valueOf(time_elapsed_sec)};
            System.out.println("LENGTH PATT "+regEx.getRegEx().length());
            writer.writeNext(res);
        }

        //delete temp files
        text_to_process.delete();
        pattern_to_process.delete();
        // closing writer connection
        writer.close();
        outputfile.close();
    }

    public static char[] removeSpecialCharacters(char[] input) {
        int newIndex = 0; // Index for the modified array without special characters

        for (int i = 0; i < input.length; i++) {
            char currentChar = input[i];

            // Check if the character is an alphabetic character (A-Za-z)
            if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z') || currentChar == ' ') {
                input[newIndex] = currentChar; // Move the valid character to the new position
                newIndex++;
            }
        }

        // Create a new array of the correct length and copy the valid characters
        char[] result = new char[newIndex];
        System.arraycopy(input, 0, result, 0, newIndex);

        return result;
    }

    public static File readFirstNCharactersFromFile(File inputFile, int n, boolean remove_special_char, String output_filename) throws IOException {
        FileReader fileReader = new FileReader(inputFile);
        char[] buffer = new char[n]; // Buffer to store the first n characters
        int charsRead = fileReader.read(buffer);
        if (remove_special_char) buffer=removeSpecialCharacters(buffer);

        if (charsRead == -1) {
            // The file is empty
            throw new IOException("The file is empty");
        }

        File outputFile = new File(output_filename); // Change the file name as needed
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(buffer, 0, buffer.length);
        }

        return outputFile;
    }

    public static String readFirstNCharactersFromFile(String filename, int n) throws IOException {
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            char[] buffer = new char[4096];
            int charsRead;
            int totalCharsRead = 0;

            while ((charsRead = br.read(buffer, 0, Math.min(buffer.length, n - totalCharsRead))) != -1) {
                result.append(buffer, 0, charsRead);
                totalCharsRead += charsRead;

                if (totalCharsRead >= n) {
                    break;
                }
            }
        }

        return result.toString();
    }

}
