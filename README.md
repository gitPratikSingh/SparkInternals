# SparkInternals
Internal design and optimizations in Spark

# Motivation for this project:
Spark enables us to process large quantities of data, beyond what can fit on a single machine, with a high-level, relatively easy-to-use API. Spark’s design and interface are unique, and it is one of the fastest systems of its kind. Uniquely, Spark allows us to write the logic of data transformations and machine learning algorithms in a way that is parallelizable, but relatively system agnostic. So it is often possible to write computations which are fast for distributed storage systems of varying kind and size.

# However
Despite its many advantages and the excitement around Spark, the simplest
implementation of many common data science routines in Spark can be much slower
and much less robust than the best version. Since the computations we are concerned
with may involve data at a very large scale, the time and resources that gains from
tuning code for performance are enormous. Performance that does not just mean run
faster; often at this scale it means getting something to run at all. It is possible to construct
a Spark query that fails on gigabytes of data but, when refactored and adjusted
with an eye towards the structure of the data and the requirements of the cluster succeeds
on the same system with terabytes of data.


# Aim of this project
In this project I intend to provide the internal specifications of various operation in Spark(count,countdistinct,join..etc), that will help developers understand the working of the operations and help them write more efficient code for Spark


# Results

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
 
 ![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/PartitionerJoin.PNG?raw=true "Partitioner")
 

As with most key-value operations, the cost of the join increases with the number of keys and the distance the records have to travel in order to get to their correct partition.


# Choosing a Join type:
The best scenario for a standard join is when both RDDs contain the same set of distinct keys. With duplicate keys, the size of the data may expand dramatically causing performance issues, and if one key is not present in both RDDs you will loose that row of data. Here are a few guidelines:

 - When both RDDs have duplicate keys, the join can cause the size of the data to expand dramatically. It may be better to perform a distinct or combineByKey operation to reduce the key space or to use cogroup to handle duplicate keys instead of producing the full cross product. By using smart partitioning during the combine step, it is possible to prevent a second shuffle in the join.

 - If keys are not present in both RDDs you risk losing your data unexpectedly. It can be safer to use an outer join, so that you are guaranteed to keep all the data in either the left or the right RDD, then filter the data after the join.

 - If one RDD has some easy-to-define subset of the keys, in the other you may be better off filtering or reducing before the join to avoid a big shuffle of data, which you will ultimately throw away anyway.
 
# Join Execution Plan
- In order to join data, Spark needs the data that is to be joined (i.e. the data based on each key) to live on the same partition. The default implementation of join in Spark is a shuffled hash join. The shuffled hash join ensures that data on each partition will contain the same keys by partitioning the second dataset with the same default partitioner as the first, so that the keys with the same hash value from both datasets are in the same partition.
  #### 
  While this approach always works, it can be more expensive than necessary because it requires a shuffle.
  The shuffle can be avoided if:
  	- Both RDDs have a known partitioner.
	- One of the datasets is small enough to fit in memory, in which case we can do a broadcast hash join


# Speed up techniques: 
  - Speeding Up Joins by Assigning a Known Partitioner
 If you have to do an operation before the join that requires a shuffle, such as aggrega teByKey or reduceByKey, you can prevent the 
 shuffle by adding a hash partitioner with the same number of partitions as an explicit argument to the first operation and persisting
 the RDD before the join. You could make the example in the previous section even faster, by using the partitioner for the address data
 as an argument for the reduceByKey step.
 
 ![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/Partitioner.PNG?raw=true "Partitioner")
 
  - Speeding Up Joins Using a Broadcast Hash Join
 A broadcast hash join pushes one of the RDDs (the smaller one) to each of the worker nodes. Then it does a map-side combine with each partition of the larger RDD. If one of your RDDs can fit in memory or can be made to fit in memory it is always beneficial to do a broadcast hash join, since it doesn’t require a shuffle.

![Alt text](https://github.com/gitPratikSingh/SparkInternals/blob/master/Broadcast.PNG?raw=true "braodcast")
