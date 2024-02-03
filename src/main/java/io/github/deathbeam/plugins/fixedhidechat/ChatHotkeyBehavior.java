package io.github.deathbeam.plugins.fixedhidechat;

import lombok.*;

@RequiredArgsConstructor
public enum ChatHotkeyBehavior {
    SHOW("Show chat"),
    HIDE("Hide chat"),
    TOGGLE("Toggle chat");

    private final String option;
}