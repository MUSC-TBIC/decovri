package edu.musc.tbic.libsvm;

import libsvm.*;

public class Utils {
	
	public static void do_cross_validation( svm_problem current_problem , 
											svm_parameter current_params ) {
		// Default to 5 folds for cross-validation
		do_cross_validation( current_problem , 
							 current_params , 
							 5 );
	}
	
	public static void do_cross_validation( svm_problem current_problem , 
											svm_parameter current_params ,
											int nr_fold ) {
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[ current_problem.l];

		svm.svm_cross_validation( current_problem , 
								  current_params , 
								  nr_fold , target );
		if(current_params.svm_type == svm_parameter.EPSILON_SVR ||
				current_params.svm_type == svm_parameter.NU_SVR)
		{
			for(i=0;i<current_problem.l;i++)
			{
				double y = current_problem.y[i];
				double v = target[i];
				total_error += (v-y)*(v-y);
				sumv += v;
				sumy += y;
				sumvv += v*v;
				sumyy += y*y;
				sumvy += v*y;
			}
			System.out.print("Cross Validation Mean squared error = "+total_error/current_problem.l+"\n");
			System.out.print("Cross Validation Squared correlation coefficient = "+
					((current_problem.l*sumvy-sumv*sumy)*(current_problem.l*sumvy-sumv*sumy))/
					((current_problem.l*sumvv-sumv*sumv)*(current_problem.l*sumyy-sumy*sumy))+"\n"
					);
		}
		else
		{
			for(i=0;i<current_problem.l;i++)
				if(target[i] == current_problem.y[i])
					++total_correct;
			System.out.print("Cross Validation Accuracy = "+100.0*total_correct/current_problem.l+"%\n");
		}
	}

	public static double atof(String s)
	{
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d))
		{
			System.err.print("NaN or Infinity in input\n");
			System.exit(1);
		}
		return(d);
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}
}
