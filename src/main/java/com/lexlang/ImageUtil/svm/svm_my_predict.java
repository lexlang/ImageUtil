package com.lexlang.ImageUtil.svm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class svm_my_predict {
    public svm_model model;
    
	public svm_my_predict(String modelFile){
		try {this.model = svm.svm_load_model(modelFile);} catch (IOException e) {}
	}
    
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}
	
	public double predict(String line) {
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		final int svm_type = svm.svm_get_svm_type(model);
		final int nr_class = svm.svm_get_nr_class(model);
		double[] prob_estimates = null;
		
		final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

		final double target = atof(st.nextToken());
		final int m = st.countTokens() / 2;
		final svm_node[] x = new svm_node[m];
		for (int j = 0; j < m; j++) {
			x[j] = new svm_node();
			x[j].index = atoi(st.nextToken());
			x[j].value = atof(st.nextToken());
		}

		double v;
		v = svm.svm_predict(model, x);
		return v;
	}
}
