import java.util.Map;

public class RegExToNFAConverter {
    private static int stateIdCounter = 0;

    private NFA right_ndfa;
    private NFA left_ndfa;

    public NFA convert(RegExTree regexTree) {
        return convertLeavesFromLastToRoot(regexTree, left_ndfa, right_ndfa);
    }


    private NFA convertLeavesFromLastToRoot(RegExTree regexTree, NFA left_ndfa, NFA right_ndfa) {
        if (regexTree == null) return null;

        // Check if it's a leaf node (no children)
        if (regexTree.subTrees.isEmpty()) {
            return symbolOperation(regexTree);
        }
        // Traverse the right subtree (last leaf)
        if(regexTree.subTrees.size() > 1) {
            right_ndfa=convertLeavesFromLastToRoot(regexTree.subTrees.get(1), left_ndfa, right_ndfa);
        }

        // Traverse the left subtree (move up towards the root)
        left_ndfa=convertLeavesFromLastToRoot(regexTree.subTrees.get(0), left_ndfa,left_ndfa);

        //finally convert the root
        return convertSubtree(regexTree, left_ndfa, right_ndfa);
    }

    private static void epsilonTransition(NFAState fromState, NFAState toState) {
        fromState.getEpsilonTransitions().add(toState);
    }
    private void transition(NFAState fromState, char symbol, NFAState toState) {
        fromState.addTransition(symbol, toState);
    }

    private NFA alternationOperation(NFA r1, NFA r2) {
        // taking the automata for R1 and R2 and adding two new states, one the start
        //state and the other the accepting state.
        NFAState newStartState = new NFAState(stateIdCounter++, false, true);
        NFAState newAcceptState = new NFAState(stateIdCounter++, true, false);
        // The new start state has an e-transition to the start states of the automata for R1 and R2.
        epsilonTransition(newStartState, r1.getStartState());
        epsilonTransition(newStartState, r2.getStartState());
        // The accepting states of the automata for R1 and R2 have e-transitions to the new accepting state.
        epsilonTransition(r1.getAcceptState(), newAcceptState);
        epsilonTransition(r2.getAcceptState(), newAcceptState);
        // Remove old start/accept state
        r1.getAcceptState().setAccept(false);
        r2.getAcceptState().setAccept(false);
        r1.getStartState().setStart(false);
        r2.getStartState().setStart(false);
        return new NFA(newStartState, newAcceptState);
    }

    private static NFA concatenationOperation(NFA r1, NFA r2) {
        // We add an e-transition from the accepting state of the automaton for R1 to the start state of the automaton for R2.
        epsilonTransition(r1.getAcceptState(), r2.getStartState());
        // Remove old start/accept state
        r1.getAcceptState().setAccept(false);
        r2.getStartState().setStart(false);
        return new NFA(r1.getStartState(), r2.getAcceptState());
    }

    private static NFA starOperation(NFA r1) {
        //We add to the automaton for R1 a new start and accepting state.
        NFAState startState = new NFAState(stateIdCounter++, false, true);
        NFAState acceptState = new NFAState(stateIdCounter++, true, false);
        //The start state has an e-transition to the accepting state and to the start state of the automaton for R1.
        epsilonTransition(startState, acceptState);
        epsilonTransition(startState, r1.getStartState());
        //The accepting state of the automaton for R1 is given an e-transition back to its start state, and one to the accepting state of the automaton for R1.
        epsilonTransition(r1.getAcceptState(), r1.getStartState());
        epsilonTransition(r1.getAcceptState(), acceptState);
        // Remove old start/accept state
        r1.getAcceptState().setAccept(false);
        r1.getStartState().setStart(false);
        return new NFA(startState, acceptState);
    }

    private static NFA plusOperation(NFA r1) {
        //We add to the automaton for R1 a new start and accepting state.
        NFAState startState = new NFAState(stateIdCounter++, false, true);
        NFAState acceptState = new NFAState(stateIdCounter++, true, false);
        //The start state has an e-transition to the accepting state and to the start state of the automaton for R1.
        //epsilonTransition(startState, acceptState); //the only difference with star operation: we don't add the transition to the accept state, because we want to force at least one repetition
        epsilonTransition(startState, r1.getStartState());
        //The accepting state of the automaton for R1 is given an e-transition back to its start state, and one to the accepting state of the automaton for R1.
        epsilonTransition(r1.getAcceptState(), r1.getStartState());
        epsilonTransition(r1.getAcceptState(), acceptState);
        // Remove old start/accept state
        r1.getAcceptState().setAccept(false);
        r1.getStartState().setStart(false);
        return new NFA(startState, acceptState);
    }


    private NFA symbolOperation(RegExTree regexTree) {
        //we have to create a new start and accept state and connect them with x-transition
        NFAState startState = new NFAState(stateIdCounter++, false, true);
        NFAState acceptState = new NFAState(stateIdCounter++, true,     false);
        // start state and accept state get connected by x-transition (where x is the symbol in input)
        transition(startState, regexTree.rootToString().charAt(0), acceptState);
        return new NFA(startState, acceptState);
    }


    private NFA convertSubtree(RegExTree regexTree, NFA r1, NFA r2) {
        if (regexTree.root == RegExTokenType.CONCAT.getValue()) {
            return concatenationOperation(r1, r2);
        } else if (regexTree.root == RegExTokenType.ALTERN.getValue()) {
            return alternationOperation(r1, r2);
        } else if (regexTree.root == RegExTokenType.STAR.getValue()) {
            return starOperation(r1);
        } else if (regexTree.root == RegExTokenType.PLUS.getValue()) {
            return plusOperation(r1);
        } else {
            return symbolOperation(regexTree);
        }

    }

}
