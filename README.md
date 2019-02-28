#网易云评论 爬取（分为按歌单id进行爬取和用户 最近一周/全部听歌排行榜 爬取）

因为使用的是收费代理，提交的时候把链接去掉了，所以如果要运行起来的话需要将代理改成你的代理就可以了。


本服务使用的是接口的方式。

接口请求示例：
  http://localhost:8092/wyyAbout/musicRecords/{userId}/{type}
  
  说明：该接口获取用户听歌排行榜前每个单曲的前100页评论
  
  userId  用户id  （可通过查看用户个人信息 url后面的数字就是userId）
  type=1  最近一周
  type=0  所有时间
  

接口请求示例：
  http://localhost:8092/wyyAbout/allcommentsfromsonglist/{songList}
  
  说明：抓取一个歌单下的所有歌曲的评论，默认只抓取最大前100页评论
  
  songList  歌单id
