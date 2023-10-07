import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFAState {
    private int id;
    private boolean isAccept;
    private boolean isStart;

    private Set<NFAState> epsilonTransitions; //automation for epsilon transition: start ----> epsilon ----> end
    private Map<Character, Set<NFAState>> transitions; //automation for basic transition: start ----> x ----> end

    public NFAState(int i, boolean isAccept, boolean isStart) {
        this.id = i;
        this.isAccept = isAccept;
        this.isStart = isStart;
        this.epsilonTransitions = new HashSet<>();
        this.transitions = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public boolean isStart() {
        return isStart;
    }

    public Set<NFAState> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public Map<Character, Set<NFAState>> getTransitions() {
        return transitions;
    }

    public void setAccept(boolean accepting) {
        isAccept = accepting;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void addTransition(char symbol, NFAState state) {
        transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(state);
    }


    @Override
    public String toString() {
        return "State " + id + (isAccept ? " (Accepting)" : "");
    }
}
