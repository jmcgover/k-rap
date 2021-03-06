import java.util.*;
import java.io.*;

public class Phylogeny 
   implements Serializable {
   private HashMap<String, Species> species;
   private HashMap<String, Host> hosts;
   private HashMap<String, Isolate> isolates;
   private HashMap<String, Pyroprint> pyroprints;
   public Phylogeny(){
      this.species = new HashMap<String, Species>();
      this.hosts = new HashMap<String, Host>();
      this.isolates = new HashMap<String, Isolate>();
      this.pyroprints = new HashMap<String, Pyroprint>();
   }
   public Phylogeny(Phylogeny tree) {
      this.species = new HashMap<String, Species>();
      this.hosts = new HashMap<String, Host>();
      this.isolates = new HashMap<String, Isolate>();
      this.pyroprints = new HashMap<String, Pyroprint>();
      for (Species s : tree.getAllSpecies().values()) {
         this.add(new Species(s));
         for (Host h : s.getHosts().values()) {
            this.add(new Host(h));
            for (Isolate i : h.getIsolates().values()) {
               this.add(new Isolate(i));
               for (Pyroprint p : i.getPyroprints().values()) {
                  this.add(p);
               }
            }
         }
      }
   }

   public Map<String,Species> getAllSpecies(){
      return this.species;
   }
   public Map<String,Host> getAllHosts(){
      return this.hosts;
   }
   public Map<String,Isolate> getAllIsolates(){
      return this.isolates;
   }
   public Map<String,Pyroprint> getAllPyroprints(){
      return this.pyroprints;
   }

   public Species removeSpecies(Species s) {
      return this.species.remove(s.key());
   }

   public Host removeHost(Host h) {
      Species s = this.getSpecies(h.speciesKey());
      if (s != null && false == s.removeHost(h)) {
         System.err.printf("Could not find HOST %s in SPECIES %s.\n", h, s);
      }
      return this.hosts.remove(h.key());
   }

   public Isolate removeIsolate(Isolate i) {
      Host h = this.getHost(i.hostKey());
      Species s = this.getSpecies(i.speciesKey());
      if (h != null && false == h.removeIsolate(i)) {
         System.err.printf("Could not find ISOLATE %s in HOST %s.\n", i, h);
      }

      if (s != null && false == s.removeIsolate(i)) {
         System.err.printf("Could not find ISOLATE %s in SPECIES %s.\n", i, h);
      }
      return this.isolates.remove(i.key());
   }

   public Pyroprint removePyroprint(Pyroprint p) {
//      Species s = this.getSpecies(p.speciesKey());
      Host h = this.getHost(p.hostKey());
      Isolate i = this.getIsolate(p.isolateKey());
      if (i != null && false == i.removePyroprint(p)) {
         System.err.printf("Could not find PYROPRINT %s in ISOLATE %s.\n", p, i);
      }
      if (h != null && false == h.removePyroprint(p)) {
         System.err.printf("Could not find PYROPRINT %s in HOST %s.\n", p, i);
      }
//      if (s != null && false == s.removePyroprint(p)) {
//         System.err.printf("Could not find PYROPRINT %s in SPECIES %s.\n", p, i);
//      }
      return this.pyroprints.remove(p.key());
   }

   public int getSpeciesCount() {
      return this.species.values().size();
   }

   public int getHostCount() {
      return this.hosts.values().size();
   }

   public int getIsolateCount() {
      return this.isolates.values().size();
   }

   public int getPyroprintCount() {
      return this.pyroprints.values().size();
   }

   public int getSpeciesCount(Species s) {
      int count = 0;
      for (Species o : this.species.values()) {
         if (s.equals(o)) {
            count ++;
         }
      }
      return count;
   }

   public int getHostCount(Species s) {
      int count = 0;
      for (Species o : this.species.values()) {
         if (s.equals(o)) {
            count += o.getHostCount();
         }
      }
      return count;
   }

   public int getIsolateCount(Species s) {
      int count = 0;
      for (Species o : this.species.values()) {
         if (s.equals(o)) {
            count += o.getIsolateCount();
         }
      }
      return count;
   }

   public int getPyroprintCount(Species s) {
      int count = 0;
      for (Species o : this.species.values()) {
         if (s.equals(o)) {
            count += o.getPyroprintCount();
         }
      }
      return count;
   }

   public Species getSpecies(String key){
      return this.species.get(key);
   }
   public Host getHost(String key){
      return this.hosts.get(key);
   }
   public Isolate getIsolate(String key){
      return this.isolates.get(key);
   }
   public Pyroprint getPyroprint(String commonName, String hostId, String isoId, String pyroId){
      String pyroKey = String.format("%s,%s,%s,%s",commonName,hostId,isoId,pyroId);
      System.err.println("'" + pyroKey + "'");
      return pyroprints.get(pyroKey);
   }

   public boolean add(Species species){
      if (!this.species.containsKey(species.key())) {
         this.species.put(species.key(),species);
         return true;
      }
      return false;
   }
   public boolean add(Host host){
      if (!hosts.containsKey(host.key())) {
         hosts.put(host.key(),host);
         species.get(host.speciesKey()).addHost(host);
         return true;
      }
      return false;
   }
   public boolean add(Isolate isolate){
      if (!isolates.containsKey(isolate.key())) {
         isolates.put(isolate.key(),isolate);
         hosts.get(isolate.hostKey()).addIsolate(isolate);
         return true;
      }
      return false;
   }
   public boolean add(Pyroprint pyroprint){
      if (!pyroprints.containsKey(pyroprint.key())) {
         pyroprints.put(pyroprint.key(),pyroprint);
         isolates.get(pyroprint.isolateKey()).addPyroprint(pyroprint);
         return true;
      }
      return false;
   }

   public boolean containsPyroId(String pyroId){
      for (Pyroprint p : pyroprints.values()) {
         if (p.getPyroId().equals(pyroId)) {
            return true;
         }
      }
      return false;
   }
   public void save(String phylogenyFilename){
      FileOutputStream fileOut = null;
      ObjectOutputStream objectOut = null;
      try {
         fileOut = new FileOutputStream(phylogenyFilename);
         objectOut = new ObjectOutputStream(fileOut);
         objectOut.writeObject(this);
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not find? '" + phylogenyFilename + "'.");
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("Could not save '" + phylogenyFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
   }
   public static Phylogeny load(String phylogenyFilename){
      Phylogeny tree = null;
      FileInputStream fileIn = null;
      ObjectInputStream objectIn = null;
      Object obj = null;
      try {
         fileIn = new FileInputStream(phylogenyFilename);
         objectIn = new ObjectInputStream(fileIn);
         obj = objectIn.readObject();
      }
      catch (FileNotFoundException e) {
         System.err.println("Could not open '" + phylogenyFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (IOException e) {
         System.err.println("IOException for '" + phylogenyFilename + "'.");
         e.printStackTrace();
         System.exit(1);
      }
      catch (ClassNotFoundException e) {
         System.err.println("Could not find the Class of the object you are trying to load.");
         System.exit(1);
      }

      if (obj instanceof Phylogeny) {
         tree = (Phylogeny)obj;
      }
      else {
         throw new ClassCastException( "Your file '" + phylogenyFilename + "' was not a Phylogeny object.\n");
      }
      return tree;
   }
   
   // PRINT
   public void printTree(PrintStream stream){
      for (Species s : getAllSpecies().values()) {
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               for (Pyroprint p : i.getPyroprints().values()) {
                  stream.printf("%s %s %s %s: '%s' %s\n",s,h,i,p,p.key(), 
                        p.getAppliedRegion());
               }
            }
         }
      }
   }
}
