import java.util.*;

public class Meanwise extends Classifier<Isolate, Phylogeny, Species> {
   ArrayList<ListEntry<Isolate, Double>> neighbors;
   public Meanwise(Isolate unknown, Phylogeny library) {
      super(unknown, library);
      neighbors = compare(unknown, library);
   }

   public ArrayList<ListEntry<Isolate, Double>> compare(Isolate unknown, Phylogeny library) {
      Collection<Isolate>                    allIsolates;
      ArrayList<ListEntry<Isolate, Double>>  neighbors;
      Similarities<Isolate, Double>          similarities;
      ArrayList<Double>                      results;
      Double                                 result;

      allIsolates    = library.getAllIsolates().values();
      neighbors      = new ArrayList<ListEntry<Isolate, Double>>(allIsolates.size());
      similarities   = new PearsonIsolate();

      for (Isolate i : library.getAllIsolates().values()) {

         /*Calculate All Similarities*/
         results = new ArrayList<Double>(similarities.getSimilarities().size());
         for (SimilarityMetric<Isolate, Double> sim : similarities.getSimilarities()) {

            result = sim.similarity(unknown, i);
            if (result == null) {
               result = 0.0;
            }
            results.add(result);
         }

         /*Add to Neighbors*/
         neighbors.add(new ListEntry<Isolate, Double>(i, mean(results), -1));
      }

      /* Sort */
      Collections.sort(neighbors);

      /*Mark Position*/
      int i = 0;
      for (ListEntry<Isolate, Double> n : neighbors) {
         n.setPosition(i++);
      }
      /*We shouldn't ever have a state where the first in the list isn't the
       * unknown, since we're comparing against the whole databse.*/
      if (!neighbors.get(0).getData().equals(unknown)) {
         throw new IllegalStateException(
               String.format("The unknown (%s) is not the zeroth element (%s)", 
                  unknown, this.neighbors.get(0))
               );
      }

      return neighbors;
   }

   public Species classify(Integer k, Double alpha){
      ArrayList<ListEntry<Isolate, Double>> nearest;
      Species result;
      ListCounter<Species, Isolate, Double> counter;

      counter = new ListCounter<Species, Isolate, Double>();

      nearest = new ArrayList<ListEntry<Isolate, Double>>(k + 1);
      result  = null;
      for (int i = 1; i <= k && neighbors != null && neighbors.get(0).getValue() > alpha; i++) {
         nearest.add(neighbors.get(i));
      }

      result = counter.findMostPlural(nearest);

      return result;
   }

   public Double mean(List<Double> vals) {
      return arithmeticMean(vals);
   }

   public Double euclideanNorm(List<Double> vals) {
      Double sum = 0.0;
      for (Double val : vals) {
         sum += val*val;
      }
      return Math.sqrt(sum);
   }

   public Double arithmeticMean(List<Double> vals) {
      Double sum = 0.0;
      for (Double val : vals) {
         sum += val;
      }
      return sum / vals.size();
   }
}
