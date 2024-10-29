package exam.example;

import java.util.*;

public class PetriNet {
    private List<String> places;  // List of places
    private Map<String, List<String>[]> transitions;  // Transition rules
    private Map<String, Integer> tokens = new HashMap<>();  // Token counts for each place

    public PetriNet(List<String> places, Map<String, List<String>[]> transitions) {
        this.places = places;
        this.transitions = transitions;
    }

    // Add tokens to a place
    public void addTokens(String place, int count) {
        tokens.put(place, tokens.getOrDefault(place, 0) + count);
    }

    // Fire a transition
    public boolean fire(String transition) {
        List<String>[] io = transitions.get(transition);
        List<String> inputPlaces = io[0];
        List<String> outputPlaces = io[1];

        // Check if transition can fire (all input places have tokens)
        if (inputPlaces.stream().allMatch(place -> tokens.getOrDefault(place, 0) > 0)) {
            inputPlaces.forEach(place -> tokens.put(place, tokens.get(place) - 1));
            outputPlaces.forEach(place -> tokens.put(place, tokens.getOrDefault(place, 0) + 1));
            return true;
        }
        return false;
    }

    // Get the current state of tokens
    public int[] getState() {
        return places.stream().mapToInt(place -> tokens.getOrDefault(place, 0)).toArray();
    }

    // Check if the system is in a deadlock state
    public boolean isDeadlocked() {
        return transitions.keySet().stream().noneMatch(this::canFire);
    }

    // Helper to check if a transition can fire
    private boolean canFire(String transition) {
        List<String>[] io = transitions.get(transition);
        List<String> inputPlaces = io[0];
        return inputPlaces.stream().allMatch(place -> tokens.getOrDefault(place, 0) > 0);
    }

    // **New Getter for Transitions**
    public Map<String, List<String>[]> getTransitions() {
        return transitions;
    }
}

