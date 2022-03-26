package se.haxtrams.launchpad.backend.integration.video.player.mplayer;
//command doc: https://github.com/philipl/mplayer/blob/master/DOCS/tech/slave.txt

public enum CLICommands {
    PAUSE("pause"),
    SEEK("seek"),
    SWITCH_AUDIO("switch_audio"),
    SUB_SELECT("sub_select"),
    SUB_VISIBILITY("sub_visibility");


    private final String value;
    CLICommands(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
