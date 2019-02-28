package com.wyyabout.collectingcomments.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wyyabout.collectingcomments.service.WyyyServiceImpl;

import com.wyyabout.collectingcomments.util.HTTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/wyyAbout")
public class WyyyyController {

    @Autowired
    private WyyyServiceImpl wyyyService;

    //type = 1:  最近一周
    //type= 0： 所有时间
    //获取用户 类型 前每个单曲的前30页评论
    @ResponseBody
    @GetMapping("/musicRecords/{userId}/{type}")
    public void wycomments(@PathVariable("userId") String userId,
                           @PathVariable("type") String type){
        try {
            List<Map<String,String>> songs = new ArrayList<>();
            songs = wyyyService.getUserSongRecords(userId,type);
            for(Map<String,String> song:songs){
                System.out.println("songId : "+song.get("songid"));
                int size = wyyyService.getWyCommentPageSize(song.get("songid"));
                if(size < 100){
                    wyyyService.saveRangePagesOfSongComments(userId,"最近一周排行榜",
                            song.get("songid"),
                            1,
                            size);

                }else{
                    wyyyService.saveRangePagesOfSongComments(userId,"最近一周排行榜",
                            song.get("songid"),
                            1,
                            100);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 抓取一个歌单下的所有歌曲的评论，默认只抓取最大前100页评论
     * @param songListId  歌单id
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/allcommentsfromsonglist/{songList}")
    public void allCommentsFromSongList(@PathVariable("songList") String songListId) throws InterruptedException {
        List<Map<String,String>> songs = wyyyService.getWyyySongList(songListId,null);
        for(Map<String,String> song:songs){
            int size = wyyyService.getWyCommentPageSize(song.get("songid"));
            wyyyService.saveRangePagesOfSongComments(null,songListId,song.get("songid"),1,size);
        }
    }
}
