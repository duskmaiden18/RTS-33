package com.example.lab33;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // equation: a*x1 + b*x2 + c*x3 + d*x4 = y

    private int[][] firstPopulation = new int[4][4];
    private int[][] lastPopulation = new int[4][4];
    private int[] fits = new int[4];
    private int[] stats = new int[4];
    private double[] deltas = new double[4];
    private int mutCof;

    private EditText a, b, c, d, y, mutCoef;
    private TextView genom, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        getUIElements();

        int maxRange = castToInt(y) / 2;

        for (int i = 0; i < firstPopulation.length; i++) {
            firstPopulation[i] = getRandomArray(maxRange);
        }

        int[] eqCoef = {castToInt(a), castToInt(b), castToInt(c), castToInt(d)};
        mutCof = castToDouble(mutCoef);

        if (mutCof < 0 || mutCof > 1) {
            Toast.makeText(getApplicationContext(),
                    "Not right input",
                    Toast.LENGTH_SHORT).show();
        }

        long t1 = System.currentTimeMillis();
        int[] result = findFitness(eqCoef, castToInt(y));
        long t2 = System.currentTimeMillis() - t1;

        endTime.setText(Long.toString(t2));
        genom.setText(Arrays.toString(result));
    }

    private int[] findFitness(int[] cor, int maxRange) {

        double sumDelta = 0;

        for (int i = 0; i < fits.length; i++) {
            fits[i] += firstPopulation[i][0] * cor[i];
            deltas[i] = Math.abs(maxRange - fits[i]);
        }

        for (int i = 0; i < deltas.length; i++) {
            if (deltas[i] == 0) {
                return firstPopulation[i];
            }
            sumDelta += 1 / (deltas[i] + 1);
        }

        for (int i = 0; i < deltas.length; i++) {
            stats[i] = (int) ((1 / deltas[i]) / sumDelta * 100);
        }

        for (int i = 0; i < lastPopulation.length; i++) {
            lastPopulation[i] = randomPopulation();
        }

        crossOver();

        // mutations
        mutate(mutCof);

        // recursive algorithm
        return findFitness(cor, maxRange);
    }

    public void getUIElements(){
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);
        y = findViewById(R.id.y);
        endTime = findViewById(R.id.genotype);
        genom = findViewById(R.id.resTime);
        mutCoef = findViewById(R.id.mutCoef);
    }


    private int[] getRandomArray(int maxRange) {
        int[] rand = new int[4];
        for (int i = 0; i < rand.length; i++) {
            rand[i] = (int) (Math.random() * maxRange);
        }
        return rand;
    }

    public void crossOver() {
        int tmp = Objects.requireNonNull(lastPopulation[0])[0];
        lastPopulation[0][0] = lastPopulation[1][0];
        lastPopulation[1][0] = tmp;

        tmp = lastPopulation[2][lastPopulation[2].length - 1];
        lastPopulation[2][lastPopulation[2].length - 1] = lastPopulation[3][lastPopulation[2].length - 1];
        lastPopulation[3][lastPopulation[2].length - 1] = tmp;
    }

    private int[] randomPopulation() {
        int genValue = randomizeNumber(100);

        if (genValue <= 100) {
            return firstPopulation[3];
        }
        if (genValue < stats[0]) {
            return firstPopulation[0];
        }
        if (genValue < stats[0] + stats[1]) {
            return firstPopulation[1];
        }
        if (genValue < stats[0] + stats[1] + stats[2]) {
            return firstPopulation[2];
        }
        return null;
    }

    public void mutate(double mutCoef) {
        double firstMutation = mutCoef * randomizeNumber(lastPopulation[2].length - 1);
        double secondMutation = mutCoef * randomizeNumber(lastPopulation[2].length - 1);

        for (int i = 0; i < lastPopulation.length; i++) {
            if ((int)Math.round(firstMutation) == i) {
                lastPopulation[i][(int)Math.round(secondMutation)] += 1;
            }
        }

        System.arraycopy(lastPopulation, 0, firstPopulation, 0, lastPopulation.length);
    }

    public int randomizeNumber(int maxValue){
        return (int) (Math.random() * maxValue);
    }

    public int castToInt(EditText param){
        return Integer.parseInt(param.getText().toString());
    }

    public int castToDouble(EditText param){
        return Double.parseDouble(param.getText().toString());
    }
}