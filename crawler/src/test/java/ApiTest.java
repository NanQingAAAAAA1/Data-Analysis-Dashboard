import cn.hutool.core.collection.CollUtil;
import cn.moonshot.util.Message;
import cn.moonshot.util.MoonshotAiUtils;
import cn.moonshot.util.RoleEnum;
import org.junit.Test;


import java.util.List;


public class ApiTest {

    @Test
    public void chat(){
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), "你是kimi AI"),
                new Message(RoleEnum.user.name(), "hello")
        );
        MoonshotAiUtils.chat("moonshot-v1-8k",messages);
    }


}
