package de.chojo.channelcopy.commands;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.chojo.channelcopy.configuration.ConfigFile;
import de.chojo.jdautil.configuratino.Configuration;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

public class CopyHandler implements SlashHandler {
    private static final Logger log = getLogger(CopyHandler.class);

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        GuildMessageChannel source = event.getOption("source", OptionMapping::getAsChannel).asGuildMessageChannel();
        GuildChannelUnion target = event.getOption("target", OptionMapping::getAsChannel);

        Webhook channelCopy = target.asTextChannel().createWebhook("ChannelCopy").complete();
        String url = channelCopy.getUrl();

        event.reply("Started").setEphemeral(true).complete();
        JDAWebhookClient client = new WebhookClientBuilder(url).setAllowedMentions(AllowedMentions.none()).buildJDA();

        CompletableFuture.runAsync(() -> {
            long startMessage = event.getOption("start_message", 0, OptionMapping::getAsLong).longValue();
            MessageHistory history;
            if (startMessage == 0) {
                history = source.getHistoryFromBeginning(100).complete();
            } else {
                history = source.getHistoryAfter(startMessage, 100).complete();
            }
            do {
                ArrayList<Message> messages = new ArrayList<>(history.getRetrievedHistory());

                Collections.reverse(messages);
                for (Message message : messages) {
                    long start = System.currentTimeMillis();
                    try {
                        var builder = WebhookMessageBuilder.fromJDA(message)
                                .setAvatarUrl(message.getAuthor().getEffectiveAvatarUrl())
                                .setUsername(Optional.ofNullable(message.getMember()).map(Member::getEffectiveName).orElse(message.getAuthor().getName()));
                        if (!message.getAttachments().isEmpty()) {
                            for (Message.Attachment attachment : message.getAttachments()) {
                                builder.addFile(attachment.getFileName(), attachment.getProxy().download().join());
                            }
                        }
                        client.send(builder.build()).join();
                        Thread.sleep(Duration.ofMillis(Math.max(0, 2100 - System.currentTimeMillis() - start)));
                        log.info("Send message {} from {}", message.getIdLong(), DateTimeFormatter.ISO_DATE_TIME.format(message.getTimeCreated()));
                    } catch (Exception e) {
                        // ignore
                    }
                }
                history = source.getHistoryAfter(history.getRetrievedHistory().stream().mapToLong(ISnowflake::getIdLong).max().orElse(0), 100).complete();
            } while (!history.isEmpty());
        }).whenComplete((res, err) -> {
            log.error("Error: ", err);
        });
    }
}
