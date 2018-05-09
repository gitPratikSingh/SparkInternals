// inefficient
val data = List(1,2,3,4,5,6,7,8,9)
val dataRdd = sc.makeRDD(data)
val maxValue = dataRdd.reduce(_ max _)

//efficient, less shuffling volume
val maxValue = dataRdd.mapPartitions(iter => {iter.reduce(_ max _)}).reduce(_ max _)
