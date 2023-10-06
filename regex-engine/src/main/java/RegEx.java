import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Exception;

public class RegEx {
  private String regEx;
  public RegEx(String regEx){
    this.regEx = regEx;
  }
    public String getRegEx() {
        return regEx;
    }
    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

  //FROM REGEX TO SYNTAX TREE
  public RegExTree parse() throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    for (int i=0;i<regEx.length();i++) result.add(new RegExTree(charToRoot(regEx.charAt(i)),new ArrayList<RegExTree>()));
    
    return parse(result);
  }
  private static int charToRoot(char c) {
    if (c=='.') return RegExTokenType.DOT.getValue();
    if (c=='*') return RegExTokenType.STAR.getValue();
    if (c=='+') return RegExTokenType.PLUS.getValue();
    if (c=='|') return RegExTokenType.ALTERN.getValue();
    if (c=='(') return RegExTokenType.PARENTHESEOUVRANT.getValue();
    if (c==')') return RegExTokenType.PARENTHESEFERMANT.getValue();
    return (int)c;
  }
  private RegExTree parse(ArrayList<RegExTree> result) throws Exception {
    while (containParenthese(result)) result=processParenthese(result);
    while (containEtoile(result)) result=processEtoile(result);
    while (containPlus(result)) result=processPlus(result);
    while (containConcat(result)) result=processConcat(result);
    while (containAltern(result)) result=processAltern(result);

    if (result.size()>1) throw new Exception();

    return removeProtection(result.get(0));
  }
  private boolean containParenthese(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==RegExTokenType.DOT.getValue() || t.root==RegExTokenType.PARENTHESEOUVRANT.getValue()) return true;
    return false;
  }
  private ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false; //boolean variable to track whether the closing parenthesis has been found

    for (RegExTree t: trees) {
        // Check if the closing parenthesis is found and no other closing parenthesis has been found yet
        if (!found && t.root == RegExTokenType.PARENTHESEFERMANT.getValue()) {
            boolean done = false;
            ArrayList<RegExTree> content = new ArrayList<RegExTree>(); //new ArrayList to store the content within the parentheses

            // Process the content within the parentheses
            while (!done && !result.isEmpty()) {
                if (result.get(result.size()-1).root == RegExTokenType.PARENTHESEOUVRANT.getValue()) {
                    // If an opening parenthesis is found, set 'done' to true and remove it from the result
                    done = true;
                    result.remove(result.size()-1);
                } else {
                    // Otherwise, add the content to the 'content' ArrayList and remove it from the result
                    content.add(0, result.remove(result.size()-1));
                }
            }

            // If 'done' is still false, it means there was no matching opening parenthesis
            if (!done) throw new Exception();

            // If exeption wan not raised, mark that the closing parenthesis has been found
            found = true;

            // Create an ArrayList of subTrees by parsing the 'content' ArrayList
            ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
            subTrees.add(parse(content));

            // Add a new RegExTree with the root as 'PROTECTION' and the subTrees to the result
            result.add(new RegExTree(RegExTokenType.PROTECTION.getValue(), subTrees));
        } else {
            // If the current RegExTree is not a closing parenthesis, simply add it to the result
            result.add(t);
        }
    }

    // If no closing parenthesis was found, throw an exception
    if (!found) throw new Exception();

    // Return the result ArrayList
    return result;
}

  private boolean containEtoile(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==RegExTokenType.STAR.getValue() && t.subTrees.isEmpty()) return true;
    return false;
  }
  private ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t: trees) {
      if (!found && t.root==RegExTokenType.STAR.getValue() && t.subTrees.isEmpty()) {
        if (result.isEmpty()) throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size()-1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(RegExTokenType.STAR.getValue(), subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private boolean containPlus(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==RegExTokenType.PLUS.getValue() && t.subTrees.isEmpty()) return true;
    return false;
  }
  private ArrayList<RegExTree> processPlus(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t: trees) {
      if (!found && t.root==RegExTokenType.PLUS.getValue() && t.subTrees.isEmpty()) {
        if (result.isEmpty()) throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size()-1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(RegExTokenType.PLUS.getValue(), subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private boolean containConcat(ArrayList<RegExTree> trees) {
    boolean firstFound = false;
    for (RegExTree t: trees) {
      if (!firstFound && t.root!=RegExTokenType.ALTERN.getValue()) { firstFound = true; continue; }
      if (firstFound) if (t.root!=RegExTokenType.ALTERN.getValue()) return true; else firstFound = false;
    }
    return false;
  }
  private ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    boolean firstFound = false;
    for (RegExTree t: trees) {
      if (!found && !firstFound && t.root!=RegExTokenType.ALTERN.getValue()) {
        firstFound = true;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root==RegExTokenType.ALTERN.getValue()) {
        firstFound = false;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root!=RegExTokenType.ALTERN.getValue()) {
        found = true;
        RegExTree last = result.remove(result.size()-1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        subTrees.add(t);
        result.add(new RegExTree(RegExTokenType.CONCAT.getValue(), subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }
  private boolean containAltern(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==RegExTokenType.ALTERN.getValue() && t.subTrees.isEmpty()) return true;
    return false;
  }
  private ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    RegExTree gauche = null;
    boolean done = false;
    for (RegExTree t: trees) {
      if (!found && t.root==RegExTokenType.ALTERN.getValue() && t.subTrees.isEmpty()) {
        if (result.isEmpty()) throw new Exception();
        found = true;
        gauche = result.remove(result.size()-1);
        continue;
      }
      if (found && !done) {
        if (gauche==null) throw new Exception();
        done=true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(gauche);
        subTrees.add(t);
        result.add(new RegExTree(RegExTokenType.ALTERN.getValue(), subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }
  private RegExTree removeProtection(RegExTree tree) throws Exception {
    if (tree.root==RegExTokenType.PROTECTION.getValue() && tree.subTrees.size()!=1) throw new Exception();
    if (tree.subTrees.isEmpty()) return tree;
    if (tree.root==RegExTokenType.PROTECTION.getValue()) return removeProtection(tree.subTrees.get(0));

    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    for (RegExTree t: tree.subTrees) subTrees.add(removeProtection(t));
    return new RegExTree(tree.root, subTrees);
  }
  

}
