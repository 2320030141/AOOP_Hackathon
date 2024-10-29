package exam.example;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class DeadlockPredictor {
    @org.jetbrains.annotations.NotNull
    public static MultiLayerNetwork createModel(int inputDim) {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(123)  // Set the seed here
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputDim)
                        .nOut(16)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(16)
                        .nOut(8)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                        .nIn(8)
                        .nOut(1)
                        .activation(Activation.SIGMOID)
                        .build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        return model;
    }

    public static void main(String[] args) {
        // Define places and transitions
        List<String> places = Arrays.asList("P1", "P2");
        Map<String, List<String>[]> transitions = new HashMap<>();
        transitions.put("T1", new List[]{Arrays.asList("P1"), Arrays.asList("P2")});
        transitions.put("T2", new List[]{Arrays.asList("P2"), Arrays.asList("P1")});

        // Initialize Petri net and add tokens
        PetriNet petriNet = new PetriNet(places, transitions);
        petriNet.addTokens("P1", 1);

        // Generate data
        DataGenerator.generateData(petriNet, 100);
        int[][] dataArray = DataGenerator.data.toArray(new int[0][0]);
        int[] labelArray = DataGenerator.labels.stream().mapToInt(i -> i).toArray();

        // Check and confirm correct shapes
        System.out.println("Data Array Shape: [" + dataArray.length + ", " + dataArray[0].length + "]");
        System.out.println("Label Array Shape: " + labelArray.length);

        // Convert data and labels to INDArrays
        INDArray data = Nd4j.create(dataArray);
        INDArray labels = Nd4j.create(labelArray, new int[]{labelArray.length, 1});  // Ensure correct shape

        if (dataArray.length != labelArray.length) {
            throw new IllegalArgumentException("Data and labels must have the same number of samples.");
        }
        // Debug: Print shapes to confirm alignment
        System.out.println("Data shape: " + Arrays.toString(data.shape()));  // [batch_size, input_dim]
        System.out.println("Labels shape: " + Arrays.toString(labels.shape()));  // [batch_size, 1]

        // Create and train the model
        MultiLayerNetwork model = DeadlockPredictor.createModel(dataArray[0].length);
        DataSet dataset = new DataSet(data, labels);

        // Debug: Print dataset shapes
        System.out.println("Dataset input shape: " + Arrays.toString(dataset.getFeatures().shape()));
        System.out.println("Dataset label shape: " + Arrays.toString(dataset.getLabels().shape()));

        model.fit(dataset);

        // Predict on new data
        INDArray predictions = model.output(data);
        System.out.println("Predictions: " + predictions);
    }
}