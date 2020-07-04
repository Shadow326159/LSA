package org.dman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.File;
import javafx.stage.FileChooser;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive32Store;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.netio.BasicLogger;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML
    private Button Calculated_Button;

    @FXML
    private Button Get_keywords_button;

    @FXML
    private TextField FileDirectory_Field;

    @FXML
    private TextField KeyWords_Field;

    @FXML
    private Button Browser_Button;

    @FXML
    private TextArea texts;

    private static ArrayList<String> sentences = new ArrayList<>(), bag_of_words = new ArrayList<>();

    @FXML
    void initialize() {
        Browser_Button.setOnAction(actionEvent -> {
            FileDirectory_Field.clear();
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null)
                FileDirectory_Field.appendText(file.getAbsolutePath());
        });

        Get_keywords_button.setOnAction(actionEvent -> {
            texts.clear();
            bag_of_words.clear();
            sentences.clear();
            try {
                sentences = Parsing.readTxt(FileDirectory_Field.getText());
                bag_of_words = Parsing.getBag_of_words();
                texts.appendText("Доступные ключевые слова: " + bag_of_words);
                texts.appendText("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Calculated_Button.setOnAction(actionEvent -> {
            double[][] M = Frequency.keywords_frequency(sentences, bag_of_words);
            int[] doc = new int[Parsing.getDocument().size()];
            for (int i = 0; i < doc.length; i++)
                doc[i] = i;
            int[] kwords = new int[Parsing.getBag_of_words().size()];
            for (int i = 0; i < kwords.length; i++)
                kwords[i] = i;
            double[] frequency_vector = Frequency.request_frequency(KeyWords_Field.getText());
            MatrixStore<Double> matrix = Primitive32Store.FACTORY.rows(M);
            SingularValue<Double> svd = SingularValue.PRIMITIVE.make(matrix);
            svd.compute(matrix);
            MatrixStore<Double> foru = svd.getU().logical().column(0,1).row(kwords).get(); // U до размеров m×k
            MatrixStore<Double> ford = svd.getD().logical().column(0,1).row(0,1).get(); // D до размеров k×k
            MatrixStore<Double> forv = svd.getV().transpose().logical().column(doc).row(0,1).get(); // V^T до размеров k×n
            MatrixStore<Double> res = foru.multiply(ford.multiply(forv));
            //M = res.toRawCopy2D();

            MatrixStore<Double> usix = svd.getU().logical().column(kwords).row(kwords).get(); // U до размеров m×k
            MatrixStore<Double> dsix = svd.getD().logical().column(0,1,2,3).row(0,1,2,3).get(); // U до размеров m×k
            MatrixStore<Double> vect = Primitive32Store.FACTORY.rows(frequency_vector); // 0 0 0.358352 0 0 0.358352
            
            /*MatrixStore<Double> d_invert = dsix;
            try {
                d_invert= InverterTask.PRIMITIVE.invert(dsix);
            } catch (RecoverableCondition recoverableCondition) {
                recoverableCondition.printStackTrace();
            }

            //vect = vect.multiply(usix.multiply(d_invert));
            //vect = foru.multiply(d_invert);//  0.352391 0
            //vect = vect.multiply(usix.multiply(dsix));
            frequency_vector = vect.toRawCopy1D();*/

            Frequency.cosine_distance(M, frequency_vector, Parsing.getBag_of_words().size(), Parsing.getDocument().size(), texts);
            BasicLogger.debug("A", matrix);
            BasicLogger.debug();
            BasicLogger.debug("SVD", res);
            BasicLogger.debug();
            BasicLogger.debug("U", svd.getU());
            BasicLogger.debug();
            BasicLogger.debug("D", svd.getD());
            BasicLogger.debug();
            BasicLogger.debug("V^T", svd.getV().transpose());
            BasicLogger.debug();
            BasicLogger.debug("Трансформ U", foru);
            BasicLogger.debug();
            BasicLogger.debug("Трансформ D", ford);
            BasicLogger.debug();
            BasicLogger.debug("Трансформ V", forv);
            BasicLogger.debug();
            BasicLogger.debug("Вектор в семантическом пространстве:", vect);
            //BasicLogger.debug();
            //BasicLogger.debug("D^-1:", d_invert);
            BasicLogger.debug();
            BasicLogger.debug("U (6x6)", usix);
            BasicLogger.debug();
            BasicLogger.debug("D (6x6)", dsix);
            BasicLogger.debug();
            BasicLogger.debug("UxD (6x6)", dsix);
        });
    }
}