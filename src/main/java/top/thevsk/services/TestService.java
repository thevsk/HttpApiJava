package top.thevsk.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import top.thevsk.annotation.BotMessage;
import top.thevsk.annotation.BotService;
import top.thevsk.entity.ApiRequest;
import top.thevsk.entity.ApiResponse;
import top.thevsk.enums.MessageType;
import top.thevsk.utils.CQUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@BotService
public class TestService {

    @BotMessage(messageType = MessageType.GROUP, filter = "startWith:!share")
    public void share(ApiRequest request, ApiResponse response) {
        try {
            Map<String, String> map = parseMap(request.getMessage());
            response.reply(CQUtils.share(getOrEx(map, "url"), getOrEx(map, "title"), getOrEx(map, "content"), getOrEx(map, "image")));
        } catch (Exception e) {
            response.replyAt(e.getMessage());
        }
    }

    @BotMessage(messageType = MessageType.GROUP, filter = "startWith:!getQQ")
    public void getQQ(ApiRequest request, ApiResponse response) {
        String[] strings = CQUtils.getUserIdInCqAtMessage(request.getMessage());
        response.reply(StrKit.join(strings, ","));
    }

    @BotMessage(messageType = MessageType.GROUP, filter = "startWith:!say")
    public void say(ApiRequest request, ApiResponse response) {
        response.reply(request.getMessage());
    }

    @BotMessage(messageType = MessageType.GROUP, filter = "startWith:!acg")
    public void acg(ApiRequest request, ApiResponse response) {
        JSONObject jsonObject = JSON.parseObject((HttpKit.get("http://acg.bakayun.cn/randbg.php?Type=json")));
        response.reply(CQUtils.image(jsonObject.getString("ImgUrl")));
    }

    @BotMessage(messageType = MessageType.GROUP, filter = "startWith:!getUrl")
    public void getUrl(ApiRequest request, ApiResponse response) {
        try {
            response.reply(CQUtils.getUrlInCqImage(request.getMessage().trim())[0]);
        } catch (Exception e) {
            response.replyAt(e.getMessage());
        }
    }

    //    @BotMessage(messageType = MessageType.GROUP, filter = "groupId:326116567")
    @BotMessage(messageType = MessageType.GROUP)
    public void repeat(ApiRequest request, ApiResponse response) {
        if (new Random().nextInt(1000) == 0) {
            response.reply(request.getMessage());
        }
    }

    private Map<String, String> parseMap(String message) {
        Map<String, String> map = new HashMap<>();
        String[] str = message.trim().split(" ");
        for (int i = 0; i < str.length; i++) {
            String m = str[i];
            map.put(m.substring(0, m.indexOf(":")), m.substring(m.indexOf(":") + 1, m.length()));
        }
        return map;
    }

    private String getOrEx(Map<String, String> map, String key) throws Exception {
        if (map == null) throw new Exception("empty map");
        if (map.get(key) == null) throw new Exception("key " + key + " is null");
        return map.get(key);
    }
}
