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
