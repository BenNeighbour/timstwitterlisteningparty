package com.timstwitterlisteningparty.tools.lambda.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.ReplayPHPScript
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.parser.TimeSlotReader
import com.timstwitterlisteningparty.tools.social.TweetUtils
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.time.LocalDateTime

/**
 * Uses the [ReplayPHPScript.readPhpReplayScript] to get the current replays
 * and then checks against the time-slot-data.csv in the s3 bucket and tweets any replays
 * that are missing the s3 time-slot-data.csv but are in the
 */
class S3ReplayTweetGenerator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun tweetReplay(bucketName: String = "timstwitterlisteningparty.com",
                  srcKeyTimeSlots: String = "data/time-slot-data.csv"
  ): String {

    val existingList = getTimeSlots(bucketName, srcKeyTimeSlots)
    val replayMap = ReplayPHPScript().readPhpReplayScript()
    var tweetMsg = ""
    var missingData = ""
    existingList.forEach {
      if (replayMap.containsKey(it.hashBandAlbum())) {
        val replay = replayMap[it.hashBandAlbum()]
        if (replay != null) {
          // a new replay - tell the world
          if (it.replayLink.isEmpty() && replay.fullReplayLink().isNotEmpty()) {
            logger.info("we have a new replay for:  $replay")

            // tweet them about the collection - disabled for now
            // var tweet = TweetUtils().tweetCollection(it, replayId = replay.trimmedId)
            // tweetMsg = tweetMsg.plus("\n").plus(tweet)
            // and the replay
            val tweet = TweetUtils().tweetReplay(it, replayLink = replay.fullReplayLink())
            tweetMsg = tweetMsg.plus("\n").plus(tweet)
            logger.info("tweeted $tweetMsg")
          } else {
            logger.info("Already tweeted about replay for:  $replay")
          }
        }
      } else {
        missingData = missingData.plus("!!!").plus(it)
        logger.info("Didn't find a match in the php for $it")
      }
    }

    // randomly tweet about a tweet if we didn't tweet about a new replay
    // as don't want to clog timeline
    if (tweetMsg.isEmpty()) {
      for (i in 1..3) {
        val timeSlot = existingList.random()
        if (timeSlot.replayId().isNotEmpty()) {
          val tweet = TweetUtils().tweetReplay(timeSlot, timeSlot.replayId())
          tweetMsg = tweetMsg.plus("\n").plus(tweet)
        }
      }
    }


    return "Ok: replayLink tweeted the following replay tweets $tweetMsg !!!! Did not match $missingData !!! : Generated time is :${LocalDateTime.now()} "
  }


  /**
   * Find any completed listening parties and if the album was released today N years ago then
   * tweet about it
   */
  fun tweetAnniversary(bucketName: String = "timstwitterlisteningparty.com",
                       srcKeyTimeSlots: String = "data/time-slot-data.csv"): String {
    val slots = getTimeSlots(bucketName, srcKeyTimeSlots)
    val anythingTweeted = TweetUtils().tweetAnniversary(slots)
    return "anniversary to tweet about = $anythingTweeted"
  }


  private fun getTimeSlots(bucketName: String, srcKeyTimeSlots: String): List<TimeSlot> {
    print("bucket = $bucketName and file = $srcKeyTimeSlots")
    val s3Client = AmazonS3ClientBuilder.defaultClient() as AmazonS3Client
    println("Getting $srcKeyTimeSlots from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyTimeSlots))
    println("Object for $srcKeyTimeSlots from bucket $bucketName is $s3Object")
    val objectData: InputStream = s3Object.objectContent
    return TimeSlotReader(inputStream = objectData).timeSlots
  }

  /**
   * Any listening partys that have hit a year or more anniversary since first happened
   */
  fun tweetYearAnniversary(bucketName: String = "timstwitterlisteningparty.com",
                       srcKeyTimeSlots: String = "data/time-slot-data.csv"): String {
    val slots = getTimeSlots(bucketName, srcKeyTimeSlots)
    val anythingTweeted = TweetUtils().tweetYearlyAnniversary(slots)
    return "yearly anniversary to tweet about = $anythingTweeted"
  }


}
