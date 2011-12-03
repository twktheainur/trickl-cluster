package com.trickl.cluster;

import com.trickl.cluster.FuzzyCMeans;
import com.trickl.cluster.stats.Partition;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import com.trickl.dataset.GaussianCircles2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.Test;

public class FuzzyCMeansTest {

   public FuzzyCMeansTest() {
   }

   // TODO: Need a verifiable result with assertions
   @Test
   public void ClusterGaussianCircles() throws IOException
   {
      RandomEngine randomEngine = new MersenneTwister(123456789);
      GaussianCircles2D gaussianCircles = new GaussianCircles2D();
      gaussianCircles.setRadiusStd(0.20);
      gaussianCircles.setRandomEngine(randomEngine);
      DoubleMatrix2D data = gaussianCircles.generate(100);

      FuzzyCMeans fcm = new FuzzyCMeans();      
      fcm.setRandomEngine(randomEngine);
      fcm.cluster(data, 3);

      DoubleMatrix2D partition = fcm.getPartition();
      System.out.println("Partition index: " + Partition.partitionCoefficient(partition));
      System.out.println("Partition entropy: " + Partition.partitionEntropy(partition));
      System.out.println("Xie-Beni index: " + Partition.xieBeniIndex(partition, fcm.getMeans().viewDice(), data, fcm.getFuzzification()));

      // Output the membership data to separate files
      for (int k = 0; k < partition.columns(); ++k)
      {
         String fileName = "fuzzy-cmeans-cluster-" + k + ".dat";
         String packagePath = this.getClass().getPackage().getName().replaceAll("\\.", "/");
         File outputFile = new File("src/test/resources/"
              + packagePath
              + "/" + fileName);
         PrintWriter writer = new PrintWriter(outputFile);

         for (int i = 0; i < data.rows(); ++i)
         {
            StringBuffer dataLine = new StringBuffer();
            dataLine.append(data.getQuick(i, 0));

            for (int j = 1; j < data.columns(); ++j)
            {
               dataLine.append(' ');
               dataLine.append(data.getQuick(i, j));
            }

            dataLine.append(' ');
            dataLine.append(partition.getQuick(i, k));
            writer.println(dataLine.toString());
         }

         writer.close();
      }
   }
}
