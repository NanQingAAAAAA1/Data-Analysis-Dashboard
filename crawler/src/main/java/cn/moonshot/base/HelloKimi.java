package cn.moonshot.base;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class HelloKimi {

    private static final String API_KEY = "sk-KaYMtFjI1Qcg4uqt3xVNmUfToZqhJCIgrbosREki1uORFjGY"; // kimi 的 API_KEY
    private static final String MODELS_URL = "https://api.moonshot.cn/v1/chat/completions"; // kimi 的聊天 URL
    private static final String MODEL = "moonshot-v1-8k"; // 模型名称

    public static void main(String[] args) throws IOException {

        HelloKimi helloKimi = new HelloKimi(); // 创建 helloKimi 对象
        helloKimi.chat(); // 调用 chat方法
    }

    public void chat() throws IOException {

        Scanner scanner = new Scanner(System.in); // 获取用户的输入
        OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例

        System.out.println("你好！我是 Kimi，你可以和我聊天。输入 'exit' 结束对话。"); // 欢迎提示

        while (true) {

            System.out.println("----------用户信息----------");
            String userInput = scanner.nextLine().trim(); // 获取用户输入的信息，并存储到 userinput 变量

            if ("exit".equalsIgnoreCase(userInput)) { // 如果输入了 exit 就退出
                System.out.println("再见！");
                break;
            }

            String response = getKimiResponse(client, userInput); // 调用 getKimiResponse 类来获取并输出 kimi 的输出消息
            System.out.println("Kimi: " + response);

        }
        scanner.close();
    }

    public String getKimiResponse(OkHttpClient client, String prompt) throws IOException {

        // 构建消息对象
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONArray messages = new JSONArray();
        messages.add(message);

        // 构造 JSON 请求体
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("model", MODEL);
        requestBodyJson.put("messages", messages);
        requestBodyJson.put("temperature", 0.7);
        requestBodyJson.put("max_tokens", 1024);

        String requestBody = requestBodyJson.toString();

        // 构造 OkHttp 请求
        Request request = new Request.Builder()
                .url(MODELS_URL)
                .post(RequestBody.create(requestBody, MediaType.get("application/json; charset=utf-8")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API 调用失败: " + response.code() + ", 响应: " + response.body().string());
            }

            // 解析 JSON 响应
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            // 提取回复内容
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject messageObj = choice.getJSONObject("message");
//                return messageObj.toString(); // 返回整个json对象
                return messageObj.getStr("content"); // 仅返回 content
            } else {
                return "请重试！";
            }
        }
    }

}
