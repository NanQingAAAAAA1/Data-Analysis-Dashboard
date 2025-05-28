package cn.moonshot.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import okhttp3.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MoonshotAiChatUtils {

    private static final String API_KEY = "sk-KaYMtFjI1Qcg4uqt3xVNmUfToZqhJCIgrbosREki1uORFjGY";
    private static final String MODELS_URL = "https://api.moonshot.cn/v1/models";
    private static final String FILES_URL = "https://api.moonshot.cn/v1/files";
    private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";
    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";

    // 模型输出方法
    public static String generateContentFromPrompt(String model, List<Message> messages) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    // 如果分析量太大，AI生成的可能会比较久，如果没有设置时间，就会报错
                    .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为30秒
                    .writeTimeout(30, TimeUnit.SECONDS)   // 设置写入超时时间为30秒
                    .readTimeout(30, TimeUnit.SECONDS) // 设置读取超时时间为30秒
                    .build();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", model);
            jsonBody.put("messages", new JSONArray(messages));
            jsonBody.put("stream", false); // 关闭流式输出

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8") // // 设置请求体格式为JSON
            );

            Request request = new Request.Builder()
                    .url(CHAT_COMPLETION_URL)
                    .post(body) // // 设置请求方法为POST + 请求体
                    .addHeader("Authorization", "Bearer " + API_KEY) // 授权头
                    .build();

            Response response = client.newCall(request).execute(); // 发起请求并等待响应

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);

                JSONArray choices = jsonResponse.getJSONArray("choices");  // 解析响应中的"choices"数组
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject messageObj = choice.getJSONObject("message");
                    return messageObj.getStr("content"); // 返回 content 内容
                }
            } else {
                System.err.println("API 请求失败: " + response.code());
                System.err.println("响应体: " + response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "AI 回答生成失败，请检查网络或 API 配置。";
    }
}
