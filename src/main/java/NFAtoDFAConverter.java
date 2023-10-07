import java.util.*;

public class NFAtoDFAConverter {
    public DFA convert(NFA ndfa) {
        DFA dfa = new DFA();
        Set<Set<NFAState>> visited = new HashSet<>();
        Queue<Set<NFAState>> queue = new LinkedList<>();

        Set<NFAState> startStateSet = new HashSet<>();
        startStateSet.add(ndfa.getStartState());
        convertState(dfa, ndfa, startStateSet, visited);
        queue.offer(startStateSet);

        while (!queue.isEmpty()) {
            Set<NFAState> currentStateSet = queue.poll();
            convertState(dfa, ndfa, currentStateSet, visited);

            // Iterate through each cell of the transition table. If a new cell of the table is not one of the state cells of the entire DFA table, use convertState on it
            for (DFAState state : dfa.getTable()) {
                for (Map.Entry<Character, Set<Integer>> entry : state.getTransitions().entrySet()) {
                    Set<Integer> id = entry.getValue();
                    boolean found = false;
                    for (DFAState s : dfa.getTable()) {
                        if (s.getState().equals(id)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // Convert the state that is not present in the DFA table
                        Set<NFAState> newStateSet = new HashSet<>();
                        for (Integer i : id) {
                            NFAState ndfaState = ndfa.getStateById(i);
                            // Check if it is not already in the queue or visited
                            if (!queue.contains(newStateSet) && !visited.contains(newStateSet)) {
                                newStateSet.add(ndfaState);
                            }
                        }
                        queue.offer(newStateSet);
                    }
                }
            }
        }
        return dfa;
    }

    public void convertState(DFA dfa, NFA ndfa, Set<NFAState> currentStateSet, Set<Set<NFAState>> visited) {
        // Check if the state has already been visited
        if (visited.contains(currentStateSet)) {
            return;
        }

        // Convert NFAState to DFAState and add to DFA
        // Determine state cell of the table: the IDs of the currentStateSet and all the IDs of the states that we can reach with epsilon-transitions
        Set<Integer> state = new HashSet<>();
        for (NFAState nfaState : currentStateSet) {
            state.add(nfaState.getId());
            searchAllEpsilonTransitions(state, nfaState);
        }

        // determine transitions of the state: for each state in the state cell, get the symbols of the transitions and the id of the state that we can reach with that symbol
        Map<Character, Set<Integer>> transitions = new HashMap<>();
        for (Integer id : state) {
            for (Map.Entry<Character, Set<NFAState>> entry : ndfa.getStateById(id).getTransitions().entrySet()) {
                char symbol = entry.getKey();
                Set<NFAState> symbolStates = entry.getValue();
                Set<Integer> newState = new HashSet<>();

                for (NFAState symbolState : symbolStates) {
                    //if the symbol is not in the map, add to the new state
                    newState.add(symbolState.getId());
                    //add also the states that we can reach with a e-transition
                    searchAllEpsilonTransitions(newState,symbolState);
                    transitions.put(symbol, newState);
                }
            }
        }

        // Determine if there is a state in the state cell that is starting or accepting
        boolean isStartingState = false;
        boolean isAcceptingState = false;
        for (Integer id : state) {
            if (ndfa.getStartState().getId() == id) {
                isStartingState = true;
            }
            if (ndfa.getAcceptState().getId() == id) {
                isAcceptingState = true;
            }
        }

        DFAState dfaState = new DFAState(state, transitions, isStartingState, isAcceptingState);
        dfa.addState(dfaState);

        // Mark the state as visited
        visited.add(currentStateSet);
    }

    public void searchAllEpsilonTransitions(Set<Integer> stateSet, NFAState state) {
        for (NFAState epsilonState : state.getEpsilonTransitions()) {
            stateSet.add(epsilonState.getId());
            searchAllEpsilonTransitions(stateSet, epsilonState);
        }
    }
}
