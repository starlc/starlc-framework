package com.starlc.common.message;

/**
 * 消息发送结果
 */
public class MessageResult {
    private boolean success;          // 是否成功
    private String messageId;         // 消息ID
    private String error;             // 错误信息
    private Object originalResult;    // 原始结果对象

    /**
     * 创建成功结果
     * @return 成功的消息结果
     */
    public static MessageResult success() {
        MessageResult result = new MessageResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * 创建成功结果
     * @param messageId 消息ID
     * @param originalResult 原始结果
     * @return 成功的消息结果
     */
    public static MessageResult success(String messageId, Object originalResult) {
        MessageResult result = new MessageResult();
        result.setSuccess(true);
        result.setMessageId(messageId);
        result.setOriginalResult(originalResult);
        return result;
    }

    /**
     * 创建失败结果
     * @param error 错误信息
     * @return 失败的消息结果
     */
    public static MessageResult failure(String error) {
        MessageResult result = new MessageResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }

    /**
     * 创建失败结果
     * @param throwable 异常
     * @return 失败的消息结果
     */
    public static MessageResult failure(Throwable throwable) {
        return failure(throwable.getMessage());
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public Object getOriginalResult() { return originalResult; }
    public void setOriginalResult(Object originalResult) { this.originalResult = originalResult; }
}