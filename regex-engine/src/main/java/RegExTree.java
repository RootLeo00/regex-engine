import java.util.ArrayList;

//UTILITARY CLASS
class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;
    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }
    //FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty()) return rootToString();
        StringBuilder result = new StringBuilder(rootToString() + "(" + subTrees.get(0).toString());
        for (int i=1;i<subTrees.size();i++) result.append(",").append(subTrees.get(i).toString());
        return result+")";
    }
    public String rootToString() {
        if (root==RegExTokenType.CONCAT.getValue()) return ".";
        if (root==RegExTokenType.STAR.getValue()) return "*";
        if (root==RegExTokenType.ALTERN.getValue()) return "|";
        if (root==RegExTokenType.DOT.getValue()) return ".";
        return Character.toString((char)root);
    }


}
