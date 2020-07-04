package org.dman;

import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Frequency {
    public static void cosine_distance(double[][] M, double[] frequency_vector, int m, int n, TextArea texts) {
        double dotProd = 0, sqA = 0, sqB = 0;
        ArrayList<String> origin_document = Parsing.getOrigin_document();
        ArrayList<Double> distance = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < m; k++) {
                dotProd += M[k][i] * frequency_vector[k];
                sqA += M[k][i] * M[k][i];
                sqB += frequency_vector[k] * frequency_vector[k];
            }
            distance.add(dotProd/(Math.sqrt(sqA)*Math.sqrt(sqB)));
            if (distance.get(i) > 0.01) {
                texts.appendText("C " + (i + 1) + "-ым предложением [" + origin_document.get(i) + "]: ");
                texts.appendText(String.format("%.3f", distance.get(i)));
                texts.appendText("\n");
            }
            dotProd = 0;
            sqA = 0;
            sqB = 0;
        }
    }

    public static double tf(String keyword, String sentence) {
        int n_rep = 0, n_word = 0;
        Scanner scanner = new Scanner(sentence);
        while (scanner.hasNext()) {
            String word = scanner.next();
            n_word++;
            if (word.equals(keyword))
                n_rep++;
        }
        return (double) n_rep/n_word;
    }

    public static double idf(String keyword, ArrayList<String> sentences) {
        int n_rep = 0;
        for (String s : sentences) {
            Scanner scanner = new Scanner(s);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (word.equals(keyword)) {
                    n_rep++;
                    break;
                }
            }
        }
        return Math.log((double) sentences.size()/n_rep);
    }

    public static double tf_idf(String keyword, String sentence, ArrayList<String> sentences) {
        return tf(keyword,sentence)*idf(keyword,sentences);
    }

    public static double[] request_frequency(String input) {
        double[] frequency_vector = new double[Parsing.getBag_of_words().size()];
        Arrays.fill(frequency_vector, 0);
        ArrayList<String> sentences = Parsing.getDocument();
        ArrayList<String> bag_of_words = Parsing.getBag_of_words();
        String keywords = Parsing.Keywords_To_List(input);
        for (String k: bag_of_words) {
            frequency_vector[bag_of_words.indexOf(k)] = tf_idf(k, keywords, sentences);
        }
        return frequency_vector;
    }

    public static double[][] keywords_frequency(ArrayList<String> sentences, ArrayList<String> bag_of_words) {
        double[][] frequency_matrix = new double[12][12];
        for (String k: bag_of_words)
            for (String s : sentences) {
                frequency_matrix[bag_of_words.indexOf(k)][sentences.indexOf(s)] = tf_idf(k, s, sentences);
            }
        return frequency_matrix;
    }
}
