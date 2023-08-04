package de.chojo.channelcopy.commands;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.provider.SlashCommand;

public class Copy extends SlashCommand {


    public Copy() {
        super(Slash.of("copy", "Copy")
                .adminCommand()
                .unlocalized()
                .command(new CopyHandler())
                .argument(Argument.channel("source", "channel").asRequired())
                .argument(Argument.channel("target", "target").asRequired())
                .argument(Argument.text("start_message", "oldest message to start with.")));
    }
}
