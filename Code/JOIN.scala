// *******************************************************************

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

// *******************************************************************


// basic RDD Join

def joinScoresWithAddress1( scoreRDD : RDD[(Long, Double)],
addressRDD : RDD[(Long, String )]) : RDD[(Long, (Double, String))]= {
val joinedRDD = scoreRDD.join(addressRDD)
joinedRDD.reduceByKey( (x, y) => if(x._1 > y._1) x else y )
}

/*
However, this is probably not as fast as first reducing the score data, so that the first
dataset contains only one row for each Panda with her best score, and then joining
that data with the address data.
*/

// Pre-filter before join
def joinScoresWithAddress2( scoreRDD : RDD[(Long, Double)],
addressRDD : RDD[(Long, String )]) : RDD[(Long, (Double, String))]= {
val bestScoreData = scoreRDD.reduceByKey((x, y) => if(x > y) x else y)
bestScoreData.join(addressRDD)
}
