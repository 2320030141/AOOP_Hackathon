package exam.example;

import java.util.*;

public class DataGenerator {
    public static List<int[]> data = new ArrayList<>();
    public static List<Integer> labels = new ArrayList<>();

    public static void generateData(PetriNet petriNet, int steps) {
        for (int i = 0; i < steps; i++) {
            // Record current state
            int[] state = petriNet.getState();
            data.add(state);

            // Check for deadlock and store label
            boolean deadlock = petriNet.isDeadlocked();
            labels.add(deadlock ? 1 : 0);

            if (deadlock) break;  // Stop if deadlock detected

            // Fire one of the available transitions randomly
            for (String transition : petriNet.getTransitions().keySet()) {
                if (petriNet.fire(transition)) break;
            }
        }
    }
}
