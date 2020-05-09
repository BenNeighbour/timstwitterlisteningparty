package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.twitter.SpotifyUtils
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class SpotifyCommand {

  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("finds an album on spotify with the album title")
  fun spotifyAlbum(@ShellOption("-A", "--album") album: String, @ShellOption("-B", "--band") band: String): String? {
    val album = SpotifyUtils().search(artist = band, album = album)

    if (album != null){
      logger.info("Album href is ${album.spotifyLink} " +
        "and ${album.albumName} and images = ${album.imgLink} and release ${album.releaseDate} and id ${album.spotifyId}")
    }
    return album.toString()
  }


  @ShellMethod("enriches the csv")
  fun spotifyEnrich(): String? {
    val found = SpotifyUtils().enrich()
    logger.info(found)
    return found
  }


}
