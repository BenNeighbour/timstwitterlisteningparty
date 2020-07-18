<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  })
</script>
<section class="post">
  <div class="container-fluid">
    <div class="card d mb-3" style="width: 100%;">
      <div class="card-header font-weight-bold">
        <i class="fas fa-calendar-day"></i> Upcoming Events - Dates to be confirmed
      </div>
      <#list tbc_list as slot>
        <div class="card-body">
          <table style="width: 100%;">
            <tr>
              <td style="width:35%;text-align:left" class="font-weight-light"><a href="${slot.spotifyLink}"
                                                                                 target="_blank">
                  <img alt="album image" src="${slot.spotifyImgLink}" data-toggle="tooltip" data-placement="top"
                       title="${slot.album}

              <#if slot.isActuallySpotifyLink() >
                Spotify album link   "
                    <#else>
                       link"
                  </#if>

                  " alt="album" style="width:80px;height:80px;"></a><br>
                <hr style="width:80px;margin-left:0;">
                TBC
              </td>
              <td style="width:50%;text-align:left;">
                <b>${slot.band}</b><br/>${slot.album}
              </td>
              <td style="width:15%"><a class="pure-button pure-button-active"
                                       href="${slot.link}" target="_blank"><i
                          class="fab fa-twitter-square"></i></a></td>
            </tr>
          </table>
          <hr/>
        </div>
      </#list>
      <hr/>
    </div>
  </div>
</section>
