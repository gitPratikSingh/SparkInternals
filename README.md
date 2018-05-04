# SparkInternals
Internal design and optimizations in Spark



###
Spark manages data using partitions that helps parallelize distributed data processing with minimal network traffic for sending data between executors.
By default, Spark tries to read data into an RDD from the nodes that are close to it. Since Spark usually accesses distributed partitioned data, to optimize transformation operations it creates partitions to hold the data chunks.

