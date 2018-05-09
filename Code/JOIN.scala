val partitioner = new HashPartitioner(partitions = 4)

val a = spark.sparkContext.makeRDD(Seq(
  (1, "A"), (2, "B"), (3, "C"), (4, "D"), (5, "E"), (6, "F"), (7, "G"), (8, "H")
)).partitionBy(partitioner)

val b = spark.sparkContext.makeRDD(Seq(
  (1, "a"), (2, "b"), (3, "c"), (4, "d"), (5, "e"), (6, "f"), (7, "g"), (8, "h")
)).partitionBy(partitioner)

println("A:")
a.foreachPartition(p => {
  p.foreach(t => print(t + " "))
  println()
})

println("B:")
b.foreachPartition(p => {
  p.foreach(t => print(t + " "))
  println()
})

println("Join:")
a.join(b, partitioner)
  .foreachPartition(p => {
    p.foreach(t => print(t + " "))
    println()
})


// notes
/*
In order to join data, Spark needs the data that is to be joined (i.e. the data based on
each key) to live on the same partition. The default implementation of join in Spark is
a shuffled hash join. The shuffled hash join ensures that data on each partition will
contain the same keys by partitioning the second dataset with the same default partitioner
as the first, so that the keys with the same hash value from both datasets are in
the same partition. While this approach always works, it can be more expensive than
necessary because it requires a shuffle. The shuffle can be avoided if:

1. Both RDDs have a known partitioner.
2. One of the datasets is small enough to fit in memory, in which case we can do a
broadcast hash join

*/


def joinScoresWithAddress( scoreRDD : RDD[(Long, Double)],
addressRDD : RDD[(Long, String )]) : RDD[(Long, (Double, String))]= {
//if addressRDD has a known partitioner we should use that,
//otherwise it has a default hash parttioner, which we can reconstrut by getting the umber of
// partitions.
val addressDataPartitioner = addressRDD.partitioner match {
case (Some(p)) => p
case (None) => new HashPartitioner(addressRDD.partitions.length)
}
val bestScoreData = scoreRDD.reduceByKey(addressDataPartitioner, (x, y) => if(x > y) x else y)
bestScoreData.join(addressRDD)
}
