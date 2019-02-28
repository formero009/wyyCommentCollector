package com.wyyabout.collectingcomments.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wyyabout.collectingcomments.entity.WyyyComment;
import com.wyyabout.collectingcomments.entity.WyyyUser;
import com.wyyabout.collectingcomments.mapper.WyyyCommentMapper;
import com.wyyabout.collectingcomments.mapper.WyyyUserMapper;
import com.wyyabout.collectingcomments.util.HTTPUtils;
import com.wyyabout.collectingcomments.util.WyyyyUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WyyyServiceImpl {

    @Autowired
    private WyyyCommentMapper wyyyCommentMapper;

    @Autowired
    private WyyyUserMapper wyyyUserMapper;


    /**
     * 返回歌曲评论总页数
     * @param songId
     * @return
     */
    public int getWyCommentPageSize(String songId){
        String json = WyyyyUtils.getWyyyComment(songId,1);
        JSONObject result = (JSONObject)JSONObject.parse(json);
        int size = (int) result.get("total");
        double k = size/20.00;
        size = (int) Math.ceil(k);
        System.out.println("这首歌有"+size+"页评论");
        return size;
    }

    /**
     * 获取范围页数内评论数
     * @param songId
     * @param startPage
     * @param endPage
     * @throws InterruptedException
     */

    @Async
    public void saveRangePagesOfSongComments(String masterId,String songListId,String songId,Integer startPage,Integer endPage) throws InterruptedException {
        for(int i=startPage;i<endPage;i++){
            saveSinglePageCommentToDB(masterId,songListId,songId,i);
        }
    }


    /**
     * 保存单页评论到表中
     * @param songListId
     * @param songId
     * @param pageIndex
     */

    public void saveSinglePageCommentToDB(String masterId,String songListId,String songId,int pageIndex){
        String json = WyyyyUtils. getWyyyComment(songId,pageIndex);
        JSONObject result = (JSONObject)JSONObject.parse(json);
        JSONArray comments = (JSONArray) result.get("comments");
        List<WyyyUser> wyyyUsers = new ArrayList<>();
        List<WyyyComment> wyyyComments = new ArrayList<>();
        for(Object comment:comments){
            JSONObject cc = (JSONObject) comment;
            WyyyComment wycomment = new WyyyComment();
            WyyyUser wyuser = new WyyyUser();
            wycomment = cc.toJavaObject(WyyyComment.class);
            System.out.println(wycomment.getContent());
            wyuser = wycomment.getUser();
            wycomment.setUserId(wyuser.getUserId());
            wycomment.setMasterId(masterId);
            wycomment.setUserName(wyuser.getNickname());
            wycomment.setPageIndex(pageIndex);
            wycomment.setSongId(songId);
            wycomment.setSongListId(songListId);
            System.out.println("正在保存歌单id为"+songListId+"中歌曲id为"+songId+"的第"+pageIndex+"页"+wycomment.getCommentId()+"评论 "+wyuser.getNickname()+" ");
            wycomment.setBeReplied(null);
            wyyyUsers.add(wyuser);
            wyyyComments.add(wycomment);
        }
        wyyyUserMapper.insertList(wyyyUsers);
        wyyyCommentMapper.insertList(wyyyComments);
    }

    /**
     * 获得指定歌单List<String>
     * @param songListId   歌单id
     * @return
     */
    @Value("${data.wyy.songlisturl}")
    private String songlisturl;

    public List<Map<String,String>> getWyyySongList(String id,String songlistname){
        System.out.println("正在请求id为"+id+"的歌单获取歌曲id列表");
        List<Map<String,String>> songlist = new ArrayList<>();
        Map<String,String> headers = new HashMap<>();
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        String html = HTTPUtils.getHtmlWithGet(songlisturl+id,headers,null);
        Document doc = Jsoup.parse(html);
        Elements eles = doc.getElementsByClass("f-hide");
        Element listelement = eles.get(0);
        Elements songs = listelement.getElementsByTag("a");
        for(Element song:songs){
            Map<String,String> map = new HashMap<>();
            String songId = song.attr("href");
            String songName = song.text();
            songId = songId.substring(songId.indexOf("id=")+3);
            map.put("songname",songName);
            map.put("songid",songId);
            map.put("songlistname",songlistname);
            songlist.add(map);
        }
        return songlist;
    }

    /**
     * 获得指定用户id的听歌排行榜
     * @param userId    用户id
     * @param type      指定类型 ： 1最近一周  0所有时间
     * @return
     */
    public List<Map<String,String>> getUserSongRecords(String userId,String type){
        if(type.equals("0"))
            System.out.println("looking for user:"+userId+" alltime songrecords");
        if(type.equals("1"))
            System.out.println("looking for user:"+userId+" weeklly songrecords");

        String realtype = "-1";
        String result = WyyyyUtils.getSongRecord(userId,realtype);
        List<Map<String,String>> songs = new ArrayList<>();
        JSONObject json = (JSONObject) JSONObject.parse(result);
        JSONArray jsons = null;
        if(type.equals("1")){
            jsons = (JSONArray) json.get("weekData");
        }
        if(type.equals("0")){
            jsons = (JSONArray) json.get("allData");
        }
        for(Object j:jsons){
            String pppp = String.valueOf(j);
            JSONObject temp = (JSONObject) JSONObject.parse(pppp);
            String songinfo = String.valueOf(temp.get("song"));
            JSONObject kkk = (JSONObject) JSONObject.parse(songinfo);
            Map<String,String> map = new HashMap<>();
            String songid = String.valueOf(kkk.get("id"));
            String songname = String.valueOf(kkk.get("name"));
            map.put("songname",songname);
            map.put("songid",songid);
            if(type.equals("0")){
                map.put("songlistname","allData song records");
            }
            if(type.equals("1")){
                map.put("songlistname","weekData song records");
            }
            songs.add(map);
        }
        return songs;
    }

    /**
     * 获得指定用户id的所有歌单id 集合
     * @param userId
     * @return
     */
    public Map<String,String> getSpecificUserSongList(String userId){
        Map<String,String> map = new HashMap<>();
        String html = WyyyyUtils.getSongList(userId);
        JSONObject json = (JSONObject) JSONObject.parse(html);
        JSONArray playlists = (JSONArray) json.get("playlist");
        for(Object play:playlists){
            JSONObject p = (JSONObject) play;
            String listid = p.get("id").toString();
            String listname = p.get("name").toString();
            if(listid.equals("67289456"))continue;
            map.put(listid,listname);
            System.out.println("user "+userId+" songlist id:"+listid+" name:"+listname+" added");
        }
        return map;
    }

    /**
     * 比较获得交集
     * @param userId1   主用户，为全量，以此进行对比
     * @param userId2   辅用户，目标用户,仅匹配每周量
     */
    public List<Map<String,String>> compareTwoUserMatched(String userId1,String userId2,String type){
        List<Map<String,String>> userWeeklyRecords1 = getUserSongRecords(userId1,type);
        List<Map<String,String>> userWeeklyRecords2 = getUserSongRecords(userId2,type);
        List<Map<String,String>> result = new ArrayList<>();
        Map<String,String> userListIds = getSpecificUserSongList(userId1);
        Set<Map<String,String>> allRecordsOfUser1 = new HashSet<>();
        for(String id:userListIds.keySet()){
            List<Map<String,String>> songlistids = getWyyySongList(id,userListIds.get(id));
            allRecordsOfUser1.addAll(songlistids);
        }
        List<String> jiaojiRecords = new ArrayList<>();
        allRecordsOfUser1.addAll(userWeeklyRecords1);
        List<String> User1SongIds = new ArrayList<>();
        List<String> User2SongIds = new ArrayList<>();
        for(Map<String,String> record:userWeeklyRecords2){
            User2SongIds.add(record.get("songid"));
        }
        for(Map<String,String> record:allRecordsOfUser1){
            User1SongIds.add(record.get("songid"));
        }
        for(String id:User2SongIds){
            if(User1SongIds.contains(id))
                jiaojiRecords.add(id);
        }
        System.out.println("the jiaoji of two user id calced");
        for(Map<String,String> record:allRecordsOfUser1){
            for(String jiaoji:jiaojiRecords){
                if(record.get("songid").equals(jiaoji))
                    result.add(record);
            }
        }


        return result;
    }
}