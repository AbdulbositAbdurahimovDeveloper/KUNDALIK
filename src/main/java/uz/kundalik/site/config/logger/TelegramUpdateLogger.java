package uz.kundalik.site.config.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * A dedicated service for logging incoming Telegram {@link Update} objects in a structured,
 * color-coded, and human-readable format.
 * <p>
 * This logger is designed to be used during development and debugging to provide clear
 * insight into the data received from the Telegram Bot API. It handles common update
 * types such as text messages, callback queries, and bot status changes (e.g., when a user
 * blocks or starts the bot).
 * <p>
 * ANSI escape codes are used to colorize the console output, making it easier to distinguish
 * different parts of the log message.
 *
 * @see Update
 */
@Service
public class TelegramUpdateLogger {

    private static final Logger log = LoggerFactory.getLogger(TelegramUpdateLogger.class);

    // ANSI color codes for rich console formatting
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";

    /**
     * Logs the details of a received Telegram {@link Update} to the console.
     * <p>
     * The method identifies the type of the update (e.g., text message, button click)
     * and constructs a detailed, multi-line log entry. The format includes:
     * <ul>
     *   <li>Update Type: A clear description of the event.</li>
     *   <li>User Info: The user's first name, ID, and username.</li>
     *   <li>Chat ID: The identifier of the chat where the event occurred.</li>
     *   <li>Content: The actual text or callback data of the update.</li>
     * </ul>
     * Unhandled update types are logged with a generic message.
     *
     * @param update The {@link Update} object received from the Telegram Bot API.
     */
    public void logUpdate(Update update) {
        if (update == null) {
            log.warn("Received a null update object.");
            return;
        }

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n")
                .append(ANSI_GREEN).append("================ INCOMING TELEGRAM UPDATE ================").append(ANSI_RESET)
                .append("\n");

        if (update.hasMessage() && update.getMessage().hasText()) {
            buildTextMessageLog(logBuilder, update);
        } else if (update.hasCallbackQuery()) {
            buildCallbackQueryLog(logBuilder, update);
        } else if (update.hasMyChatMember()) {
            buildChatMemberLog(logBuilder, update);
        } else {
            buildUnknownUpdateLog(logBuilder, update);
        }

        logBuilder.append(ANSI_GREEN).append("========================================================").append(ANSI_RESET);
        log.info(logBuilder.toString());
    }

    /**
     * Appends formatted log details for a text message update.
     *
     * @param builder The StringBuilder to append to.
     * @param update  The incoming update, guaranteed to contain a text message.
     */
    private void buildTextMessageLog(StringBuilder builder, Update update) {
        User from = update.getMessage().getFrom();
        String text = update.getMessage().getText();

        builder.append(formatLine("Type", "Text Message", ANSI_CYAN));
        builder.append(formatLine("User", formatUserInfo(from), ANSI_YELLOW));
        builder.append(formatLine("Chat ID", String.valueOf(update.getMessage().getChatId()), ANSI_YELLOW));
        builder.append(formatLine("Content", String.format("\"%s\"", text), ANSI_YELLOW));
    }

    /**
     * Appends formatted log details for a callback query (inline button click) update.
     *
     * @param builder The StringBuilder to append to.
     * @param update  The incoming update, guaranteed to contain a callback query.
     */
    private void buildCallbackQueryLog(StringBuilder builder, Update update) {
        User from = update.getCallbackQuery().getFrom();
        String data = update.getCallbackQuery().getData();

        builder.append(formatLine("Type", "Callback Query (Button Click)", ANSI_CYAN));
        builder.append(formatLine("User", formatUserInfo(from), ANSI_YELLOW));
        builder.append(formatLine("Chat ID", String.valueOf(update.getCallbackQuery().getMessage().getChatId()), ANSI_YELLOW));
        builder.append(formatLine("Data", String.format("\"%s\"", data), ANSI_YELLOW));
    }

    /**
     * Appends formatted log details for a chat member status update concerning the bot itself.
     * This is typically triggered when a user starts, blocks, or unblocks the bot.
     *
     * @param builder The StringBuilder to append to.
     * @param update  The incoming update, guaranteed to contain a `my_chat_member` update.
     */
    private void buildChatMemberLog(StringBuilder builder, Update update) {
        ChatMemberUpdated chatMember = update.getMyChatMember();
        User from = chatMember.getFrom();
        String oldStatus = chatMember.getOldChatMember().getStatus();
        String newStatus = chatMember.getNewChatMember().getStatus();

        builder.append(formatLine("Type", "Bot Status Change (my_chat_member)", ANSI_CYAN));
        builder.append(formatLine("User", formatUserInfo(from), ANSI_YELLOW));
        builder.append(formatLine("Chat ID", String.valueOf(chatMember.getChat().getId()), ANSI_YELLOW));

        String statusChange = String.format("%s -> %s", oldStatus, formatNewStatus(newStatus));
        builder.append(formatLine("Status", statusChange, ANSI_BLUE));
    }

    /**
     * Appends a generic log message for update types that are not explicitly handled.
     *
     * @param builder The StringBuilder to append to.
     * @param update  The unhandled update.
     */
    private void buildUnknownUpdateLog(StringBuilder builder, Update update) {
        builder.append(formatLine("Type", "Unsupported or Unknown Update Type", ANSI_CYAN));
        builder.append(formatLine("Update ID", String.valueOf(update.getUpdateId()), ANSI_YELLOW));
    }

    /**
     * Helper method to format a standard log line with a key and a value.
     *
     * @param key   The label for the log entry (e.g., "User", "Content").
     * @param value The value for the log entry.
     * @param color The ANSI color code for the key.
     * @return A formatted string for a single log line.
     */
    private String formatLine(String key, String value, String color) {
        return String.format("%s%-10s: %s%s\n", color, key, ANSI_RESET, value);
    }

    /**
     * Helper method to format user information into a consistent string.
     *
     * @param user The {@link User} object from the update.
     * @return A formatted string like "John Doe (ID: 123, Username: @john doe)".
     */
    private String formatUserInfo(User user) {
        if (user == null) {
            return "N/A";
        }
        String username = user.getUserName() != null ? "@" + user.getUserName() : "N/A";
        return String.format("%s %s (ID: %d, Username: %s)",
                user.getFirstName(),
                user.getLastName() != null ? user.getLastName() : "",
                user.getId(),
                username
        ).trim();
    }

    /**
     * Helper method to colorize the new status in a chat member update.
     *
     * @param newStatus The new status string (e.g., "kicked", "member").
     * @return A color-coded status string.
     */
    private String formatNewStatus(String newStatus) {
        return switch (newStatus) {
            case "kicked" -> String.format("%s%s%s (Bot Blocked)", ANSI_RED, newStatus.toUpperCase(), ANSI_RESET);
            case "member" ->
                    String.format("%s%s%s (Bot Started/Unblocked)", ANSI_GREEN, newStatus.toUpperCase(), ANSI_RESET);
            default -> newStatus;
        };
    }
}