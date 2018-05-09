# Spark Model of Parallel Computing: RDDs
 - Spark allows users to write a program for the driver (or master node) on a cluster computing system that can perform 
   operations on data in parallel.
 - Spark represents large datasets as RDDs, immutable distributed collections of objects, which are stored in the executors
   or (slave nodes). The objects that comprise RDDs are called partitions and may be (but do not need to be) computed on 
   different nodes of a distributed system. RDDs are immutable. 
 - Rather than evaluating each transformation as soon as specified by the driver program, Spark evaluates RDDs lazily, 
   computing RDD transformations only when the final RDD data needs to be computed (often by writing out to storage or 
   collecting an aggregate to the driver)
   
# The RDD Interface
  Spark uses five main properties to represent an RDD internally.
 
   - partitions(): Returns an array of the partition objects that make up the parts of the distributed dataset
   - iterator(p, parentIters) Computes the elements of partition p given iterators for each of its parent partitions. 
     This function is called in order to compute each of the partitions in this RDD.
   - dependencies() Returns a sequence of dependency objects. The dependencies let the scheduler know how this RDD 
	 depends on other RDDs
   - partitioner() Returns a Scala option type that contains a partitioner object if the RDD has a function between 
     datapoint and partitioner associated with it, such as a hashPartitioner
   - preferredLocations(p) Returns information about the data locality of a partition, p.
   
# DataFrames, Datasets & Spark SQL
 - Like RDDs, DataFrames and Datasets represent distributed collections, with additional schema information not found in RDDs
 - This additional schema information is used to provide a more efficient storage layer and in the optimizer. Beyond schema 
   information, the operations performed on DataFrames are such that the optimizer can inspect the logical meaning rather than 
   arbitrary functions.
