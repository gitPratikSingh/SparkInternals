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

# Core Spark Joins
  Joins in general are expensive since they require that corresponding keys from each RDD are located at the same partition so that they can be combined locally. 
 
 - ShuffleJoin: If the RDDs do not have known partitioners, they will need to be shuffled so that both RDDs share a partitioner and data
with the same keys lives in the same partitions.

![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/ShuffleJoin.PNG?raw=true "ShuffleJoin")

 - Co-located Join: If they have the same partitioner, the data may be colocated, so as to avoid network transfer. 
 
 ![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/ColocatedJoine.PNG?raw=true "ShuffleJoin")
 
 - Partitioner Join: Regardless of if the partitioners are the same, if one (or both) of the RDDs have a known partitioner only a narrow dependency is created. 
 
 ![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/PartitionerJoin.PNG?raw=true "ShuffleJoin")
 

As with most key-value operations, the cost of the join increases with the number of keys and the distance the records have to travel in order to get to their correct partition.
