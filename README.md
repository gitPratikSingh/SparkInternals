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
