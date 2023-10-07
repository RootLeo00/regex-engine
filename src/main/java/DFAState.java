import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class DFAState {
    private Set<Integer> state;
    private Map<Character, Set<Integer>> transitions;
    private boolean isStartingState;
    private boolean isAcceptingState;

    public DFAState(Set<Integer> state, Map<Character, Set<Integer>> transitions, boolean isStartingState, boolean isAcceptingState) {
        this.state = state;
        this.transitions = transitions;
        this.isStartingState = isStartingState;
        this.isAcceptingState = isAcceptingState;
    }

    public Set<Integer> getState() {
        return state;
    }

    public void setState(Set<Integer> state) {
        this.state = state;
    }
    public void setState(Integer state) {
        Set<Integer> set = new HashSet<>();
        set.add(state);
        this.state = set;
    }

    public Map<Character, Set<Integer>> getTransitions() {
        return transitions;
    }

    public boolean isStartingState() {
        return isStartingState;
    }

    public boolean isAcceptingState() {
        return isAcceptingState;
    }

    @Override
    public String toString(){
        return state.toString() +"\t" + transitions.toString() +"\t" + isStartingState +"\t" + isAcceptingState ;
    }
}
