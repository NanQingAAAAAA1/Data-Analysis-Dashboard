package cn.moonshot.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.Optional;


public class MoonshotAiUtils {

    private static final String API_KEY = "sk-KaYMtFjI1Qcg4uqt3xVNmUfToZqhJCIgrbosREki1uORFjGY";
    private static final String MODELS_URL = "https://api.moonshot.cn/v1/models";
    private static final String FILES_URL = "https://api.moonshot.cn/v1/files";
    private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";
    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";

    // 获取模型列表的方法
    public static String getModelList() {
        // 向 MODELS_URL 发送 GET 请求
        return getCommonRequest(MODELS_URL)
                .execute()
                .body();
    }

    // 上传文件方法
    public static String uploadFile(@NonNull File file) {
        return getCommonRequest(FILES_URL)
                .method(Method.POST) // 请求方法 POST
                .header("purpose", "file-extract") // 自定义头部：用途为提取文件内容
                .form("file", file) // 添加表单字段，键为 "file"，值为传入的文件
                .execute()
                .body(); // 返回响应体
    }

    // 获取文件列表
    public static String getFileList() {
        return getCommonRequest(FILES_URL)
                .execute()
                .body();
    }

    // 删除指定 ID 的文件
    public static String deleteFile(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId) // 拼接文件 URL
                .method(Method.DELETE) // 使用 DELETE 方法删除文件
                .execute()
                .body();
    }

    // 获取指定文件的详情信息
    public static String getFileDetail(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId)
                .execute()
                .body();
    }

    // 获取指定文件的内容
    public static String getFileContent(@NonNull String fileId) {
        return getCommonRequest(FILES_URL + "/" + fileId + "/content")
                .execute()
                .body();
    }

    // 预估消息所需的 token 数量
    public static String estimateTokenCount(@NonNull String model, @NonNull List<Message> messages) {

        // 构造 JSON 请求体，包含模型名称和消息列表
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .toString();

        return getCommonRequest(ESTIMATE_TOKEN_COUNT_URL)
                .method(Method.POST)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue()) // 设置 Content-Type 为 JSON
                .body(requestBody) // 设置请求体
                .execute()
                .body();
    }

    @SneakyThrows
    public static void chat(@NonNull String model, @NonNull List<Message> messages) {

        // 构造 JSON 请求体，设置 stream 为 true 表示启用流式响应
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .putOpt("stream", true)
                .toString();

        // 使用 OkHttp 构建请求
        Request okhttpRequest = new Request.Builder()
                .url(CHAT_COMPLETION_URL) // 请求地址
                .post(RequestBody.create(requestBody, MediaType.get(ContentType.JSON.getValue()))) // 设置请求体和类型
                .addHeader("Authorization", "Bearer " + API_KEY) // 添加鉴权头
                .build();

        Call call = new OkHttpClient().newCall(okhttpRequest); // 创建调用对象
        Response okhttpResponse = call.execute(); // 执行请求

        // 读取流式响应
        BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
        String line;
        while ((line = reader.readLine()) != null) {
            if (StrUtil.isBlank(line)) {
                continue; // 跳过空行
            }
            if (JSONUtil.isTypeJSON(line)) { // 如果是 JSON 格式
                Optional.of(JSONUtil.parseObj(line))
                        .map(x -> x.getJSONObject("error")) // 提取 error 字段
                        .map(x -> x.getStr("message")) // 提取错误信息
                        .ifPresent(x -> System.out.println("error: " + x)); // 输出错误
                return;
            }
            line = StrUtil.replace(line, "data: ", StrUtil.EMPTY); // 去除前缀
            // 判断是否结束或非法数据
            if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
                return;
            }

            // 解析并输出流式返回的内容
            Optional.of(JSONUtil.parseObj(line))
                    .map(x -> x.getJSONArray("choices")) // 提取 choices 数组
                    .filter(CollUtil::isNotEmpty) // 确保数组非空
                    .map(x -> (JSONObject) x.get(0)) // 取第一个元素
                    .map(x -> x.getJSONObject("delta")) // 提取 delta 对象
                    .map(x -> x.getStr("content")) // 提取 content 字段
                    .ifPresent(x -> System.out.println("rowData: " + x)); // 输出内容
        }
    }

    // 构建带有认证头的通用请求对象
    private static HttpRequest getCommonRequest(@NonNull String url) {
        return HttpRequest.of(url).header(Header.AUTHORIZATION, "Bearer " + API_KEY);
    }



}