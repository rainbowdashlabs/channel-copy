package de.chojo.channelcopy;

import de.chojo.channelcopy.commands.Copy;
import de.chojo.channelcopy.configuration.ConfigFile;
import de.chojo.jdautil.configuratino.Configuration;
import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ChannelCopy {
    public static void main(String[] args) {
        Configuration<ConfigFile> configuration = Configuration.create(new ConfigFile());
        ShardManager build = DefaultShardManagerBuilder.createDefault(configuration.config().general.token())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        InteractionHub.builder(build)
                .withCommands(new Copy())
                .testMode()
                .build();
    }
}
